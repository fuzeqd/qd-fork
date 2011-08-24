/*
 * FormField.java
 *
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
 *
 */
//#ifdef SERVICE_DISCOVERY
package disco;

import com.alsutton.jabber.JabberDataBlock;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.MultiLine;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
import util.StringUtils;

public class FormField {
    private String label;
    private String type;
    private String name;
    public Object formItem;
    boolean hidden;

    public boolean instructions;
    private Vector optionsList;
    private boolean registered;

    public FormField(JabberDataBlock field) {
        name=field.getTagName();
        label=name;
        String body=field.getText();

        if (name.equals("field")) {
            type=field.getTypeAttribute();
            name=field.getAttribute("var");
            label=field.getAttribute("label");
            if (label==null) label=name;
            body=field.getChildBlockText("value");
	    hidden= type.equals("hidden");
            if (type.equals("fixed")) {
                formItem = new MultiLine((label==null?"Label":label), body);
            }
            else if (type.equals("boolean")) {
                boolean set=false;
                JabberDataBlock desc = field.getChildBlock("desc");
                if (body.equals("1") || body.equals("true")) set=true;
                formItem = new CheckBox( label + ( desc==null?"":"%"+desc.getText() ) ,set);
            }
            else if (type.equals("list-single")) {
                DropChoiceBox listsingle=new DropChoiceBox(label);
                optionsList=null;
                optionsList=new Vector(0);
                int size = field.getChildBlocks().size();
                int index = -1;
                for (int i=0; i<size; i++) {
                    JabberDataBlock option=(JabberDataBlock)field.getChildBlocks().elementAt(i);
                    if (option.getTagName().equals("option")) {
                        String value=option.getChildBlockText("value");
                        String label=option.getAttribute("label");//������� �����

                        if (label==null) {//���
                            label=value;
                            listsingle.append(value);
                        }else {
                            listsingle.append(label);
                        }
                        optionsList.addElement(value);
                        index++;
                        //System.out.println("   add->"+label+"->"+value + " INDEX: "+index);
                        if (body.equals(value)) listsingle.setSelectedIndex(index);
                        formItem = listsingle;
                    }
                }
                //[<instructions>Choose a username and password to register with this server</instructions>, <username/>, <password/>]
            }else if (type.equals("list-multi")) {
                DropChoiceBox listmulti=new DropChoiceBox(label);
                optionsList=new Vector(0);
                int size = field.getChildBlocks().size();
                int index = -1;
                for (int i=0; i<size; i++) {
                    JabberDataBlock option=(JabberDataBlock)field.getChildBlocks().elementAt(i);
                    if (option.getTagName().equals("option")) {
                        String value=option.getChildBlockText("value");
                        String label=option.getAttribute("label");
                        if (label==null) {
                            label=value;
                            listmulti.append(value);
                        }else{
                            listmulti.append(label);
                        }
                        optionsList.addElement(value);
                        index++;
                        if (body.equals(value)) listmulti.setSelectedIndex(index);
                       formItem =  listmulti;
                    }
                }
            } else if (type.equals("jid-multi")) {
                StringBuffer jids = new StringBuffer();
                Vector values = field.getChildBlocks();
                if (values != null) {
                    int size = values.size();
                    for (int i = 0; i < size; i++) {
                        jids.append(((JabberDataBlock) values.elementAt(i)).getText().trim()).append('\n');
                    }
                }
                formItem = new TextInput(label, jids.toString().trim(),TextField.ANY);
            // text-single, text-private
            } else {
                if (body.length()>=200) {
                    body=body.substring(0,198);
                }
                if (type.equals("text-private")) {
                    formItem = new PasswordInput(label, body);
                } else {
                    formItem = new TextInput(label, body, TextField.ANY);
                }
            }
        } else {
            // not x-data
            if ( instructions=name.equals("instructions") )

                formItem =new MultiLine("Instructions", body);

            else if ( name.equals("title") )//jud.jabber.ru

                formItem =new MultiLine("Tittle", body);

            else if ( name.equals("registered") ) {
		registered=true;
                formItem =new CheckBox("Remove registration",false);
            }
            else{
                formItem =new TextInput(label, body, TextField.ANY);
            }
        }
        if (name!=null)
        if ( name.equals("key") ) hidden=true;
    }


    public JabberDataBlock constructBlock(){
        if (formItem instanceof MultiLine) {
            return null;
        }
        
        JabberDataBlock j = null;
        if (formItem instanceof TextInput) {
            if (type==null) {
                j=new JabberDataBlock(null, name, ((TextInput)formItem).toString());
            } else {
                j=new JabberDataBlock("field", null, null);
                j.setAttribute("var", name);
                j.setAttribute("type", type);
                String value = ((TextInput) formItem).getValue();
                if (type.equals("jid-multi")) {
                    Vector jids = StringUtils.split(value, '\n');
                    for (int i = 0; i < jids.size(); i++) {
                        j.addChild("value", (String)jids.elementAt(i));
                    }
                } else {
                    j.addChild("value", ((TextInput)formItem).getValue());
                }
            }
        }

        if (formItem instanceof DropChoiceBox || 
                formItem instanceof CheckBox) {
            if (registered) {
                boolean unregister = ((CheckBox)formItem).getValue();
                if (unregister) return new JabberDataBlock("remove", null, null);
                return null;
            }
            //only x:data
                j=new JabberDataBlock("field", null, null);
                j.setAttribute("var", name);
                j.setAttribute("type", type);
                if (optionsList==null) {
                    j.addChild("value", ((CheckBox)formItem).getValue()==true?"1":"0");
                } else if (type.equals("list-multi")) {
                     j.addChild("value", (String)optionsList.elementAt(((DropChoiceBox)formItem).getSelectedIndex()));
                } else  {
                    int index=((DropChoiceBox)formItem).getSelectedIndex();
                    if (index>=0)  j.addChild("value", (String)optionsList.elementAt(index));
                }
        }
        return j;
    }
}
//#endif
