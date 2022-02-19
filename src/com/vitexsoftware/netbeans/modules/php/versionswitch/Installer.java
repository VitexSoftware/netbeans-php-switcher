/**
 * PHPSwitch module Installer
 *
 * @author Vítězslav Dvořák <info@vitexsoftware.cz>
 * @copyright 2021-2022 Vitex@hippy.cz (G)
 */
package com.vitexsoftware.netbeans.modules.php.versionswitch;

import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 *
 * @author vitex
 */
public class Installer extends ModuleInstall {

    private static final long serialVersionUID = 1L;

    @Override
    public void restored() {
    }

    @Override
    public void close() {
        try {
            // if other exceptions are thrown when IDE is closed, the options may not be saved
            // so sleep a bit
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
