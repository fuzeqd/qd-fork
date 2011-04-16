/*
 * StatsWindow.java
 *
 * Created on 03.10.2008, 19:42
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
//#ifdef STATS
package stats;

import client.Config;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import client.Roster;
import menu.Command;
//#endif
import javax.microedition.lcdui.Displayable;
import locale.SR;
import menu.MenuListener;
import midlet.Commands;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import util.StringUtils;
//#ifdef GRAPHICS_MENU
import ui.GMenu;
import ui.GMenuConfig;
//#endif
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
/**
 *
 * @author ad,aqent
 */
public class StatsWindow extends DefForm implements MenuListener {
    Stats st=Stats.getInstance();

    public Command cmdClear;
    public Command cmdSave;

    public StatsWindow() {
        super(SR.get(SR.MS_STATS));
        StringBuffer sb = new StringBuffer(0);

        cmdClear = new Command(SR.get(SR.MS_CLEAR), Command.SCREEN, 2);
        cmdClear.setImg(0x33);

        cmdSave = new Command(SR.get(SR.MS_SAVE), Command.OK, 3);
        cmdSave.setImg(0x44);

        sb.append(SR.get(SR.MS_ALL))
          .append(StringUtils.getSizeString(st.getAllTraffic()))
          .append('\n');
        sb.append(SR.get(SR.MS_PREVIOUS_))
          .append(StringUtils.getSizeString(st.getLatest()))
          .append('\n');
        sb.append(SR.get(SR.MS_CURRENT))
          .append(StringUtils.getSizeString(st.getCurrentTraffic()));
	if (midlet.BombusQD.sd.roster!=null && midlet.BombusQD.sd.roster.theStream!=null)
	    sb.append(midlet.BombusQD.sd.roster.theStream.getStreamStatsBar());
	sb.append('\n');

        if (midlet.BombusQD.sd.roster.isLoggedIn() && midlet.BombusQD.cf.userAppLevel == 1) {
           sb.append(SR.get(SR.MS_COMPRESSION))
	     .append("\n")
             .append(midlet.BombusQD.sd.roster.theStream.getStreamStats())
             .append('\n');
        }

        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            sb.append(SR.get(SR.MS_CONNECTED))
	      .append("\n")
              .append(midlet.BombusQD.sd.roster.theStream.getConnectionData())
              .append('\n');
        }
        sb.append(SR.get(SR.MS_CONN))
          .append(st.getSessionsCount())
          .append('\n');
        sb.append(SR.get(SR.MS_STARTED))
          .append(Roster.startTime)
          .append('\n');
        sb.append(SR.get(SR.MS_APPRUN_COUNT))
          .append(st.appRunCount);

        MultiLine item = new MultiLine( null, sb.toString(), getWidth());
        item.setSelectable(true);
        addControl(item);
    }


    public void commandAction(Command command, Displayable displayable) {
        if(command==cmdSave){
            Stats.getInstance().saveToStorage(false,false);
//#ifdef CLIPBOARD
	} else if (command == Commands.cmdCopy) {
            // we have only one control
            String text = getControl(0).toString();
            ClipBoard.setClipBoard(text);
            destroyView();
//#endif
        } else if (command == cmdClear) {
            st.saveToStorage(true,false);
            destroyView();
        }
    }

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
//#ifdef CLIPBOARD
        if (Config.useClipBoard) {
            addCommand(Commands.cmdCopy);
        }
//#endif
        addCommand(cmdClear);
        addCommand(cmdSave);
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand() {
        return SR.get(SR.MS_MENU);
    }

//#ifdef GRAPHICS_MENU
    public void touchLeftPressed(){
        showGraphicsMenu();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.STATS_WINDOW;
        redraw();
        return GMenu.STATS_WINDOW;
    }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATS), null, menuCommands);
//#    }
//#endif

//#endif


}
//#endif