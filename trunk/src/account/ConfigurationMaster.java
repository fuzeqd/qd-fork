/*
 * ConfigurationMaster.java

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

package account;

import client.Config;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.MultiLine;

public final class ConfigurationMaster extends DefForm {
    private static final int PAGES_COUNT = 3;

    private static final int SIMPLE = 1;
    private static final int MEDIUM = 2;
    private static final int DETAILED = 3;

    private DropChoiceBox contactsSettings;
    private DropChoiceBox chatSettings;
    private DropChoiceBox uiSettings;

    private int currentPage = 1;

    public ConfigurationMaster() {
        super("");

        updateForm();
    }

    private void updateForm() {
        StringBuffer buf = new StringBuffer(SR.get(SR.MS_CONFIGURATION_MASTER));
        buf.append(" (");
        buf.append(currentPage);
        buf.append("/");
        buf.append(PAGES_COUNT);
        buf.append(")");

        /* Вид контактов (шаг 1/3) */
        setMainBarItem(new MainBar(buf.toString()));

        itemsList = null;
        itemsList = new Vector();

        switch (currentPage) {
            case 1:
                /*if (contactsSettings == null) {
                    contactsSettings = new DropChoiceBox("Contacts settings");
                    fillChoiceBox(contactsSettings);
                }
                addControl(contactsSettings);
                simpleContacts = new CheckBox("Simple contacts", true);
                addControl(simpleContacts);

                mediumContacts = new CheckBox("Medium contacts", false);
                addControl(mediumContacts);*/
                MultiLine line;

                line = new MultiLine(
                        "Simple contacts",
                        "Don't show avatars, client icons, status string, extended statuses, contact resources",
                        width);
                line.setSelectable(true);
                addControl(line);

                line = new MultiLine(
                        "Medium contacts",
                        "Show only client icons, extended statuses",
                        width);
                line.setSelectable(true);
                addControl(line);

                line = new MultiLine(
                        "Medium contacts",
                        "Show avatars, client icons, status string, extended statuses, contact resources",
                        width);
                line.setSelectable(true);
                addControl(line);
                break;
            case 2:
                if (chatSettings == null) {
                    chatSettings = new DropChoiceBox("Chat settings");
                    fillChoiceBox(chatSettings);
                }
                addControl(chatSettings);
                break;
            case 3:
                if (uiSettings == null) {
                    uiSettings = new DropChoiceBox("UI settings");
                    fillChoiceBox(uiSettings);
                }
                addControl(uiSettings);
                break;
        }
        redraw();
    }

    private void applySettings() {
        switch (currentPage) {
            case 1:
                switch (contactsSettings.getValue()) {
                    case SIMPLE:
                        Config.simpleContacts = true;
                        Config.auto_queryPhoto = false;
                        Config.showResources = false;
                        break;
                    case MEDIUM:
                        Config.simpleContacts = false;
                        Config.rosterStatus = false;
                        Config.showClientIcon = true;
                        Config.auto_queryPhoto = false;
                        Config.showResources = false;
                        break;
                    case DETAILED:
                        Config.simpleContacts = false;
                        Config.rosterStatus = true;
                        Config.showClientIcon = true;
                        Config.auto_queryPhoto = true;
                        Config.showResources = true;
                        break;
                }
                break;
            case 2:
                switch (contactsSettings.getValue()) {
                    case SIMPLE:
                        Config.showNickNames = false;
                        Config.storeConfPresence = false;
                        Config.hideMessageIcon = false;
                        Config.showTimeInMsgs = false;
                        Config.showCollapsedPresences = true;
                        break;
                    case MEDIUM:
                        Config.showNickNames = false;
                        Config.storeConfPresence = true;
                        Config.hideMessageIcon = true;
                        Config.showTimeInMsgs = true;
                        Config.showCollapsedPresences = true;
                        break;
                    case DETAILED:
                        Config.showNickNames = true;
                        Config.storeConfPresence = true;
                        Config.hideMessageIcon = true;
                        Config.showTimeInMsgs = true;
                        Config.showCollapsedPresences = true;
                }
                break;
            case 3:
                switch (uiSettings.getValue()) {
                    case SIMPLE:
                        Config.panelsState = 0;
                        Config.showTimeTraffic = false;
                        Config.popUps = false;
                        Config.showBalloons = false;
                        Config.gradient_cursor = false;
                        break;
                    case MEDIUM:
                        Config.panelsState = 2;
                        Config.showTimeTraffic = false;
                        Config.popUps = true;
                        Config.showBalloons = false;
                        Config.gradient_cursor = true;
                        break;
                    case DETAILED:
                        Config.panelsState = 2;
                        Config.showTimeTraffic = true;
                        Config.popUps = true;
                        Config.showBalloons = true;
                        Config.gradient_cursor = true;
                        break;
                }
                VirtualList.updatePanelsState();
        }
        Config.getInstance().saveToStorage();
    }

    public String touchLeftCommand() {
        return (currentPage == PAGES_COUNT) ?
                "Done" : "Next";
    }

    public void cmdOk() {
        applySettings();

        if (currentPage == PAGES_COUNT) {
            destroyView();
        } else {
            ++currentPage;
            updateForm();
        }
    }

    public void cmdCancel() {
        if (currentPage == 1) {
            destroyView();
        } else {
            --currentPage;
            updateForm();
        }
    }

    private void fillChoiceBox(DropChoiceBox box) {
        box.append("Current settings");
        box.append("Simple");
        box.append("Medium");
        box.append("Detailed");
    }

    private String constructTip() {
        StringBuffer buf = new StringBuffer();

        switch (currentPage) {
            case 1:
                buf.append("Simple").append(": ");
                buf.append("don't show avatars, client icons, status string, extended statuses, contact resources");
                buf.append("\n\n");
                buf.append("Medium").append(": ");
                buf.append("show only client icons, extended statuses");
                buf.append("\n\n");
                buf.append("Detailed").append(": ");
                buf.append("show avatars, client icons, status string, extended statuses, contact resources");
                break;
        }

        return buf.toString();
    }
}
