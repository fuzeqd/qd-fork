/*
 * StringUtils.java
 *
 * Created on 28 ��� 2008 �., 13:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;
import conference.ConferenceGroup;
import conference.MucContact;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import xmpp.XmppError;
import java.util.Random;
//#ifdef CLASSIC_CHAT
//# import client.Config;
//# import font.FontCache;
//# import client.ClassicChat;
//#endif
import client.Msg;

/**
 *
 * @author ad
 */

public class StringUtils {

    /** Creates a new instance of StringUtils */
    public StringUtils() { }


    public static String quoteString(Msg msg) {
        StringBuffer out=new StringBuffer(0);
        String subject = msg.getSubject();
        if (subject!=null)
            if (subject.length()>0)
                out.append(subject).append('\n');
        out.append(msg.toString());
        int i=0;
        int len = out.length();
        while (i<len) {
            if (out.charAt(i)<0x03) out.deleteCharAt(i);
            else i++;
        }
        return out.toString();
    }

    private final static String[] badChars= { "?", "\\", "/", "*", ".", "\"", ":", "%", "@", "|", "<", ">", "COM", "LPT", "NULL", "PRINT"};

    public static String replaceNickTags(String body){
         if (null==body) {
             return null;
         }

         body = stringReplace(body, "<nick>", "");
         body = stringReplace(body, "</nick>", "");
         return body;
    }

    public static Msg replaceNickTags(Msg msg){
         msg.setBody(replaceNickTags(msg.getBody()));
         return msg;
    }

    public static String stringReplace(String aSearch, String aFind, String aReplace) {
    	int pos = aSearch.indexOf(aFind);
    	if (pos != -1) {
            StringBuffer buffer = new StringBuffer(0);
            int lastPos = 0;
             while (pos != -1) {
                    buffer.append(aSearch.substring(lastPos, pos)).append(aReplace);
                    lastPos = pos + aFind.length();
                    pos = aSearch.indexOf(aFind, lastPos);
            }
            buffer.append( aSearch.substring(lastPos));
            aSearch = buffer.toString();
    	}
    	return aSearch;
    }

//#ifdef CLASSIC_CHAT
//#   public static Font getFont() {
//#         return FontCache.getFont(false, Config.msgFont);
//#   }
//# 
//#   public static void addClassicChatMsg(String message, int availWidth,ClassicChat scrMsg) {
//#         Vector lines=new Vector(0);
//#         char[] valueChars = message.concat("   ").toCharArray();
//#         int startPos = 0;
//#         availWidth-=24;
//#         int currentLineWidth = 0;
//# 
//#         for (int i = 0; i < valueChars.length; i++) {
//#             char c = valueChars[i];
//#             currentLineWidth += //MFont.isCheck()?MFont.getCharWidth(c):
//#                     getFont().charWidth(c);
//#             if (c == '\n') {
//#                 scrMsg.storeMessage( new String( valueChars, startPos, i - startPos ));
//#                 //lastSpacePos = -1;
//#                 startPos = i+1;
//#                 currentLineWidth = 0;
//#                 i = startPos;
//#             } else if (currentLineWidth >= availWidth && i > 0) {
//#                     i--;
//#                     scrMsg.storeMessage( new String( valueChars, startPos, i - startPos ));
//#                     startPos =  i;
//#                     currentLineWidth = 0;
//#             }
//#         }
//#         scrMsg.storeMessage( new String( valueChars, startPos, valueChars.length - startPos ));
//#     }
//#endif

