/**
 * PHPSwitch module class
 *
 * @author Vítězslav Dvořák <info@vitexsoftware.cz>
 * @copyright 2021 Vitex@hippy.cz (G)
 */
package com.vitexsoftware.phpswitch;

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
 *
 * @author vitex
 */
@OnShowing
public class PHPSwitch extends ModuleInstall implements Runnable {

    public static final String CODENAME = "com.vitexsoftware.phpswitch";

    public static final Logger log = Logger.getLogger("PHPSwitch");

    public static final String phpver = PHPSwitch.currentPhpVersion();

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

    public static void info(String msg) {
        log.log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        log.log(Level.WARNING, msg);
    }

    public static void error(String msg) {
        log.log(Level.SEVERE, msg);
    }

    public static void errorDialog(String msg) {
        int msgType = NotifyDescriptor.ERROR_MESSAGE;
        NotifyDescriptor d = new NotifyDescriptor.Message(msg, msgType);
        DialogDisplayer.getDefault().notify(d);
    }

    public static void debug(String msg) {
        log.log(Level.CONFIG, msg);
    }

}
