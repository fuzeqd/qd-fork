/*
 * Menu.java
 *
 * Created on 1.05.2005, 20:48
 *
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
 */

package menu;

import client.Config;
import images.ImageList;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualCanvas;
import ui.VirtualElement;
import ui.VirtualList;


/**
 *
 * @author Evg_S,aqent
 */

public class Menu extends VirtualList {
    private Vector menuitems;

    private ImageList il;

    public Menu(String mainbar) {
        this(mainbar, null);
    }

    public Menu(String mainbar, ImageList il) {
        super();
        setMainBarItem(new MainBar(mainbar));
        menuitems = new Vector(0);
        this.il = il;
    }
    
    public VirtualElement getItemRef(int index) {
        return (VirtualElement)menuitems.elementAt(index);
    }

    public int getItemCount() {
        return menuitems.size();
    }

    public void addItem(MenuItem mi) {
        mi.pos = getItemCount();
        menuitems.addElement(mi);
    }

    public void addItem(String label, int index, int iconIndex) {
        addItem(new MenuItem(label, index, iconIndex, il));
    }

    public void addItem(String label, int index) {
        addItem(label, index, -1);
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_OK);
    }
    
    public void touchLeftPressed() {
        eventOk();
    }
     
    public void keyPressed(int keyCode) {
        if (Config.executeByNum && getItemCount() > 0) {
            switch (keyCode) {
                case VirtualCanvas.KEY_NUM0:
                    executeCommand(9);
                    return;
                case VirtualCanvas.KEY_NUM1:
                    executeCommand(0);
                    return;
                case VirtualCanvas.KEY_NUM2:
                    executeCommand(1);
                    return;
                case VirtualCanvas.KEY_NUM3:
                    executeCommand(2);
                    return;
                case VirtualCanvas.KEY_NUM4:
                    executeCommand(3);
                    return;
                case VirtualCanvas.KEY_NUM5:
                    executeCommand(4);
                    return;
                case VirtualCanvas.KEY_NUM6:
                    executeCommand(5);
                    return;
                case VirtualCanvas.KEY_NUM7:
                    executeCommand(6);
                    return;
                case VirtualCanvas.KEY_NUM8:
                    executeCommand(7);
                    return;
                case VirtualCanvas.KEY_NUM9:
                    executeCommand(8);
                    return;
            }
        } else {
            switch (keyCode) {
            case VirtualCanvas.KEY_NUM4:
                pageLeft();
                break;
            case VirtualCanvas.KEY_NUM6:
                pageRight();
                break;
            }
        }
        super.keyPressed(keyCode);
    }
   
    private void executeCommand(int index) {
        moveCursorTo(index);
        eventOk();
    }   
}
