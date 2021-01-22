/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vitexsoftware.netbeans.modules.php.versionswitch;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.ToolbarPool;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 * Switcher widget code
 * 
 * @author vitex
 */

@ActionID(category = "Window",
        id = "com.vitexsoftware.netbeans.modules.php.versionswitch.ToolbarSwitch"
)
@ActionRegistration(
        iconBase = "com/vitexsoftware/netbeans/modules/php/versionswitchphpx.png",
        displayName = "#CTL_ToolbarSwitch",
        lazy = false
)
@ActionReference(path = "Toolbars/ToolbarPool", position = 2000)
@Messages({
    "CTL_ToolbarSwitch=PHP Version",
    "PHPVersions.no_version=(no PHP Version)"
})

public class ToolbarSwitch extends AbstractAction implements Presenter.Toolbar {

    private JButton button;

//    @Override
//    public boolean accept(Object sender) {
//        return super.accept(sender); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == null) {
            // show popup menu
            button.mouseDown(null, 30, 30);
        } else {
            ToolbarPool.getDefault().setConfiguration(e.getActionCommand());
            setPhpVersion(e.getActionCommand());
        }

    }

    @Override
    public Component getToolbarPresenter() {
        if (button == null) {
            final JPopupMenu popup = new JPopupMenu();

            List<String> phps = Arrays.asList("5.6", "7.0", "7.1", "7.2", "7.3", "7.4", "8.0");

            for (String ver : phps) {
                popup.add(ver).addActionListener(ToolbarSwitch.this);

            }

            button = DropDownButtonFactory.createDropDownButton(
                    ImageUtilities.loadImageIcon("com/vitexsoftware/netbeans/modules/php/versionswitchphp" + PHPSwitch.phpver + ".png", false),
                    //new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)),
                    popup);

            button.addActionListener(this);
//      Actions.connect(button, a);

            PropertyChangeListener pcl = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {

                }

            };
            popup.addPropertyChangeListener("ancestor", pcl);

//         popup.addSeparator();
//         setProjectGroups(popup);
        }
        return button;

    }

    /**
     * Try to set PHP version to given value
     * 
     * @param ver 
     */
    private void setPhpVersion(String ver) {
        try {
            PHPSwitch.log.info("Try to set PHP version to " + ver);
            boolean switched = switchToVersion(ver);
            if (switched == true) {
                StatusDisplayer.getDefault().setStatusText("Switched to PHP version '" + ver + "'");
                button.setIcon(ImageUtilities.loadImageIcon("com/vitexsoftware/netbeans/modules/php/versionswitch/php" + ver + ".png", false));
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * PHP Version switcher itself
     * 
     * @param ver "5.6" to "8.0"
     * 
     * @return is requested version active ?
     */
    public static boolean switchToVersion(String ver) {
        Process process;
        String line = null;
        try {
            process = Runtime.getRuntime().exec("usephp-" + ver);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        String detected = PHPSwitch.currentPhpVersion();

        return ver.equals(detected);
    }

}
