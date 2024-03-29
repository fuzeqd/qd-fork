/*
 * AlertCustomize.java
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package alert; 

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import util.StringLoader;

public class AlertCustomize {
    
    private final static String toneSequence="tone sequence";
    private final static String none="none";
    
    public int soundsMsgIndex=1;
    public String messagesnd="";
    public String messageSndType=toneSequence;
    
    public int soundOnlineIndex=0;
    public String soundOnline="";
    public String soundOnlineType=none;    

    public int soundOfflineIndex=0;
    public String soundOffline="";
    public String soundOfflineType=none;
    
    public int soundForYouIndex=1;
    public String soundForYou="";
    public String soundForYouType=toneSequence;
    
    public int soundComposingIndex=0;
    public String soundComposing="";
    public String soundComposingType=none;
    
    public int soundConferenceIndex=1;
    public String soundConference="";
    public String soundConferenceType=toneSequence;
//#ifdef JUICK.COM
    public int soundBlogIndex=0;
    public String soundBlog="";
    public String soundBlogType="none";
//#endif
    public int soundStartUpIndex=1;
    public String soundStartUp="";
    public String soundStartUpType=toneSequence;
    
    public int soundOutgoingIndex=1;
    public String soundOutgoing="";
    public String soundOutgoingType=none;
    
    public int soundVIPIndex=1;
    public String soundVIP="";
    public String soundVIPType=toneSequence;
    
    public boolean vibrateOnlyHighlited = true;

    public int soundVol=50;
    public int vibraLen=300;
    public int vibraRepeatCount=2;
    public int vibraRepeatPause=200;
    
    public boolean enableAttention=true;
    public int soundAttentionIndex=1;
    public String soundAttention="";
    public String soundAttentionType=toneSequence;

    private static int size=0;
    
    // Singleton
    private static AlertCustomize instance;

    private Vector files[]=new StringLoader().stringLoader("/sounds/res.txt", 3);

    //public boolean flashBackLight;
    
    public static AlertCustomize getInstance(){
	if (instance==null) {
	    instance=new AlertCustomize();
            instance.setSize();
	    instance.loadFromStorage();
	}
	return instance;
    }
    
    
    
    protected void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("AlertCustomize", 0);
        try {
            soundVol=inputStream.readInt();
            vibraLen=inputStream.readInt();
            vibraRepeatCount=inputStream.readInt();
            vibraRepeatPause=inputStream.readInt();
	    soundsMsgIndex=inputStream.readInt();            
            soundOnlineIndex=inputStream.readInt();
            soundOfflineIndex=inputStream.readInt();
            soundForYouIndex=inputStream.readInt();
            soundComposingIndex=inputStream.readInt();
            soundConferenceIndex=inputStream.readInt();
//#ifdef JUICK.COM
            soundBlogIndex=inputStream.readInt();
//#endif
	    soundStartUpIndex=inputStream.readInt();
	    soundOutgoingIndex=inputStream.readInt();
            soundVIPIndex=inputStream.readInt();
            
            vibrateOnlyHighlited=inputStream.readBoolean();
            inputStream.readBoolean(); //flashBackLight
	    soundAttentionIndex=inputStream.readInt();
            enableAttention=inputStream.readBoolean();
            inputStream.close();
            inputStream=null;
	} catch (Exception e) {
            try {
                if (inputStream!=null) {
                    inputStream.close();
                    inputStream=null;
                }
            } catch (IOException ex) {}
	}

        instance.updateSoundNames();
    }
    
    public void saveToStorage(){
	try {
            DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
            
            outputStream.writeInt(soundVol);
            outputStream.writeInt(vibraLen);
            outputStream.writeInt(vibraRepeatCount);
            outputStream.writeInt(vibraRepeatPause);
	    outputStream.writeInt(soundsMsgIndex);            
	    outputStream.writeInt(soundOnlineIndex);
	    outputStream.writeInt(soundOfflineIndex);
	    outputStream.writeInt(soundForYouIndex);
            outputStream.writeInt(soundComposingIndex);
            outputStream.writeInt(soundConferenceIndex);
//#ifdef JUICK.COM
            outputStream.writeInt(soundBlogIndex);
//#endif
	    outputStream.writeInt(soundStartUpIndex);
	    outputStream.writeInt(soundOutgoingIndex);
            outputStream.writeInt(soundVIPIndex);
            
            outputStream.writeBoolean(vibrateOnlyHighlited);
            outputStream.writeBoolean(false); //flashBackLight
            
            outputStream.writeInt(soundAttentionIndex);
            outputStream.writeBoolean(enableAttention);
            NvStorage.writeFileRecord(outputStream, "AlertCustomize", 0, true);
	} catch (IOException e) {
            //e.printStackTrace();
        }

        instance.updateSoundNames();
    }
    
    public void setSize(){
        size=files[0].size();
    }

    public void updateSoundNames() {
        if (soundsMsgIndex>=size) soundsMsgIndex=0;
	messageSndType=(String) files[0].elementAt(soundsMsgIndex);
	messagesnd=(String) files[1].elementAt(soundsMsgIndex);

        if (soundOnlineIndex>=size) soundOnlineIndex=0;
	soundOnlineType=(String) files[0].elementAt(soundOnlineIndex);
	soundOnline=(String) files[1].elementAt(soundOnlineIndex);

        if (soundOfflineIndex>=size) soundOfflineIndex=0;
	soundOfflineType=(String) files[0].elementAt(soundOfflineIndex);
	soundOffline=(String) files[1].elementAt(soundOfflineIndex);

        if (soundForYouIndex>=size) soundForYouIndex=0;
	soundForYouType=(String) files[0].elementAt(soundForYouIndex);
	soundForYou=(String) files[1].elementAt(soundForYouIndex);

        if (soundComposingIndex>=size) soundComposingIndex=0;
	soundComposingType=(String) files[0].elementAt(soundComposingIndex);
	soundComposing=(String) files[1].elementAt(soundComposingIndex);

        if (soundConferenceIndex>=size) soundOnlineIndex=0;
	soundConferenceType=(String) files[0].elementAt(soundConferenceIndex);
	soundConference=(String) files[1].elementAt(soundConferenceIndex);
//#ifdef JUICK.COM
        if (soundBlogIndex>=size) soundBlogIndex=0;
        soundBlogType=(String) files[0].elementAt(soundBlogIndex);
        soundBlog=(String) files[1].elementAt(soundBlogIndex);
//#endif 
        if (soundStartUpIndex>=size) soundStartUpIndex=0;
	soundStartUpType=(String) files[0].elementAt(soundStartUpIndex);
	soundStartUp=(String) files[1].elementAt(soundStartUpIndex);

        if (soundOutgoingIndex>=size) soundOnlineIndex=0;
	soundOutgoingType=(String) files[0].elementAt(soundOutgoingIndex);
	soundOutgoing=(String) files[1].elementAt(soundOutgoingIndex);

        if (soundVIPIndex>=size) soundVIPIndex=0;
	soundVIPType=(String) files[0].elementAt(soundVIPIndex);
	soundVIP=(String) files[1].elementAt(soundVIPIndex);

        if (soundAttentionIndex>=size) soundAttentionIndex=0;
	soundAttentionType=(String) files[0].elementAt(soundAttentionIndex);
	soundAttention=(String) files[1].elementAt(soundAttentionIndex);
    }
}
