/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.keys;

import client.Config;
import client.ConfigForm;
import client.StaticData;
import colors.ColorTheme;
//#ifdef XML_CONSOLE
//# import console.xml.XMLConsole;
//#endif
//#ifdef PRIVACY
import privacy.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif
//#if FILE_IO && FILE_TRANSFER
import io.file.transfer.TransferManager;
//#endif
//#ifdef STATS
import stats.StatsWindow;
//#endif
import java.util.Vector;
import locale.SR;
import midlet.BombusQD;
import ui.VirtualList;
import images.ImageList;
import ui.IconTextElement;
import images.MenuIcons;
import images.ActionsIcons;
import images.RosterIcons;
import ui.controls.AlertBox;
import com.alsutton.jabber.datablocks.Presence;
import client.StatusList;
import conference.bookmark.Bookmarks;

/**
 *
 * @author Mars
 */

public class UserActions {
    public static final int UA_ALL= -1;
    public static final int UA_KEYS= 0x01;
    public static final int UA_TASKS= 0x02;

    public static class userAct {
        int id; // Номер действия в кейсе actionExecute
        int type; // битовый флаг, определяющий,
            // где может использоваться действие:
            // UA_ALL - везде, UA_KEYS - только в хоткеях,
            // UA_TASKS - только в планировщике
        //String text; // Название действия
        IconTextElement item; // Иконка действия
        // (бред, но не знаю как это иначе назвать)

        public userAct( int id, int type, String text, ImageList ilist, int index) {
            this.id= id; this.type= type;
            this.item= new IconTextElement(  text, ilist, index);
        }

        public userAct( int id, int type, String text) {
            this( id, type, text, RosterIcons.getInstance(), RosterIcons.ICON_TRANSPARENT);
        }
    }

    private static UserActions instance;

    public static UserActions getInstance() {
        if (instance==null) instance=new UserActions();
        return instance;
    }

    public UserActions() {
    }

    public static Vector getActionsList( int type) {
        // Заполняем список доступных команд
        // в зависимости от типа списка
        Vector actList= null;
        actList= new Vector(0);

        for( int i=0; i <allActs.length; i++){
            if( (allActs[i].type & type) !=0)
                actList.addElement( allActs[i].item);
        }
        return actList;
    }

    public static boolean doActionByExtIndex( int type, int eInd, String text) {
        // Выполняем действие по внешнему индексу
        // в зависимости от типа списка

        for( int i=0; i <allActs.length; i++){
            if( (allActs[i].type &type) !=0)
                if( eInd-- ==0){
                    //if( (type & UA_TASKS) !=0 && text != null)
                        //snew AlertBox( allActs[i].item.toString(), text, AlertBox.BUTTONS_OK, 0).show();
                    return ActionExecute( allActs[i].id, type);
                }// if
        }
        return false;
    }

    public static final userAct allActs[]= {
        new userAct( 0, -1, SR.get(SR.MS_NO), RosterIcons.getInstance(), RosterIcons.ICON_TRANSPARENT)
        ,new userAct( 1, 1, SR.get(SR.MS_OPTIONS))
        ,new userAct( 2, 1, SR.get(SR.MS_CLEAN_ALL_MESSAGES))
        ,new userAct( 3, UA_ALL, SR.get(SR.MS_RECONNECT))
//#ifdef STATS
        ,new userAct( 4, 1, SR.get(SR.MS_STATS))
//#endif
        ,new userAct( 5, 1, SR.get(SR.MS_STATUS))
//#if FILE_IO && FILE_TRANSFER
        ,new userAct( 6, 1, SR.get(SR.MS_FILE_TRANSFERS))
//#endif
//#ifdef ARCHIVE
        ,new userAct( 7, 1, SR.get(SR.MS_ARCHIVE))
//#endif
//#ifdef SERVICE_DISCOVERY
        ,new userAct( 8, 1, SR.get(SR.MS_DISCO))
//#endif
//#ifdef PRIVACY
        ,new userAct( 9, 1, SR.get(SR.MS_PRIVACY_LISTS))
//#endif
        ,new userAct( 10, 1, SR.get(SR.MS_CUSTOM_KEYS))
//#ifdef POPUPS
        ,new userAct( 11, 1, SR.get(SR.MS_CLEAR_POPUPS))
//#endif
        ,new userAct( 12, -1, SR.get(SR.MS_APP_MINIMIZE))
        ,new userAct( 13, -1, SR.get(SR.MS_INVERT))
        ,new userAct( 14, 1, SR.get(SR.MS_FULLSCREEN))
//#ifdef XML_CONSOLE
//#         ,new userAct( 15, 1, SR.get(SR.MS_XML_CONSOLE))
//#endif
        ,new userAct( 16, -1, SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES))
        ,new userAct( 17, UA_ALL, "Quit QD")
        ,new userAct( 18, -1, SR.get(SR.MS_LOGOFF))
        ,new userAct( 19, -1, SR.get(SR.MS_AUTOLOGIN))
        ,new userAct( 20, -1, SR.get(SR.MS_DO_AUTOJOIN))
        ,new userAct( 21, UA_KEYS, "Juick commands")
        ,new userAct( 22, UA_ALL, "Status: online")
        ,new userAct( 23, UA_ALL, "Status: offline")
        ,new userAct( 24, UA_ALL, "Status: chat")
        ,new userAct( 25, UA_ALL, "Status: away")
        ,new userAct( 26, UA_ALL, "Status: dnd")
        ,new userAct( 27, UA_ALL, "Status: invisible")
        ,new userAct( 28, UA_KEYS, "Command: EventOk")
        //,new userAct( 29, UA_KEYS, "Command: keyClear")
        ,new userAct( 30, UA_KEYS, "Command: keyClose")
        ,new userAct( 31, UA_KEYS, "Command: Bookmarks")
        ,new userAct( 32, UA_KEYS, "Command: MyServices")
        ,new userAct( 33, UA_KEYS, "Cursor: moveHome")
        ,new userAct( 34, UA_KEYS, "Cursor: moveEnd")
        ,new userAct( 35, UA_KEYS, "Cursor: pageLeft")
        ,new userAct( 36, UA_KEYS, "Cursor: pageRight")

