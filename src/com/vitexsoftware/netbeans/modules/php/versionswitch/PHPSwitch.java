/**
 * PHPSwitch module class
 *
 * @author Vítězslav Dvořák <info@vitexsoftware.cz>
 * @copyright 2021 Vitex@hippy.cz (G)
 */
package com.vitexsoftware.netbeans.modules.php.versionswitch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
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
 *
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
     * System recognization
     */
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    
    /**
     * Introduce plugin into live
     */
    @Override
    public void run() {
        System.out.println("PHPSwitch module loaded with PHP " + PHPSwitch.phpver + " detected");

        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
            }
        });

    }

    /**
     * Obtain current PHP version
     *
     * @return from "5.6" to "8.1"
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
     * Search for PHP versions in system
     *
     * @return php versions found
     */
    public static List<String> phpVersionsAvailble() {
        List<String> candidates = Arrays.asList("5.6", "7.0", "7.1", "7.2", "7.3", "7.4", "8.0", "8.1");
        List<String> versionsFound = new ArrayList<String>();

        ListIterator<String> candidatesIterator = candidates.listIterator();

        while (candidatesIterator.hasNext()) {
            String version = candidatesIterator.next();
            File f = new File("/usr/bin/php" + version);
            if (f.exists() && !f.isDirectory() && f.isFile()) {
                versionsFound.add(version);
                PHPSwitch.log.log(Level.INFO, "PHP version {0} found", version);
            }

        }

        if(versionsFound.isEmpty()){
            PHPSwitch.errorDialog("No /usr/bin/phpX found");
        }
        
        return  versionsFound;
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
