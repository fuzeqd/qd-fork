/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.controls.form;

//#ifdef USER_KEYS
import client.Config;
import colors.ColorTheme;
import font.FontCache;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.EOFException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualList;
//import ui.keys.UserKeyExec;
//import javax.microedition.lcdui.*;

/**
 *
 * @author Mars
 */
public class KeyScanner extends IconTextElement{
    protected String caption;

    public String text;
    private int boxType;

    private Font font;
    private Font captionFont;
    private int fontHeight;
    private int captionFontHeight;

    private int colorItem;
    private int colorBorder;
    private int colorBGnd;

    public boolean selected;
    public int keyCode;

    public KeyScanner(String caption) {
        super( null);
        if( caption ==null)
            this.caption= "";
        else
            this.caption= caption;

        colorItem = ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        colorBorder = ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
        colorBGnd = ColorTheme.getColor(ColorTheme.LIST_BGND);

        font = FontCache.getFont(false, Config.rosterFont);
        fontHeight = font.getHeight();
        itemHeight = fontHeight;

        captionFont = FontCache.getFont(true, Config.rosterFont);
        captionFontHeight = captionFont.getHeight();
        itemHeight += captionFontHeight;
    }

    public KeyScanner(int keyCode, String caption) {
        this( caption);
        this.keyCode = keyCode;
        if( selected)
            text= ui.keys.UserKey.getKeyName( keyCode);
        else
            text= "press your key";
    }

    public int getCaptionLength() {
        if (caption == null) {
            return 0;
        }
        if (caption.length() == 0) {
            return 0;
        }
        return captionFont.stringWidth(caption);
    }

    public int getTextLength() {
        if (text == null) {
            return 0;
        }
        if (text.length() == 0) {
            return 0;
        }
        return font.stringWidth(text);
    }

    public String toString() {
        return getValue();
    } //Tishka17

    public void onSelect( VirtualList view){
        if( !selected){
            selected= true;
            text= "press your key";
        }
    }

    public boolean handleEvent( int keyCode){
        if( selected){
            this.keyCode= keyCode;
            text= ui.keys.UserKey.getKeyName( keyCode);
            selected = false;
        }
        return false;
    }

    public int getKeyCode( ){
        return keyCode;
    }

    public String getValue() {
        return text;
    }

    public int getVHeight() {
        return itemHeight;
    }

    public int getVWidth() {
        if (caption != null) {
            return captionFont.stringWidth(caption);
        }
        return -1;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int width = g.getClipWidth();
        int height = fontHeight;

        int oldColor = g.getColor();

        int xOffset = 0;
        int baseOffset = getOffset();

        int y = 0;
        if (caption != null) {
            xOffset = (getCaptionLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(captionFont);
            g.drawString(caption, xOffset, y, Graphics.TOP | Graphics.LEFT);
            y = captionFontHeight;
        }

        if (text.length() == 0) {
            width = width - midlet.BombusQD.cf.scrollWidth - 5;
            g.setColor(colorBGnd);
            g.fillRect(5, y, width, height - 3);

            g.setColor((sel) ? colorBorder : colorItem);
            g.drawRoundRect(5, y, width, height - 3, 8, 8); //Tishka17
        }

        g.setColor(oldColor);

        if (getTextLength() > 0) {
            xOffset = (getTextLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(font);
            g.drawString(toString(), xOffset, y, Graphics.TOP | Graphics.LEFT);
        }
    }

    public boolean isSelectable() {
        return true;
    }
}
//#endif
