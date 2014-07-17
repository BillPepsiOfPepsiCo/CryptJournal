package resources;

import java.io.InputStream;
import java.net.URL;

/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: resources
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 7/17/14 at 12:22 PM.
 */
public class Index {
	public static final URL darkThemeStylesheet = Index.class.getResource("css/DarkTheme.css"),
			rootTweaksStylesheet = Index.class.getResource("css/RootStyle.css");

	public static final InputStream easterEggSound = Index.class.getResourceAsStream("sound/smoke_weed_erryday.wav"),
			mPlusFont = Index.class.getResourceAsStream("font/mplus-1m-regular.ttf");

}
