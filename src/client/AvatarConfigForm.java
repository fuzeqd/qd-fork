/*
 * ConfigAvatar.java
 *
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

//#ifdef AVATARS
package client;

import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SpacerItem;
import javax.microedition.lcdui.Image;
import ui.controls.form.LinkString;
import java.util.Vector;
//#ifdef FILE_IO
import io.file.FileIO;
import ui.controls.form.PathSelector;
//#endif
import util.StringUtils;

import ui.controls.form.SimpleString;
//#ifdef FILE_IO  
    class AvatarsUpdater implements Runnable {
        
        private static boolean inProgress = false;
        private AvatarConfigForm form;
        public AvatarsUpdater(AvatarConfigForm form) {
            this.form = form;
        }
        public void run() {
            if (inProgress) return;
            
            form.addControl(new SpacerItem(10));
            SimpleString progress = new SimpleString("Please wait, loading...", false);
            form.addControl(progress);
            form.redraw();
            inProgress = true;
            long s1 = System.currentTimeMillis();
            int loadingAvatars_roster = applyAvatars(true);
            int loadingAvatars_muc = applyAvatars(false);
            UpdateAvatarsOnline();

            if (loadingAvatars_roster > -1 && loadingAvatars_muc > -1) {
                long s2 = System.currentTimeMillis();
                form.removeControl(progress);
                form.addControl(new SimpleString("Update Success!", true));
                form.addControl(new SimpleString("Time: " + Long.toString(s2 - s1) + " msec", true));
                form.addControl(new SpacerItem(10));
                form.redraw();
            }
            inProgress = false;
        }
        
        public int applyAvatars(boolean isRoster) {
            Contact c = null;
            int countLoadedImages = 0;
            try {
                FileIO f = FileIO.createConnection(Config.getInstance().msgAvatarPath);
                Vector e = f.fileList(false);
                int size1 = e.size() - 1;
                int size = midlet.BombusQD.sd.roster.contactList.contacts.size();

                for (int i = 0; i < size1; i++) {
                    for (int j = 0; j < size; j++) {
                        c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(j);
                        if (isRoster) {
                            String checkBareJid = StringUtils.replaceBadChars("roster_" + c.bareJid);
                            String fsName = e.elementAt(i).toString();
                            if (fsName.startsWith("roster") && fsName.indexOf(checkBareJid) > -1) {
                                if (createContactImageFS(c, fsName)) {
                                    countLoadedImages += 1;
                                    checkBareJid = null;
                                    fsName = null;
                                    Thread.sleep(50);
                                    break;
                                }
                            }
                        } else {
                            String checkNick = StringUtils.replaceBadChars("muc_" + c.getNick());
                            String fsName = e.elementAt(i).toString();
                            if (fsName.startsWith("muc") && fsName.indexOf(checkNick) > -1) {
                                if (createContactImageFS(c, fsName)) {
                                    countLoadedImages += 1;
                                    checkNick = null;
                                    fsName = null;
                                    Thread.sleep(50);
                                    break;
                                }
                            }
                        }
                    }
                }
                return countLoadedImages;
            } catch (Exception e) {
            }
            return -1;
        }

        private boolean createContactImageFS(Contact c, String name) {
            Image photoImg = null;
            byte[] b;
            int len = 0;

            try {
                FileIO f = FileIO.createConnection(Config.getInstance().msgAvatarPath + name);
                b = f.fileRead();

                len = b.length;
                if (!c.hasPhoto) {
                    try {
                        photoImg = Image.createImage(b, 0, len);
                        c.setImageAvatar(photoImg);
                    } catch (OutOfMemoryError eom) {
                        //StaticData.getInstance().roster.errorLog("createContactImage: OutOfMemoryError " + c.getJid());
                    } catch (Exception e) {
                        //StaticData.getInstance().roster.errorLog("createContactImage: Exception " + c.getJid());
                    }
                }
                f.close();
            } catch (Exception e) {
            }
            return true;
        }

        public void UpdateAvatarsOnline() {
            Image photoImg = null;
            int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
            Contact c = null;
            for (int i = 0; i < size; i++) {
                c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                if (c.hasPhoto) {
                    try {
                        photoImg = Image.createImage(c.vcard.getPhoto(), 0, c.vcard.getPhoto().length);
                        c.setImageAvatar(photoImg);
                    } catch (OutOfMemoryError eom) {
                        //StaticData.getInstance().roster.errorLog("UpdateAvatars_menu: OutOfMemoryError "+c.getJid());
                    } catch (Exception e) {
                        //StaticData.getInstance().roster.errorLog("UpdateAvatars_menu: Exception load vcard "+c.getJid());
                    }
                }
            }
        }

    }
//#endif


public class AvatarConfigForm extends DefForm {
    private NumberInput maxAvatarHeight;
    private NumberInput maxAvatarWidth;

    private CheckBox auto_queryPhoto;
    private CheckBox showAvatarRect;

//#ifdef FILE_IO
    private CheckBox autoload_FSPhoto;
    private CheckBox autoSaveVcard;
    private PathSelector avatarFolder;
//#endif
    private Config config;
    public AvatarConfigForm() {
        super(SR.get(SR.MS_AVATARS));

        config = Config.getInstance();

        if (config.userAppLevel == 1) {
            auto_queryPhoto = new CheckBox(SR.get(SR.MS_AUTOLOAD_VCARD), config.auto_queryPhoto);
            addControl(auto_queryPhoto);

//#ifdef FILE_IO
            autoload_FSPhoto = new CheckBox(SR.get(SR.MS_AUTOLOAD_VCARD_FROMFS), config.autoload_FSPhoto);
            addControl(autoload_FSPhoto);
//#endif
            showAvatarRect = new CheckBox(SR.get(SR.AVATAR_DRAW_RECT), config.showAvatarRect);
            addControl(showAvatarRect);
            addControl(new SpacerItem(5));
        }

        maxAvatarHeight = new NumberInput(SR.get(SR.MS_MAX_AVATAR_HEIGHT), config.maxAvatarHeight, 12, 100);
        addControl(maxAvatarHeight);

        addControl(new SpacerItem(5));
        maxAvatarWidth = new NumberInput(SR.get(SR.MS_MAX_AVATAR_WIDTH), config.maxAvatarWidth, 12, 100);
        addControl(maxAvatarWidth);

//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            addControl(new SpacerItem(10));
            autoSaveVcard = new CheckBox(SR.get(SR.AVATAR_AUTOSAVE_FS), config.autoSaveVcard);
            avatarFolder = new PathSelector(SR.get(SR.AVATAR_FOLDER), config.msgAvatarPath, PathSelector.TYPE_DIR);
            addControl(autoSaveVcard);
            addControl(avatarFolder);
        }
//#endif

//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            final AvatarConfigForm form = this;
            addControl(new LinkString(SR.get(SR.MS_UPDATE)) {
                public void doAction() {
                    config.maxAvatarHeight = maxAvatarHeight.getIntValue();
                    config.maxAvatarWidth = maxAvatarWidth.getIntValue();
                    new Thread(new AvatarsUpdater(form)).start();
                }

            });
        }
//#endif
    }

    public void cmdOk() {
        if (config.userAppLevel == 1) {
            config.auto_queryPhoto = auto_queryPhoto.getValue();
//#ifdef FILE_IO
            config.autoload_FSPhoto = autoload_FSPhoto.getValue();
//#endif
            config.showAvatarRect = showAvatarRect.getValue();
        }

        int maxAvHeight = maxAvatarHeight.getIntValue();
        int maxAvWidth = maxAvatarWidth.getIntValue();

        if (maxAvHeight != config.maxAvatarHeight || maxAvWidth != config.maxAvatarWidth) {
            config.maxAvatarHeight = maxAvHeight;
            config.maxAvatarWidth = maxAvWidth;
        }
//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            config.autoSaveVcard = autoSaveVcard.getValue();
            config.msgAvatarPath = avatarFolder.getValue();
        }
//#endif

        destroyView();
    }
}

//#endif
