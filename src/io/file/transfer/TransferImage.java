/*
 * TransferImage.java
 *
 * Created on 7.08.2008, 23:47
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

//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
package io.file.transfer;

import client.Contact;
import images.ActionsIcons;
import images.camera.CameraImage;
import images.camera.CameraImageListener;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import locale.SR;
import midlet.BombusQD;
import ui.IconTextElement;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.ImageItem;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;
import util.Time;

/**
 *
 * @author ad
 */
public class TransferImage extends DefForm implements CameraImageListener {

    private String to;
    private byte[] photo;

    private ImageItem photoItem;
    private LinkString shot;
    private TextInput description;
    private DropChoiceBox method;

    public TransferImage(String recipientJid) {
        super(SR.get(SR.MS_SEND_PHOTO));
        this.to=recipientJid;
        
        Contact c = BombusQD.sd.roster.getContact(recipientJid, true);

        itemsList.addElement(new SimpleString(recipientJid, false));

        shot=new LinkString(SR.get(SR.MS_CAMERASHOT)) { public void doAction() { initCamera(); } };
        itemsList.addElement(shot);

        description = new TextInput(SR.get(SR.MS_DESCRIPTION), null, TextField.ANY);
        itemsList.addElement(description);
        
        method = new DropChoiceBox(SR.get(SR.MS_SEND_METHOD));
        method.append(new IconTextElement("IBB", ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_FILE));
        method.append(new IconTextElement("Jimm aspro", ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_FILE));
        if (c.origin != Contact.ORIGIN_GROUPCHAT) {
            method.setSelectedIndex(TransferTask.METHOD_IBB);
            addControl(method);
        } else {
            method.setSelectedIndex(TransferTask.METHOD_ASPRO);            
        }
        
        moveCursorTo(1);
    }

    public void initCamera() {
        new CameraImage(this);
    }

    public void cameraImageNotify(byte[] capturedPhoto) {
        this.photo=capturedPhoto;
        try {
            itemsList.removeElement(photoItem);
            Image photoImg=Image.createImage(photo, 0, photo.length);
            photoItem=new ImageItem(photoImg, photo.length+" bytes");
            itemsList.addElement(photoItem);
        } catch (Exception e) { }
    }

    public void cmdOk() {
        try {
            TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), "photo_" + Time.localDate() + "_" + Time.photoTimeStamp() + ".png", description.getValue(), true, photo, method.index);
            TransferDispatcher.getInstance().sendFile(task);
            //switch to file transfer manager
            destroyView();
            photo=null;
            //return;
        } catch (Exception e) { photo=null; }
    }
}
//#endif
//#endif