    public static String getSizeString(long number) { //multiple calls
        StringBuffer suffix = new StringBuffer(0);
        try {
            String ratio;
            if ( number > 1024000 ) {
                ratio=Long.toString(number/100000);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))

                      .append("mb");
            } else if ( number > 1024 ) {
                ratio=Long.toString(number/100);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))

                      .append("kb");
            } else {
                suffix.append(number)
                      .append("b");
            }
            //suffix.append(midlet.BombusQD.sd.roster.theStream.getStreamStatsBar());
        } catch (Exception e) { }

        return suffix.toString();
    }

    public static String replaceBadChars (String src) {
        int len = badChars.length;
        for (int i=0; i<len;i++) src=stringReplace(src,badChars[i],"_");
        return src;
    }

    public static String urlPrep(String src){
        String mask=" #$%&/:;<=>?@[\\]^'{|}";
        StringBuffer out=new StringBuffer();

        for (int i=0; i<src.length(); i++) {
            char s=src.charAt(i);

            if (mask.indexOf(s)<0) {  out.append(s); continue;  }

            out.append('%').append(hexByteToString((byte)s));
        }

        return out.toString();
    }

    public static Vector parseMessage(String value, int availWidth, Font font) {
        Vector lines=new Vector(0);
        char[] valueChars = value.toCharArray();
        int startPos = 0;
        int lastSpacePos = -1;
        int lastSpacePosLength = 0;
        int currentLineWidth = 0;
        int len = valueChars.length;
        char c;
        for (int i = 0; i < len; i++) {
            c = valueChars[i];
            currentLineWidth += font.charWidth(c);
            if (c == '\r') {
            } else if (c == '\n') {
                lines.addElement( new String( valueChars, startPos, i - startPos ) );
                lastSpacePos = -1;
                startPos = i+1;
                currentLineWidth = 0;
            } else if (currentLineWidth >= availWidth && i > 0) {
                if ( lastSpacePos == -1 ) {
                    i--;
                    lines.addElement( new String( valueChars, startPos, i - startPos ) );
                    startPos =  i;
                    currentLineWidth = 0;
                } else {
                    currentLineWidth -= lastSpacePosLength;
                    lines.addElement( new String( valueChars, startPos, lastSpacePos - startPos ) );
                    startPos =  lastSpacePos + 1;
                    lastSpacePos = -1;
                }
            } else if (c == ' ' || c == '\t') {
                lastSpacePos = i;
                lastSpacePosLength = currentLineWidth;
            }
        }
        // last string
        lines.addElement( new String( valueChars, startPos, valueChars.length - startPos ) );
        valueChars=null;
        return lines;
    }

    public static boolean containsWord(String str, String word) {
        if (str==null) return false;
        int start, end=0, length=str.length(), word_lengh=word.length();
        if (word_lengh>length) return false;
        final String separator=" `~!@#$%^&*()+=-'\"\\|/;:.>,<?[]{}\n\r\t№";
        boolean good_start=false, good_end = false;
        char symbol;
        while (end<length) {
            start = str.indexOf(word, end);
            if (start<0) return false;
            end = start + word_lengh;
            if (start == 0) {
                good_start = true;
            } else {
                symbol = str.charAt(start-1);
                good_start = (separator.indexOf(symbol)>-1);
            }
            if (end == length) {
                good_end = true;
            } else {
                symbol = str.charAt(end);
                good_end = (separator.indexOf(symbol)>-1);
            }
            if (good_start && good_end) return true;
        }
        return false;
    }
    
    public static String toExtendedString(String src){
        src=stringReplace(src,"%dt",Time.dispLocalTime());
        src=stringReplace(src,"%t",Time.localTime());
                Random rand = new Random();
                String[] qd_offline_status = {
                       "BombusQD. It's very simple",
                       "I use BombusQD. And you?",
                       "I'am happy user of BombusQD. Check it on http://bombusqd.hdd1.ru/",
                       "BombusQD. Check it on http://bombusqd.hdd1.ru/",
                       "Bombus, QD Bombus",
                       "I'am happy user of BombusQD. You can get it on http://bombusqd.hdd1.ru/",
                       "Are you ready for BombusQD?"
                };
        int i = Math.abs(rand.nextInt()%qd_offline_status.length);
        src=stringReplace(src,"%qd",qd_offline_status[i]);
        return src;
    }

    public static String hexByteToString(byte b){
        StringBuffer out=new StringBuffer();
        char c = (char) ((b >> 4) & 0xf);
        if (c > 9)   c = (char) ((c - 10) + 'a');
        else  c = (char) (c + '0');
        out.append(c);
        c = (char) (b & 0xf);
        if (c > 9)
            c = (char)((c-10) + 'a');
        else
            c = (char)(c + '0');
        out.append(c);

        return out.toString();
    }
    
    // Puts the specified word (val) into the buffer (buf) at position off using big endian byte ordering
    public static void putWordBE(byte[] buf, int off, int val) {
        buf[off]   = (byte) ((val >> 8) & 0x000000FF);
        buf[++off] = (byte) ((val)      & 0x000000FF);
    }   
    
    public static int getWordBE(byte[] buf, int off) {
        int val = (((int) buf[off]) << 8) & 0x0000FF00;
        return val | (((int) buf[++off])) & 0x000000FF;
    }
    
    /* Divide text to array of parts using separator character */
    static public String[] explode(String text, char separator) {
        if (text.equals("")) {
            return new String[0];
        }
        Vector tmp = new Vector();
        int start = 0;
        int end = text.indexOf(separator, start);
        while (end >= start) {
            tmp.addElement(text.substring(start, end));
            start = end + 1;
            end = text.indexOf(separator, start);
        }
        tmp.addElement(text.substring(start));
        String[] result = new String[tmp.size()];
        tmp.copyInto(result);
        return result; 
    }

    public static String processError(Presence presence, byte presenceType, ConferenceGroup group, MucContact muc) {
        XmppError xe=XmppError.findInStanza(presence);
        int errCode=xe.getCondition();

        //ConferenceGroup grp=(ConferenceGroup)group;//?
        if (presenceType>=Presence.PRESENCE_OFFLINE)
            midlet.BombusQD.sd.roster.testMeOffline(muc, group, true);
        if (errCode!=XmppError.CONFLICT || presenceType>=Presence.PRESENCE_OFFLINE)
            muc.setStatus(presenceType);

        String errText=xe.getText();
        if (errText!=null) return xe.toString(); // if error description is provided by server

        // legacy codes
        switch (errCode) {
            case XmppError.NOT_AUTHORIZED:        return "Password required";
            case XmppError.FORBIDDEN:              return "You are banned in this room";
            case XmppError.ITEM_NOT_FOUND:        return "Room does not exists";
            case XmppError.NOT_ALLOWED:           return "You can't create room on this server";
            case XmppError.NOT_ACCEPTABLE:         return "Reserved roomnick must be used";
            case XmppError.REGISTRATION_REQUIRED:   return "This room is members-only";
            case XmppError.CONFLICT:               return "Nickname is already in use by another occupant";
            case XmppError.SERVICE_UNAVAILABLE:     return "Maximum number of users has been reached in this room";
            default: return xe.getName();
        }
    }
    
    //split string into array
    public static Vector split(String original, char separator) {
        Vector nodes = new Vector();    
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + 1);
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);
        return nodes;
    }

    public static String getFileName(String fname) {
        if (null != fname) {
            StringBuffer buf = new StringBuffer(fname);
            int dotIdx = fname.lastIndexOf('.');
            if (-1 == dotIdx) {
                dotIdx = buf.length();
            }

            buf.insert(dotIdx, '_');
            buf.insert(dotIdx + 1, Time.localDate());

            System.out.println(buf);

            return buf.toString();
        }
        return null;
    }
}
