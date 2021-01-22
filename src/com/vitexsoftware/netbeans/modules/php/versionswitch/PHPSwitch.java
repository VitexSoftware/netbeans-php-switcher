/**
 * PHPSwitch module class
 *
 * @author Vítězslav Dvořák <info@vitexsoftware.cz>
 * @copyright 2021 Vitex@hippy.cz (G)
 */
package com.vitexsoftware.netbeans.modules.php.versionswitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.DialogDisplayer;
import org.openide.modules.ModuleInstall;
import org.openide.windows.OnShowing;
import org.openide.windows.WindowManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * Main module code
 * @author vitex
 */
@OnShowing
public class PHPSwitch extends ModuleInstall implements Runnable {

    /**
     * grab package name here
     */
    public static final String CODENAME = "com.vitexsoftware.netbeans.modules.php.versionswitch";

    /**
     * Logger nest
     */
    public static final Logger log = Logger.getLogger("PHPSwitch");

    /**
     * Version of PHP right detected
     */
    public static final String phpver = PHPSwitch.currentPhpVersion();

    /**
     * Introduce plugin into live
     */
    @Override
    public void run() {
        System.out.println("PHPSwitch module loaded with PHP " + PHPSwitch.phpver + " detected");

        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                UpdateHandler.checkAndHandleUpdates();
            }
        });

    }

    /**
     * Obtain current PHP version
     *
     * @return from "5.6" to "8.0"
     */
    public static String currentPhpVersion() {
        Process process;
        String verstring = "";
        String ver = "x";
        try {
            process = Runtime.getRuntime().exec("php -v");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            verstring = reader.readLine();
            PHPSwitch.debug("PHP verstring: " + verstring);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        Pattern pattern = Pattern.compile("^PHP (\\d+[.]\\d+).*");
        Matcher matcher = pattern.matcher(verstring);
        if (matcher.find()) {
            ver = matcher.group(1);
            PHPSwitch.log.info("default PHP version " + ver + " detected");
        }
        return ver;
    }

    /**
     * Debug level logging
     *
     * @param msg
     */
    public static void debug(String msg) {
        log.log(Level.CONFIG, msg);
    }

    /**
     * Info level logging
     *
     * @param msg
     */
    public static void info(String msg) {
        log.log(Level.INFO, msg);
    }

    /**
     * Warning level logging
     *
     * @param msg
     */
    public static void warn(String msg) {
        log.log(Level.WARNING, msg);
    }

    /**
     * Error level logging
     *
     * @param msg
     */
    public static void error(String msg) {
        log.log(Level.SEVERE, msg);
    }

    /**
     * Error notification
     *
     * @param msg
     */
    public static void errorDialog(String msg) {
        int msgType = NotifyDescriptor.ERROR_MESSAGE;
        NotifyDescriptor d = new NotifyDescriptor.Message(msg, msgType);
        DialogDisplayer.getDefault().notify(d);
    }

}
