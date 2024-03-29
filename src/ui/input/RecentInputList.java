/*
 * TextListBox.java
 *
 * Created on 25 ??? 2008 ?., 16:58
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package ui.input;

import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import java.util.Vector;
import locale.SR;
import menu.MenuListener;
import menu.Command;
import ui.GMenu;
import ui.GMenuConfig;
import ui.controls.form.ListItem;

/**
 *
 * @author ad,aqent
 */

public class RecentInputList extends VirtualList implements MenuListener {

    private Command cmdOk;
    private Command cmdClear;

    private InputTextBox input;
    private Vector recentList;

    public RecentInputList(InputTextBox input) {
        super();

        cmdOk = new Command(SR.get(SR.MS_OK), 0x43);
        cmdClear = new Command(SR.get(SR.MS_CLEAR), 0x33);

        this.input = input;
        this.recentList = input.getRecentList();

        setMainBarItem(new MainBar(SR.get(SR.MS_SELECT)));
    }

    public void commandState() {
        menuCommands.removeAllElements();

        addCommand(cmdOk); 
        addCommand(cmdClear);
    }

    public void eventOk() {
        input.setString((String)recentList.elementAt(cursor));
        destroyView();
    }

    public void commandAction(Command c){
        if (c==cmdClear) {
            input.clearRecentList();
        } else if (c == cmdOk) {
            eventOk();
        }

        destroyView();
    }

    public VirtualElement getItemRef(int index) {
        return new ListItem((String)recentList.elementAt(index));
    }

    public int getItemCount() {
        return recentList.size();
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_MENU);
    }

    public int showGraphicsMenu() {
        commandState();

        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.TEXTLISTBOX;
        return GMenu.TEXTLISTBOX;
    }
}
