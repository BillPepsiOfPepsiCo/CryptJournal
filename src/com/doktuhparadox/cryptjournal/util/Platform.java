package com.doktuhparadox.cryptjournal.util;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/28/14, at 10:55 AM.
 */
class Platform {
    private static String getPlatform() {
        return System.getProperty("os.name");
    }

    public static boolean isMacOSX() {
        return getPlatform().contains("osx");
    }

    public static boolean isWindows() {
        return getPlatform().contains("win");
    }

    public static boolean isUnixOrDerivative() {
        return getPlatform().contains("nix") || getPlatform().contains("nux") || getPlatform().contains("aix");
    }

    public static boolean isSolaris() {
        return getPlatform().contains("sunos");
    }
}