        //,new userAct( 37, UA_KEYS, "Chats: next chat")
        //,new userAct( 38, UA_KEYS, "Chats: clear chat")
        //,new userAct( 39, UA_KEYS, "Room: kick current")
        //,new userAct( 40, UA_KEYS, "Room: ban current")



/*
 *         cmds[22] = "Move cursor home";
        cmds[23] = "Move cursor end";
        cmds[24] = "Move cursor next";
        cmds[25] = "Move cursor previous";
        cmds[26] = "Move cursor left";
        cmds[27] = "Move cursor right";
        cmds[28] = "Go to previous window";
        cmds[29] = "Delete current item";
        cmds[30] = "[Chat] " + "Quote";
        cmds[31] = "[Chat] " + "Active contacts";
        cmds[32] = "[Roster] " + SR.MS_BOOKMARKS;
        cmds[33] = "[Roster] Collapse all group";
        cmds[34] = "Show info";
        cmds[35] = "Action Ok";
        cmds[36] = "Left_Soft std action";
        cmds[37] = "Right_Soft stc action";
        cmds[38] = "[Roster] " + "Show offline contacts";
        cmds[39] = "[Roster] " + SR.MS_TOOLS;
        cmds[40] = "[Roster] " + SR.MS_APP_MINIMIZE;
        cmds[41] = "[Roster + ActiveContacts] " + "Focus to next unreaded";
        cmds[42] = "[Roster] " + "Previous group";
        cmds[43] = "[Roster] " + "Next group";
        cmds[44] = "Block keyboard";
        cmds[45] = "[Roster] " + "Vibra/Sound";
        cmds[46] = "Moto backlight";
        cmds[47] = "[Roster] " + "Active contacts";
        cmds[48] = "[Chat] " + "Previous contact with messages";
        cmds[49] = "[Chat] " + "Next contact with messages";
        cmds[50] = "[Chat + XMLList + ActiveContacts] " + SR.MS_CLEAR_LIST;
        cmds[51] = "[Chat] " + SR.MS_REPLY;
        cmds[52] = "[Chat + Roster + ActiveContacts + XMLList + MessageUrl] " + SR.MS_RESUME;
        cmds[53] = "["+SR.MS_ARCHIVE+"] " + SR.MS_PASTE_BODY;
        cmds[54] = "["+SR.MS_PRIVACY_LISTS+"] " + "Add new item";
        cmds[55] = "["+SR.MS_HISTORY+"] " + "Begin of file";
        cmds[56] = "["+SR.MS_HISTORY+"] " + "End of file";
        cmds[57] = "["+SR.MS_BOOKMARKS+"] " + SR.MS_DISCO_ROOM;
        cmds[58] = "[Roster] " + "Kick from groupchat";
        cmds[59] = "[Chat] " + "focus to next hightlited message";

 */
    };

    private static boolean ActionExecute(int actId, int type) {
        //return false if action not executed

        Config cf= Config.getInstance();
        StaticData sd= StaticData.getInstance();

        switch (actId) {
            case 0: // nop
                return true;
            case 1: // options
                new ConfigForm().show();
                break;
            case 2:
                sd.roster.cmdCleanAllMessages();
                break;
            case 3:
                sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_SIMULATED_BREAK)));
                break;
