/*
 * Captcha.java
 *
 * Created on 6 Май 2008 г., 1:46
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
 *
 */
 
package xmpp.extensions;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;

/**
 *
 * @author root
 */

public class Captcha implements JabberBlockListener {
    private String from;
    private String id;
    public void destroy() {
    }    

    public Captcha() {

    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Message) {
     
            JabberDataBlock challenge=data.findNamespace("captcha", "urn:xmpp:captcha");
            if (challenge==null) {
                return BLOCK_REJECTED;
            }

            from=data.getAttribute("from");
            id=data.getAttribute("id");

            new XDataForm(data, id, from);

            return BLOCK_PROCESSED;
        }
        
        if (data instanceof Iq) {
            if (!data.getAttribute("id").equals(id)) return BLOCK_REJECTED;
            
            //TODO: error handling
            //if ()
            return BLOCK_PROCESSED;
        }
        
        return BLOCK_REJECTED;
    }
}
