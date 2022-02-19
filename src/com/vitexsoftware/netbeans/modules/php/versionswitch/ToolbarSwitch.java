/**
 * PHPSwitch ToolbarButton class
 *
 * @author Vítězslav Dvořák <info@vitexsoftware.cz>
 * @copyright 2021-2022 Vitex@hippy.cz (G)
 */
package com.vitexsoftware.netbeans.modules.php.versionswitch;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.ToolbarPool;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
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
        //        iconBase = "com/vitexsoftware/netbeans/modules/php/versionswitch/phpx.png",
        displayName = "#CTL_ToolbarSwitch",
        lazy = false
)
@ActionReference(path = "Toolbars/ToolbarPool", position = 2000)

public class ToolbarSwitch extends AbstractAction implements Presenter.Toolbar {

    private JButton button;

//    @Override
//    public boolean accept(Object sender) {
//        return super.accept(sender); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() != null) {
            ToolbarPool.getDefault().setConfiguration(e.getActionCommand());
            setPhpVersion(e.getActionCommand());
        } else {
            String phpInfoFile = null;
            try {
                phpInfoFile = generatePhpInfo();
            } catch (InterruptedException | IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                URLDisplayer.getDefault().showURL(new URL(phpInfoFile));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public Component getToolbarPresenter() {
        if (button == null) {
            final JPopupMenu popup = new JPopupMenu();

            List<String> phps = PHPSwitch.phpVersionsAvailble();

            for (String ver : phps) {
                popup.add(ver).addActionListener(ToolbarSwitch.this);

            }

            button = DropDownButtonFactory.createDropDownButton(
                    ImageUtilities.loadImageIcon("com/vitexsoftware/netbeans/modules/php/versionswitch/php" + PHPSwitch.phpver + ".png", false),
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
     * File with phpinfo
     *
     * @return
     */
    private String generatePhpInfo() throws InterruptedException, IOException {
        Process process;
        String phpInfoPath = System.getProperty("java.io.tmpdir") + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "phpinfo.html";
        String phpInfo = "";
        
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("php-cgi -f " + savePhpInfo());

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

// Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            phpInfo = phpInfo + s;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(phpInfoPath))) {
            writer.write(phpInfo);
            writer.close();
        }
        
        System.out.println("PHPinfo saved to: " + phpInfoPath);
        return "file://" + phpInfoPath;
    }

    private String savePhpInfo() throws IOException {
        String phpInfoCode = System.getProperty("java.io.tmpdir") + "/phpinfo.php";
        String code = "<?php phpinfo();";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(phpInfoCode))) {
            writer.write(code);
            writer.close();
        }
        return phpInfoCode;
    }

    /**
     * PHP Version switcher itself
     *
     * @param ver "5.6" to "8.1"
     *
     * @return is requested version active ?
     */
    public static boolean switchToVersion(String ver) {
        Process process;
        String line;
        try {
            process = Runtime.getRuntime().exec("usephp " + ver);
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
