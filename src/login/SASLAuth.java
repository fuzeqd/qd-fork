/* 
 * SASLAuth.java
 *
 * Created on 8.07.2006, 23:34
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

package login;

import account.Account;
//#if TLS || Android
//# import client.StaticData;
//#endif
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.MD5;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import locale.SR;
import xmpp.XmppError;
import util.Strconv;

/**
 *
 * @author evgs
 */
public class SASLAuth implements JabberBlockListener{

    private LoginListener listener;
    private Account account;
    private SASL_ScramSha1 scramSHA1;
    
    /** Creates a new instance of SASLAuth */
    public SASLAuth(Account account, LoginListener listener) {
        
        this.listener=listener;
        this.account=account;
        JabberStream stream=midlet.BombusQD.sd.roster.theStream;
        stream.addBlockListener(this);
        //this.stream=stream;
        //if (stream!=null) stream.addBlockListener(this);
        //listener.loginMessage(SR.get(SR.MS_SASL_STREAM));
    }
    
    public void destroy() {
        listener = null;
        account = null;
    }
    
//#if SASL_XGOOGLETOKEN
    private String token;
    public void setToken(String token) { this.token=token; }
//#endif

    public int blockArrived(JabberDataBlock data) {
        JabberStream stream=midlet.BombusQD.sd.roster.theStream;
        //System.out.println(data.toString());
        if (data.getTagName().equals("stream:features")) {
//#if TLS || Android
//#             JabberDataBlock starttls=data.getChildBlock("starttls");
//#             if (!account.useCompression() && starttls!=null && starttls.isJabberNameSpace("urn:ietf:params:xml:ns:xmpp-tls")) {
//#                 JabberDataBlock askTls=new JabberDataBlock("starttls", null, null);
//#                 askTls.setNameSpace("urn:ietf:params:xml:ns:xmpp-tls");
//#                 stream.send(askTls);
//#                 StaticData.getInstance().roster.setProgress("TLS negotiation", 39);
//#                 return JabberBlockListener.BLOCK_PROCESSED;
//#             }
//#endif
//#ifdef ZLIB
            JabberDataBlock compr=data.getChildBlock("compression");
            if (compr!=null && account.useCompression()) {
                if (compr.getChildBlockByText("zlib")!=null) {
                    // negotiating compression
                    JabberDataBlock askCompr=new JabberDataBlock("compress", null, null);
                    askCompr.setNameSpace("http://jabber.org/protocol/compress");
                    askCompr.addChild("method", "zlib");
                    stream.send(askCompr);
                    listener.loginMessage(SR.get(SR.MS_ZLIB), 43);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
            }
//#endif            
            JabberDataBlock mech=data.getChildBlock("mechanisms");
            if (mech!=null) {
                // first stream - step 1. selecting authentication mechanism
                //common body
                JabberDataBlock auth=new JabberDataBlock("auth", null,null);
                auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
                
                if (mech.getChildBlockByText("SCRAM-SHA-1")!=null && !account.getPlainAuth()) {
                    auth.setAttribute("mechanism", "SCRAM-SHA-1");
                    scramSHA1 = new SASL_ScramSha1();
                    auth.setText(util.Strconv.toBase64 (scramSHA1.init(
                            Strconv.unicodeToUTF(account.getBareJid()),
                            account.getPassword()
                    )));
                    stream.send(auth);
                    listener.loginMessage(SR.get(SR.MS_AUTH), 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                // DIGEST-MD5 mechanism
                if (mech.getChildBlockByText("DIGEST-MD5")!=null && !account.getPlainAuth()) {
                    auth.setAttribute("mechanism", "DIGEST-MD5");
                    stream.send(auth);
                    listener.loginMessage(SR.get(SR.MS_AUTH), 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                
//#if SASL_XGOOGLETOKEN
                // X-GOOGLE-TOKEN mechanism
                if (mech.getChildBlockByText("X-GOOGLE-TOKEN")!=null  && token!=null) {
                    auth.setAttribute("mechanism", "X-GOOGLE-TOKEN");
                    auth.setText(token);
                    stream.send(auth);
                    listener.loginMessage(SR.get(SR.MS_AUTH), 42);
                    return JabberBlockListener.BLOCK_PROCESSED;    
                }
//#endif

                if (mech.getChildBlockByText("PLAIN")!=null) {
                    if (!account.getPlainAuth()) {
                        listener.loginFailed("SASL: Plain auth required");
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    }
                    auth.setAttribute("mechanism", "PLAIN");
                    /* try {
                        byte []a = Strconv.unicodeToUTF(account.getBareJid().getBytes("utf-8");
                        byte []b = account.getUserName().getBytes("utf-8");
                        byte []c = account.getPassword().getBytes("utf-8");
                        final byte[] result = new byte[a.length+1+b.length+1+c.length];
                        int idx = 0;
                        System.arraycopy(a, 0, result, 0, a.length);
                        idx += a.length;
                        result[idx] = 0x0;
                        idx++;
                        System.arraycopy(b, 0, result, idx, b.length);
                        idx += b.length;
                        result[idx] = 0x0;
                        idx++;
                        System.arraycopy(c, 0, result, idx, c.length);
                        idx += c.length;
                        auth.setText(Strconv.toBase64(result, idx));
                    } catch (UnsupportedEncodingException e){} */
                    String plain =
                            Strconv.unicodeToUTF(account.getBareJid())
                            + (char) 0x00
                            + Strconv.unicodeToUTF(account.getUserName())
                            + (char) 0x00
                            + Strconv.unicodeToUTF(account.getPassword());
                    auth.setText(Strconv.toBase64(plain));

                    stream.send(auth);
                    listener.loginMessage(SR.get(SR.MS_AUTH), 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                // no more method found
                listener.loginFailed("SASL: Unknown mechanisms");
                return JabberBlockListener.NO_MORE_BLOCKS;
                
            } 
            // second stream - step 1. binding resource
            else if (data.getChildBlock("bind")!=null) {
                JabberDataBlock bindIq=new Iq(null, Iq.TYPE_SET, "bind");
                JabberDataBlock bind=bindIq.addChildNs("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                bind.addChild("resource", account.getResource());
                stream.send(bindIq);

                listener.loginMessage(SR.get(SR.MS_RESOURCE_BINDING), 44);
                
                return JabberBlockListener.BLOCK_PROCESSED;
            }
            
//#ifdef NON_SASL_AUTH
//#             if (data.findNamespace("auth", "http://jabber.org/features/iq-auth")!=null) {
//#                 new NonSASLAuth(account, listener, stream);
//#                 return JabberBlockListener.NO_MORE_BLOCKS;
//#             }
//#endif            
            
            //fallback if no known authentication methods were found
            listener.loginFailed("No known authentication methods");

            return JabberBlockListener.NO_MORE_BLOCKS; 
        } else if (data.getTagName().equals("challenge")) {
            // first stream - step 2,3. reaction to challenges
            
            String challenge=decodeBase64(data.getText());
            
            JabberDataBlock resp=new JabberDataBlock("response", null, null);
            resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
            if (scramSHA1!=null) {
                resp.setText(Strconv.toBase64(scramSHA1.response(challenge)));
            } else {
                int nonceIndex=challenge.indexOf("nonce=");
                    // first stream - step 2. generating DIGEST-MD5 response due to challenge
                if (nonceIndex>=0) {
                    nonceIndex+=7;
                    String nonce=challenge.substring(nonceIndex, challenge.indexOf('\"', nonceIndex));
                    String cnonce="123456789abcd";
                    resp.setText(responseMd5Digest(
                            account.getUserName(),
                            account.getPassword(),
                            account.getServer(),
                            "xmpp/"+account.getServer(),
                            nonce,
                            cnonce ));
                }
            }
                // first stream - step 3. sending second empty response due to second challenge
            //if (challenge.startsWith("rspauth")) {}
                
            stream.send(resp);
            return JabberBlockListener.BLOCK_PROCESSED;
        }
//#if TLS || Android
//#         else if ( data.getTagName().equals("proceed")) {
//#             try {
//#                 stream.setTls();
//#                 stream.initiateStream();
//#             } catch (IOException ex) {
//#                 //ex.printStackTrace();
//#                 listener.loginFailed("TLS negotiation failed: " + ex.getMessage());
//#             }
//#             return JabberBlockListener.NO_MORE_BLOCKS;
//#         }
//#         else if ( data.getTagName().equals("failure") && data.isJabberNameSpace("urn:ietf:params:xml:ns:xmpp-tls")) {
//#             listener.loginFailed("TLS failed");
//#         }
//#endif
//#if ZLIB
        else if ( data.getTagName().equals("compressed")) {
                stream.setZlibCompression();
            try {
                stream.initiateStream();
            } catch (IOException ex) { }
            return JabberBlockListener.NO_MORE_BLOCKS;
        }
        
//#endif
            
        else if ( data.getTagName().equals("failure")) {
            // first stream - step 4a. not authorized
            listener.loginFailed( XmppError.decodeSaslError(data).toString() );
        } else if ( data.getTagName().equals("success")) {
            if (scramSHA1!=null) {
                if (!scramSHA1.success(new String(Strconv.fromBase64(data.getText())))) {
                    listener.loginFailed("Server answer not valid");
                }
            }
            // first stream - step 4b. success.
            try {
                stream.initiateStream();
            } catch (IOException ex) { }
            return JabberBlockListener.NO_MORE_BLOCKS; // at first stream
        }

        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                // second stream - step 2. resource binded - opening session
                if (data.getAttribute("id").equals("bind")) {
                    String myJid=data.getChildBlock("bind").getChildBlockText("jid");
                    listener.bindResource(myJid);
                    JabberDataBlock session=new Iq(null, Iq.TYPE_SET, "sess");
                    session.addChildNs("session", "urn:ietf:params:xml:ns:xmpp-session");
                    stream.send(session);
                    listener.loginMessage(SR.get(SR.MS_SESSION), 45);
                    return JabberBlockListener.BLOCK_PROCESSED;
                    
                // second stream - step 3. session opened - reporting success login
                } else if (data.getAttribute("id").equals("sess")) {
                    listener.loginSuccess();
                    return JabberBlockListener.NO_MORE_BLOCKS;
                    //return JabberBlockListener.BLOCK_PROCESSED;
                }
            }
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    public static String decodeBase64(String src)  {
        int len=0;
        int ibuf=1;
        StringBuffer out=new StringBuffer(0);
        int length = src.length();
        for (int i=0; i<length; i++) {
            int nextChar = src.charAt(i);
            int base64=-1;
            if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
            else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
            else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
            else if (nextChar=='+') base64=62;
            else if (nextChar=='/') base64=63;
            else if (nextChar=='=') {base64=0; len++;} else if (nextChar=='<') break;
            if (base64>=0) ibuf=(ibuf<<6)+base64;
            if (ibuf>=0x01000000){
                out.append( (char)((ibuf>>16) &0xff) );
                if (len<2) out.append( (char)((ibuf>>8) &0xff) );
                if (len==0) out.append( (char)(ibuf &0xff) );
                //len+=3;
                ibuf=1;
            }
        }
        return out.toString();
    }

    /**
     * This routine generates MD5-DIGEST response via SASL specification
     * @param user
     * @param pass
     * @param realm
     * @param digest_uri
     * @param nonce
     * @param cnonce
     * @return
     */
    private String responseMd5Digest(String user, String pass, String realm, String digestUri, String nonce, String cnonce) {
        try {
            MD5 hUserRealmPass=new MD5();
            hUserRealmPass.init();
            hUserRealmPass.update(user.getBytes("utf-8"));
            hUserRealmPass.update((byte)':');
            hUserRealmPass.update(realm.getBytes("utf-8"));
            hUserRealmPass.update((byte)':');
            hUserRealmPass.update(pass.getBytes("utf-8"));
            hUserRealmPass.finish();

            MD5 hA1=new MD5();
            hA1.init();
            hA1.update(hUserRealmPass.getDigestBits());
            hA1.update((byte)':');
            hA1.updateASCII(nonce);
            hA1.update((byte)':');
            hA1.updateASCII(cnonce);
            hA1.finish();

            MD5 hA2=new MD5();
            hA2.init();
            hA2.updateASCII("AUTHENTICATE:");
            hA2.updateASCII(digestUri);
            hA2.finish();

            MD5 hResp=new MD5();
            hResp.init();
            hResp.updateASCII(hA1.getDigestHex());
            hResp.update((byte)':');
            hResp.updateASCII(nonce);
            hResp.updateASCII(":00000001:");
            hResp.updateASCII(cnonce);
            hResp.updateASCII(":auth:");
            hResp.updateASCII(hA2.getDigestHex());
            hResp.finish();

            String tmp1 = "username=\"";
            String tmp2 = "\",realm=\"";
            String tmp3 = "\"," +
                    "nonce=\""+nonce+"\",nc=00000001,cnonce=\""+cnonce+"\"," +
                    "qop=auth,digest-uri=\""+digestUri+"\"," +
                    "response=\""+hResp.getDigestHex()+"\",charset=utf-8";
            byte []a = user.getBytes("utf-8");
            byte []b = realm.getBytes("utf-8");
            final byte[] result = new byte[tmp1.length()+a.length+tmp2.length()+b.length+tmp3.length()];
            int idx = 0;
            System.arraycopy(tmp1.getBytes(), 0, result, 0, tmp1.length());
            idx += tmp1.length();
            System.arraycopy(a, 0, result, idx, a.length);
            idx += a.length;
            System.arraycopy(tmp2.getBytes(), 0, result, idx, tmp2.length());
            idx += tmp2.length();
            System.arraycopy(b, 0, result, idx, b.length);
            idx += b.length;
            System.arraycopy(tmp3.getBytes(), 0, result, idx, tmp3.length());
            idx += tmp3.length();
            return Strconv.toBase64(result, idx);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