//#ifdef STATS
            case 4:
                new StatsWindow().show();
                break;
//#endif
            case 5:
                sd.roster.cmdStatus();
                break;
//#if FILE_IO && FILE_TRANSFER
            case 6:
                new TransferManager().show();
                break;
//#endif
//#ifdef ARCHIVE
            case 7:
                sd.roster.cmdArchive();
                break;
//#endif
//#ifdef SERVICE_DISCOVERY
            case 8:
                if( sd.roster.isLoggedIn())
                    new ServiceDiscovery(null, null, false).show();
                break;
//#endif
//#ifdef PRIVACY
            case 9:
                if( sd.roster.isLoggedIn())
                    new PrivacySelect().show();
                break;
//#endif
            case 10:
                new UserKeysList().show();
                break;
//#ifdef POPUPS
            case 11:
                VirtualList.getPopUp().clear();;
                break;
//#endif
            case 12:
                if( cf.allowMinimize) BombusQD.hideApp();
                break;
            case 13:
                ColorTheme.invertSkin();
                break;
            case 14:
                Config.fullscreen = !Config.fullscreen;
                sd.canvas.setFullScreenMode(Config.fullscreen);
                cf.saveToStorage();
                break;
//#ifdef XML_CONSOLE
//#              case 15:
//#                  new XMLConsole().show();
//#                  break;
//#endif
            case 16: // Leave MUCs
                sd.roster.leaveAllMUCs();
                break;
            case 17: // Ouit QD
                BombusQD.getInstance().notifyDestroyed();
                break;
            case 18: // LogOff
                sd.roster.logoff( SR.get(SR.MS_LOGOFF));
                return true;
            case 19: // LogOn
                sd.roster.logon( SR.get(SR.MS_AUTOLOGIN));
                return true;
            case 20: //Join MUCs
                sd.roster.MUCsAutoJoin( SR.get(SR.MS_DO_AUTOJOIN));
                return true;
            case 21: //Juick
                //sd.roster.MUCsAutoJoin( SR.get(SR.MS_DO_AUTOJOIN));
                return true;
            case 22:
                BombusQD.sd.roster.sendPresence( Presence.PRESENCE_ONLINE,
                        StatusList.getInstance().getStatus(Presence.PRESENCE_ONLINE).getMessage());
                return true;
            case 23:
                BombusQD.sd.roster.sendPresence( Presence.PRESENCE_OFFLINE,
                        StatusList.getInstance().getStatus(Presence.PRESENCE_OFFLINE).getMessage());
                return true;
            case 24:
                BombusQD.sd.roster.sendPresence( Presence.PRESENCE_CHAT,
                        StatusList.getInstance().getStatus(Presence.PRESENCE_CHAT).getMessage());
                return true;
            case 25:
                BombusQD.sd.roster.sendPresence( Presence.PRESENCE_AWAY,
                        StatusList.getInstance().getStatus(Presence.PRESENCE_AWAY).getMessage());
                return true;
            case 26:
                BombusQD.sd.roster.sendPresence( Presence.PRESENCE_DND,
                        StatusList.getInstance().getStatus(Presence.PRESENCE_DND).getMessage());
                return true;
            case 27:
                BombusQD.sd.roster.sendPresence( Presence.PRESENCE_INVISIBLE,
                        StatusList.getInstance().getStatus(Presence.PRESENCE_INVISIBLE).getMessage());
                return true;
            case 28:
                sd.roster.eventOk();
                return true;
            case 29:
                //client;
                return true;
            case 30:
                sd.roster.destroyView();
                return true;
            case 31:
                new Bookmarks().show();
                return true;
            case 32:
                new ServiceDiscovery(null, null, false).show();
                return true;
            case 33:
                sd.roster.moveCursorHome();
                return true;
            case 34:
                sd.roster.moveCursorEnd();
                return true;
            case 35:
                sd.roster.pageLeft();
                return true;
            case 36:
                sd.roster.pageRight();
                return true;
            case 37:
                return true;
            case 38:
                return true;
            case 39:
                //new QuickPrivelegyEditForm((MucContact)item, QuickPrivelegyEditForm.KICK,myNick).show();
                return true;
            case 40:
                return true;
/*            case 22:
                return true;
            case 22:
                return true;
            case 22:
                return true;
            case 22:
                return true;
            case 22:
                return true;
            case 22:
                return true;
            case 22:
                return true;
 *
 */

        }
        return false;
    }

}
