/*
  Copyright (c) 2000, Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
 
package com.alsutton.jabber.datablocks;
import info.Version;
import xmpp.EntityCaps;
import com.alsutton.jabber.*;
import images.RosterIcons;
import xmpp.XmppError;
import java.util.*;
import locale.SR;

/**
 * Class representing the presence message block.
 */

public class Presence extends JabberDataBlock {
     public final static String PRS_OFFLINE="unavailable";
     public final static String PRS_ERROR="error";
     public final static String PRS_CHAT="chat";
     public final static String PRS_AWAY="away";
     public final static String PRS_XA="xa";
     public final static String PRS_DND="dnd";
     public final static String PRS_ONLINE="online";
     public final static String PRS_INVISIBLE="invisible";

     public final static byte PRESENCE_ONLINE=0;
     public final static byte PRESENCE_CHAT=1;
     public final static byte PRESENCE_AWAY=2;
     public final static byte PRESENCE_XA=3;
     public final static byte PRESENCE_DND=4;
     public final static byte PRESENCE_OFFLINE=5;
     public final static byte PRESENCE_ASK=6;
     public final static byte PRESENCE_UNKNOWN=7;
     public final static byte PRESENCE_INVISIBLE=RosterIcons.ICON_INVISIBLE_INDEX;
     public final static byte PRESENCE_ERROR=RosterIcons.ICON_ERROR_INDEX;
     public final static byte PRESENCE_TRASH=RosterIcons.ICON_TRASHCAN_INDEX;
     public final static byte PRESENCE_AUTH=-1;
     public final static byte PRESENCE_AUTH_ASK=-2;
     public final static byte PRESENCE_SAME=-100;    

    public Presence( JabberDataBlock _parent, Vector _attributes ) {
        super( _parent, _attributes );
    }

    public Presence(String to, String type){
        super(null,null);
        setAttribute("to",to);
        setAttribute("type",type);
    }

    public Presence(int status, int priority, String message, String nick) {
        super( null, null );
        switch (status){
            case PRESENCE_OFFLINE: setType(PRS_OFFLINE); break;
            case PRESENCE_INVISIBLE: setType(PRS_INVISIBLE); break;
            case PRESENCE_CHAT: setShow(PRS_CHAT);break;
            case PRESENCE_AWAY: setShow(PRS_AWAY);break;
            case PRESENCE_XA: setShow(PRS_XA);break;
            case PRESENCE_DND: setShow(PRS_DND);break;
        }
        if (priority!=0)
            addChild("priority",String.valueOf(priority));

        if (message!=null) 
            if (message.length()>0)
                addChild("status",message);

        if (status!=PRESENCE_OFFLINE) {
            addChild(EntityCaps.presenceEntityCaps());
            if (nick!=null)
                addChildNs("nick", "http://jabber.org/protocol/nick").setText(nick);

            setAttribute("ver", Version.getVersionString(false));
        }
    }

    private byte presenceCode;
    
    private StringBuffer presenseText;
  
    public String getPresenceText() {
        String errText=null;
        presenseText = new StringBuffer(0);
        String type=getTypeAttribute();
        presenceCode=PRESENCE_AUTH;
        if (type!=null) {
          if (type.equals(PRS_OFFLINE)) { 
              presenceCode=PRESENCE_OFFLINE;
              presenseText.append(SR.get(SR.MS_OFFLINE));
          }
          if (type.equals("subscribe")) {
              presenceCode=PRESENCE_AUTH_ASK;
              presenseText.append(SR.get(SR.MS_SUBSCRIPTION_REQUEST_FROM_USER));
          } 
          if (type.equals("subscribed")) presenseText.append(SR.get(SR.MS_SUBSCRIPTION_RECEIVED));
          if (type.equals("unsubscribed")) presenseText.append(SR.get(SR.MS_SUBSCRIPTION_DELETED));


          if (type.equals(PRS_ERROR)) {
              presenceCode=PRESENCE_ERROR;
              presenseText.append(PRS_ERROR);
              errText=XmppError.findInStanza(this).toString();
          }


          if (type.length()==0) {
              presenceCode=PRESENCE_UNKNOWN;
              presenseText.append("UNKNOWN presence stanza");
          }
        } else {
            String show=getShow(); 
            presenseText.append(SR.getPresence(show));
            presenceCode=PRESENCE_ONLINE;
            if (show.equals(PRS_AWAY)) presenceCode=PRESENCE_AWAY;
            else if (show.equals(PRS_DND)) presenceCode=PRESENCE_DND;
            else if (show.equals(PRS_XA)) presenceCode=PRESENCE_XA;
            else if (show.equals(PRS_CHAT)) presenceCode=PRESENCE_CHAT;
            show=null;
        }


        String status=(errText==null)? getChildBlockText("status"):errText;
        if (status!=null)
            if (status.length()>0)
                presenseText
                .append(" (")
                .append( status )
                .append(')');
        int priority=getPriority();
        if (priority!=0)
            presenseText
                .append(" [")
                .append(getPriority())
                .append(']');
        errText=null;
        type=null;


        return presenseText.toString();
    }
    
    

    public void setType( String type ) {
        setAttribute("type", type);
    }

    public void setTo(String jid){
        setAttribute("to", jid);
    }

    public int getPriority(){
        try {
            return Integer.parseInt(getChildBlockText("priority"));
        } catch (Exception e) {
            return 0;
        }
    }

    public void setShow(String text){
        addChild("show", text);
    }

    public String getTagName() {
        return "presence";
    }

    public byte getTypeIndex() {
        return presenceCode;
    }

    private String getShow(){
        return (getChildBlockText("show").length()==0)? PRS_ONLINE: getChildBlockText("show");
    }

    public String getFrom() {
        return getAttribute("from");
    }

    public String getStatus(){
        return (getChildBlockText("status").length()==0)? null: getChildBlockText("status");
    }

    public boolean hasEntityCaps() {
        if (getChildBlock("c")==null) return false;
        return getChildBlock("c").isJabberNameSpace("http://jabber.org/protocol/caps");
    }
  
    public String getEntityNode() {
        if (getChildBlock("c")!=null){
            if (getChildBlock("c").isJabberNameSpace("http://jabber.org/protocol/caps")) {
                return getChildBlock("c").getAttribute("node");
            }
        }
        return null;
    }

    public String getEntityVer() {
        JabberDataBlock cc=getChildBlock("c");
        if (cc!=null){
            if (cc.isJabberNameSpace("http://jabber.org/protocol/caps")) {
                String attr = cc.getAttribute("ver");
                if(attr == null) return null;
                return (attr.endsWith("=")? null : attr);
            }
        }
        return null;
    }
}