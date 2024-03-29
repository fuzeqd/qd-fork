/*
 * SimpleItemChat.java
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
//#ifdef CLASSIC_CHAT
//# package client;
//# 
//# import conference.AppendNickForm;
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.lcdui.Form;
//# import javax.microedition.lcdui.TextField;
//# import midlet.BombusQD;
//# import util.StringUtils;
//# 
//# public class SimpleItemChat implements CommandListener {
//#     private Form form;
//# 
//#     public TextField txtField;
//#     private Command cmdSend;
//#     private Command cmdInsNick;
//#     private Command cmdInsMe;
//#     private Command cmdCancel;
//#     Contact contact;
//#     
//#     public TextField msgTF;
//#     public ClassicChat scroller;
//#     private static int width;
//# 
//#     public SimpleItemChat(Contact contact) {
//#         this.contact = contact;
//# 
//#         StaticData.getInstance().roster.activeContact = contact;
//#         contact.setIncoming(0);
//# 
//#         if (Config.swapSendAndSuspend) {
//#             cmdSend = new Command(locale.SR.get(locale.SR.MS_SEND), Command.BACK, 1);
//#             cmdCancel = new Command(locale.SR.get(locale.SR.MS_BACK), Command.SCREEN, 2);
//#         } else {
//#             cmdSend = new Command(locale.SR.get(locale.SR.MS_SEND), Command.SCREEN, 1);
//#             cmdCancel = new Command(locale.SR.get(locale.SR.MS_BACK), Command.BACK, 2);
//#         }
//# 
//#         cmdInsNick = new Command(locale.SR.get(locale.SR.MS_NICKNAMES), Command.SCREEN, 6);
//#         cmdInsMe = new Command(locale.SR.get(locale.SR.MS_SLASHME), Command.SCREEN, 5);
//# 
//#         form = new Form(contact.getJid());
//#         width = form.getWidth();
//# 
//#         msgTF = new TextField(null, null, 1024, 0);
//#         scroller = new ClassicChat(null, width, Config.getInstance().classicChatHeight, contact);
//# 
//#         form.append(scroller);
//#         form.append(msgTF);
//#         form.addCommand(cmdCancel);
//#         form.addCommand(cmdSend);
//#         form.addCommand(cmdInsNick);
//#         form.addCommand(cmdInsMe);
//#         form.setCommandListener(this);
//# 
//#         contact.scroller = scroller;
//# 
//#         int size = contact.getMessageCount();
//#         for (int i = 0; i < size; i++) {
//#             String msg = contact.getMessageList().getItemRef(i).toString();
//#             if (((Msg) contact.getMessageList().getItemRef(i)).isUnread() == true) {
//#                 ((Msg) contact.getMessageList().getItemRef(i)).read();
//#             }
//#             StringUtils.addClassicChatMsg(msg, width, scroller);
//#         }
//#         contact.getMessageList().reEnumCounts();
//# 
//#         BombusQD.setCurrentView(form);
//#     }
//# 
//#     public void commandAction(Command c, Displayable s) {
//#         if (c == cmdSend) {
//#             String msg = msgTF.getString().trim();
//#             if ((msg != null) && (msg.length() != 0)) {
//#                 StaticData.getInstance().roster.sendMessage(contact, null, msg, null, null);
//#                 msgTF.delete(0, msgTF.size());
//#             }
//#         } else if (c == cmdInsNick) {
//#             new AppendNickForm(contact, msgTF.getCaretPosition(), msgTF).show();
//#             return;
//#         } else if (c == cmdInsMe) {
//#             msgTF.setString("/me ");
//#         } else if (c == cmdCancel) {
//#             BombusQD.sd.roster.show();
//#         }
//#     }
//#     
//#     public static int getWidth() {
//#         return width;
//#     }
//# }
//#endif
