package com.doktuhparadox.cryptjournal.util;

import com.doktuhparadox.easel.platform.PlatformDifferentiator;

import com.apple.eawt.Application;

/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: com.doktuhparadox.cryptjournal.core
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 7/16/14 at 6:55 PM.
 */
public class MethodProxy {
	public static void setDockBadge(String badge) {
		if (PlatformDifferentiator.isMacOSX()) Application.getApplication().setDockIconBadge(badge);
	}
}
