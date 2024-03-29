/*
 * RosterIcons.java
 *
 * Created on 3.12.2005, 20:02
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package images;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import util.StringLoader;

/**
 *
 * @author EvgS
 */

public class RosterIcons extends ImageList {
    private final static String ROSTER_ICONS_PATH = "/images/skin.png";

    private final static int ICONS_IN_ROW = 8; // 0x0X ???
    private final static int ICONS_IN_COL = 6; // 0xX0 ???

    private Hashtable transports;
    private Vector transpSkins=null;

    private static RosterIcons instance;

    public static RosterIcons getInstance() {
	if (instance == null) {
            instance = new RosterIcons();
        }
	return instance;
    }

    private RosterIcons() {
	super(ROSTER_ICONS_PATH, ICONS_IN_COL, ICONS_IN_ROW);

        transports=new StringLoader().hashtableLoader("/images/transports.txt"); //new Hashtable();
        if (transports !=null) {
            transpSkins=new Vector(transports.size());
            transports.put("conference", new Integer(ICON_GROUPCHAT_INDEX));
        }
    }
    
    public int getTransportIndex(String name){
        try {
            Object o = transports.get(name);
            if (o instanceof String) {
                int index=(transpSkins.size()+1)<<24;
                // loading additional skin
                ImageList customTransp=new ImageList((String) o, 1, ICONS_IN_ROW);
                // customTransp loading success?
                if (customTransp.getHeight()==0) customTransp=this;
                transpSkins.addElement(customTransp);
                transports.put(name, new Integer(index) );
                return index;
            }
            return ((Integer)o).intValue();
        }catch (NullPointerException e){
            return 0;
        }
    }
   

    public void drawImage(Graphics g, int index, int x, int y) {
        if (transpSkins!=null && index>66000) { //draw transport icons
            ((ImageList)transpSkins.elementAt( (index>>24) -1 )).drawImage(g, index & 0xff, x, y);
        } else super.drawImage(g, index, x, y);
    }

    public static final byte ICON_INVISIBLE_INDEX = 0x10;
    public static final byte ICON_ONLINE_STATUS = 0x00;
    public static final byte ICON_CHAT_STATUS = 0x01;
    public static final byte ICON_AWAY_STATUS = 0x02;
    public static final byte ICON_UNAVIALABLE_STATUS = 0x03;
    public static final byte ICON_BUSY_STATUS = 0x04;
    public static final byte ICON_OFFLINE_STATUS = 0x05;
    public static final byte ICON_IDONTNOW_STATUS = 0x07;
    public static final byte ICON_ERROR_INDEX = 0x11;
    public static final byte ICON_TRASHCAN_INDEX = 0x12;
    public static final byte ICON_PROGRESS_INDEX = 0x13;
    public static final byte ICON_VIEWING_INDEX = 0x14;
    public static final byte ICON_SEARCH_INDEX = 0x14;
    public static final byte ICON_REGISTER_INDEX = 0x15;
    public static final byte ICON_MSGCOLLAPSED_INDEX = 0x16;
    public static final byte ICON_KEYBLOCK_INDEX = 0x17;
    public static final byte ICON_MESSAGE_INDEX = 0x20;
    public static final byte ICON_AUTHRQ_INDEX = 0x21;
    public static final byte ICON_COMPOSING_INDEX = 0x22;    
    public static final byte ICON_AD_HOC=ICON_COMPOSING_INDEX;
    public static final byte ICON_EXPANDED_INDEX = 0x23;
    public static final byte ICON_COLLAPSED_INDEX = 0x24;
    public static final byte ICON_MESSAGE_BUTTONS = 0x25;
    public static final byte ICON_ARROW_RIGHTLEFT = 0x25;
    public static final byte ICON_ARROW_RIGHT = 0x26;
    public static final byte ICON_ARROW_LEFT = 0x27;

    public static final byte ICON_PROFILE_INDEX = 0x30;/*30..34*/
    public static final byte ICON_DELIVERED_INDEX = 0x35;
    public static final byte ICON_APPEARING_INDEX = 0x36;

    public static final byte ICON_PRIVACY_ALLOW = 0x36;
    public static final byte ICON_PRIVACY_BLOCK = 0x37;
    
    public static final byte ICON_PLUGINBOX_CHECKED = 0x36;
    public static final byte ICON_PLUGINBOX_UNCHECKED = 0x37;

    public static final byte ICON_GROUPCHAT_INDEX = 0x40;
    public static final byte ICON_GCJOIN_INDEX = 0x41;   
    public static final byte ICON_GCCOLLAPSED_INDEX = 0x42;
    public static final byte ICON_TRANSPARENT = 0x44;
    public static final byte ICON_PRIVACY_ACTIVE = 0x46;
    public static final byte ICON_ROOMLIST=ICON_PRIVACY_ACTIVE;
    public static final byte ICON_PRIVACY_PASSIVE = 0x47;
    public static final byte ICON_MODERATOR_INDEX = 0x50;

    public static final byte ICON_CHOICEBOX_UNCHECKED = 0x56;
    public static final byte ICON_CHOICEBOX_CHECKED = 0x57;
}
