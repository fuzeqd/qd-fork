/*
 * MessageList.java
 *
 * Created on 11.12.2005, 3:02
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package message;

import client.Config;
import client.Msg;
import colors.ColorTheme;
import java.util.Vector;
import menu.MenuListener;
import menu.Command;
import midlet.Commands;
import ui.VirtualList;
import ui.GMenu;
import ui.GMenuConfig;
import ui.VirtualElement;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
import util.StringUtils;

public abstract class MessageList extends VirtualList implements MenuListener {
    protected final Vector messages = new Vector(0);

    public MessageList() {
        super();
        enableListWrapping(false);
    }
    
    // Helpers  
    protected Msg getMessage(int index) {
        VirtualElement el = (VirtualElement)messages.elementAt(index);
        if (el instanceof Msg) {
            return (Msg)el;
        }
        return null;
    }
    
    protected Msg getSelectedMessage() {
        return (Msg)messages.elementAt(cursor);
    }
    
    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)messages.elementAt(index);
    }

    protected int getItemCount() {
        return messages.size();
    }

    protected abstract void commandState();

    protected void addDefaultCommands() {
//#ifdef CLIPBOARD
        if (getItemCount() != 0) {
            if (Config.useClipBoard) {
                addCommand(Commands.cmdCopy);
                if (!ClipBoard.isEmpty()) {
                    addCommand(Commands.cmdCopyPlus);
                }
            }
        }
//#endif
        if (getItemCount() != 0) {
            if (hasScheme()) {
                addCommand(Commands.cmdxmlSkin);
            }
            if (hasUrl()) {
                addCommand(Commands.cmdUrl);
            }
        }
    }

    public void commandAction(Command c) {
        Msg item = getSelectedMessage();
        if (c == Commands.cmdUrl) {
            Vector urls = (item).getUrlList();
            if (urls != null) {
                new MessageUrl(urls).show();
            }
        } else if (c == Commands.cmdxmlSkin) {
             ColorTheme.loadSkin(item.getBody(), 2, true);
        }
//#ifdef CLIPBOARD
        else if(c == Commands.cmdCopy) {
            ClipBoard.setClipBoard(msg2str(item));
        } else  if (c == Commands.cmdCopyPlus) {
            ClipBoard.addToClipBoard(msg2str(item));
        }
//#endif
    }

//#ifdef CLIPBOARD
    private String msg2str(Msg msg) {
        StringBuffer buf = new StringBuffer();

        if (msg.getSubject() != null) {
            buf.append(StringUtils.replaceNickTags(msg.getSubject()));
            buf.append('\n');
        }
        buf.append(StringUtils.replaceNickTags(msg.toString()));
        buf.append('\n');

        return buf.toString();
    }
//#endif

//#ifdef SMILES
    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        if (keyCode == '*') {
            if (getItemCount() > 0) {
                ((Msg)getFocusedObject()).toggleSmiles(this);
                redraw();
            }
        } else {
            super.keyPressed(keyCode);
        }       
    }
//#endif

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;
        return GMenu.MESSAGE_LIST;
    }

    protected boolean hasScheme() {
        if (0 == getItemCount()) {
            return false;
        }
        String body = getSelectedMessage().getBody();
        if (body.indexOf("xmlSkin") > -1) {
            return true;
        }
        return false;
    }

    protected boolean hasUrl() {
        if (0 == getItemCount()) {
            return false;
        }
        String body = getSelectedMessage().getBody();
        if (-1 != body.indexOf("http://")) {
            return true;
        }
        if (-1 != body.indexOf("https://")) {
            return true;
        }
        if (-1 != body.indexOf("ftp://")) {
            return true;
        }
        if (-1 != body.indexOf("tel:")) {
            return true;
        }
        if (-1 != body.indexOf("native:")) {
            return true;
        }
        return false;
    }
}
