/*
 * ContactEdit.java
 *
 * Created on 26.05.2008, 10:04
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */
package client;

//#ifndef WMUC
import conference.MucContact;
//#endif
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
import vcard.VCard;

/**
 *
 * @author Evg_S
 */

public final class ContactEdit extends DefForm {
    private static String lastContactJID = "";

    private TextInput tJid;
    private TextInput tNick;
    private TextInput tGroup;
    private DropChoiceBox tGrpList;
    private DropChoiceBox tTranspList;
    private CheckBox tAskSubscrCheckBox;

    int newGroupPos = 0;
    boolean newContact = true;
    private boolean newGroup;

    public ContactEdit(Contact c) {
        super(SR.get(SR.MS_ADD_CONTACT));

        tJid = new TextInput(SR.get(SR.MS_USER_JID), lastContactJID, TextField.ANY);

        tNick = new TextInput(SR.get(SR.MS_NAME), null, null, TextField.ANY);

        tGroup = new TextInput(SR.get(SR.MS_NEWGROUP), (c == null) ? "" : c.group.name, null, TextField.ANY);

        tTranspList = new DropChoiceBox(SR.get(SR.MS_TRANSPORT));
        // Transport droplist
        tTranspList.append(new Contact(BombusQD.sd.account.getServer(), BombusQD.sd.account.getServer(), 0, "both"));
        for (Enumeration e = BombusQD.sd.roster.getHContacts().elements(); e.hasMoreElements();) {
            Contact ct = (Contact)e.nextElement();
            Jid transpJid = ct.jid;
            if (transpJid.isTransport()) {
                tTranspList.append(ct);
            }
        }
        tTranspList.append(SR.get(SR.MS_OTHER));
        tTranspList.setSelectedIndex(tTranspList.size() - 1);

        tAskSubscrCheckBox = new CheckBox(SR.get(SR.MS_ASK_SUBSCRIPTION), false);

        try {
            String jid;
//#ifndef WMUC
            if (c instanceof MucContact) {
                jid = Jid.toBareJid(((MucContact)c).realJid);
            } else {
//#endif
                jid = c.bareJid;
//#ifndef WMUC
            }
//#endif
            // edit contact
            tJid.setValue(jid);
            tNick.setValue(c.getNick());
//#ifndef WMUC
            if (c instanceof MucContact) {
                c = null;
                throw new Exception();
            }
//#endif
            if (c.getGroupType() != Groups.TYPE_NOT_IN_LIST && c.getGroupType() != Groups.TYPE_SEARCH_RESULT) {
                // edit contact
                getMainBarItem().setElementAt(jid, 0);
                newContact = false;
            } else {
                c = null; // adding not-in-list
            }
        } catch (Exception e) {
            c = null;
        } // if MucContact does not contains realJid

        String grpName = "";
        if (c != null) {
            grpName = c.group.name;
        }

        Vector groups = BombusQD.sd.roster.contactList.groups.getRosterGroupNames();
        if (groups != null) {
            tGrpList = new DropChoiceBox(SR.get(SR.MS_GROUP));
            for (int i = 0; i < groups.size(); i++) {
                String gn = (String)groups.elementAt(i);
                tGrpList.append(gn);

                if (gn.equals(grpName)) {
                    tGrpList.setSelectedIndex(i);
                }
            }
        }

        if (c == null) {
            addControl(tJid);
            addControl(tTranspList);
        }
        addControl(tNick);

        tGrpList.append(SR.get(SR.MS_NEWGROUP));
        addControl(tGrpList);

        newGroupPos = itemsList.indexOf(tGrpList) + 1;


        if (newContact) {
            addControl(new SimpleString(SR.get(SR.MS_SUBSCRIPTION), true));
            addControl(tAskSubscrCheckBox);

            addControl(new LinkString(SR.get(SR.MS_VCARD)) {
                public void doAction() {
                    requestVCard();
                }

            });
        }

    }

    private void requestVCard() {
        String jid = tJid.getValue();
        if (jid.length() > 0) {
            lastContactJID = jid;
            VCard.request(jid, jid);
        }
    }

    public void cmdOk() {
        String jid = tJid.getValue().trim().toLowerCase();
        if (jid != null) {
            String name = tNick.getValue();

            String group;
            int index = tGrpList.getSelectedIndex();
            // General group is the first one;
            if (index == 0) {
                group = null;
            } else if (index == tGrpList.size() - 1) {
                group = tGroup.getValue();
            } else {
                group = (String)tGrpList.items.elementAt(index);
            }

            boolean ask = tAskSubscrCheckBox.getValue();

            int at = jid.indexOf('@');
            if (at < 0 && tTranspList.getSelectedIndex() != tTranspList.size() - 1) {
                StringBuffer jidBuf = new StringBuffer(jid);
                at = jid.length();
                jidBuf.setLength(at);
                jidBuf.append('@').append(tTranspList.items.elementAt(tTranspList.getSelectedIndex()).toString());
                jid = jidBuf.toString();
            }
            if (!new Jid(jid).getBareJid().equals(StaticData.getInstance().roster.selfContact().bareJid)) {
                BombusQD.sd.roster.storeContact(jid, name, group, ask);
            }
            destroyView();
        }
    }

    protected void beginPaint() {
        if (tGrpList != null) {
            if (tGrpList.toString().equals(SR.get(SR.MS_NEWGROUP))) {
                if (!newGroup) {
                    insertControl(tGroup, newGroupPos);
                    newGroup = true;
                }
            } else {
                if (newGroup) {
                    removeControl(tGroup);
                    newGroup = false;
                }
            }
        }
    }
}
