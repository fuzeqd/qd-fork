/*
 * Strconv.java
 *
 * Created on 12.01.2005, 1:25
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

/**
 *
 * @author Eugene Stahov
 */
package util;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Strconv {
    private Strconv() {}

    public static byte[] stringToByteArray(String val) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeUTF(val);
                  byte[] raw = baos.toByteArray();
                  byte[] result = new byte[raw.length - 2];
                System.arraycopy(raw, 2, result, 0, raw.length - 2);
                raw = null;
                raw = new byte[0];
                dos.close();
                baos.close();
                return result;
            } catch (Exception e) {
                // Do nothing
            }
        return null;
    }

    public static String convCp1251ToUnicode(final String s){
        if (s==null) {
            return null;
        }
        int len = s.length();
        StringBuffer b=new StringBuffer(len);
        for (int i = 0; i < len; ++i){
            char ch=s.charAt(i);
            if (ch>0xbf) {
                ch+=0x410-0xc0;
            }
            // Ё
            if (ch==0xa8) {
                ch=0x401;
            }
            // ё
            if (ch==0xb8) {
                ch=0x451;
            }
            b.append(ch);
        }
        return b.toString();
    }

    public static String convUnicodeToCp1251(final String s){
        if (s==null) {
            return null;
        }
        int len = s.length();
        StringBuffer b = new StringBuffer(len);
        for (int i = 0; i < len; ++i){
            char ch=s.charAt(i);
            // Ё
            if (ch==0x401) {
                ch=0xa8;
            }
            // ё
            if (ch==0x451) {
                ch=0xb8;
            }
            if (ch>0x409) ch+=0xc0-0x410;
            b.append(ch);
        }
        return b.toString();
    }

    public static String toBase64(byte source[], int len) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

        if (len<0) len=source.length;
        char[] out = new char[((len+2)/3)<<2];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;

            int val = (0xFF & source[i])<<8;
            if ((i+1) < len) {
                val |= (0xFF & source[i+1]);
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source[i+2]);
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }

    public static String toBase64(String source) {
        return toBase64(source.getBytes(), source.length());
    }

    public static StringBuffer toUTFSb(StringBuffer str) {
        int srcLen = str.length();
        StringBuffer outbuf=new StringBuffer( srcLen );
        int c = 0;
         for(int i=0; i < srcLen; ++i) {
            c = (int)str.charAt(i);

            if ((c >= 1) && (c <= 0x7f)) {
                 outbuf.append( (char) c);

            }
             if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
                outbuf.append((char)(0xc0 | (0x1f & (c >> 6))));
                outbuf.append((char)(0x80 | (0x3f & c)));
            }
             if ((c >= 0x800) && (c <= 0xffff)) {
                outbuf.append(((char)(0xe0 | (0x0f & (c >> 12)))));
                outbuf.append((char)(0x80 | (0x3f & (c >>  6))));
                outbuf.append(((char)(0x80 | (0x3f & c))));
             }
         }
         return outbuf;
     }

    public static byte[] fromBase64(String s) {
        return baosFromBase64(s).toByteArray();
    }

    private static ByteArrayOutputStream baosFromBase64(String s) {
        int padding=0;
        int ibuf=1;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(2048);
        for (int i=0; i<s.length(); ++i) {
            int nextChar = s.charAt(i);
            //if( nextChar == -1 )
            //    throw new EndOfXMLException();
            int base64=-1;
            if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
            else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
            else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
            else if (nextChar=='+') base64=62;
            else if (nextChar=='/') base64=63;
            else if (nextChar=='=') {base64=0; padding++;} else if (nextChar=='<') break;
            if (base64>=0) ibuf=(ibuf<<6)+base64;
            if (ibuf>=0x01000000){
                baos.write((ibuf>>16) &0xff);                   //00xx0000 0,1,2 =
                if (padding<2) baos.write((ibuf>>8) &0xff);     //0000xx00 0,1 =
                if (padding==0) baos.write(ibuf &0xff);         //000000xx 0 =
                //len+=3;
                ibuf=1;
            }
        }
        try { baos.close(); } catch (Exception e) {};
        return baos;
    }

    public static String unicodeToUTF(String src) {
        return toUTFSb(new StringBuffer(src)).toString();
    }

    public static String toLowerCase(String src){
        StringBuffer dst = new StringBuffer(src);
        for (int i=0; i < dst.length(); ++i) {
            char c = dst.charAt(i);
            // default latin chars
            if (c>'A'-1 && c<'Z'+1) {
                c+='a'-'A';
            }
            // cyrillic chars
            if (c>0x40f && c<0x430) {
                c+=0x430-0x410;
            }
            // TODO: other schemes by request
            dst.setCharAt(i, c);
        }
        return dst.toString();
    }
}
