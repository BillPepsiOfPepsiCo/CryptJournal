CryptJournal
============

A journaling app with personality (and encryption!).

Features
============

Comes complete with:

**1) Journaling capabilities!**

Watch in awe as you type the content of your entry on your keyboard and it shows up on the screen.
You will have the envy of your friends when they see how HTML text formatting is also available (but HTMLEditors are a tad
buggy and that's really not in my capabilities to fix).

**2) Encryption!**

So, what if a snoopy douchewagon tries to open your journal entries and view them in plaintext? You're one step ahead because CryptJournal
takes care of that for you! When you save your entries, CryptJournal automagically encrypts the text using the AES encryption algorithm
so it looks as if you wrote it while having a seizure drunk on your keyboard!

Each time an entry is saved, you are prompted for a password, so each time you save your entry it can be re-encrypted using a different password.

Not to worry, you can still view your dirty little secrets if you remember the password you used! But, if you cannot remember your password, you cannot recover the lost data.

**3) Configuration!**

A new innovation in the software engineering community: options! You can currently configure:

A) The encryption algorithm you wish to use (currently only AES and Blowfish; DES and others to come soon). You do not have to remember
which you used when opening entries because the encryption algorithm used is written to the entry's metadata file (located in Journals/.metadata/).
However, if you switch the encryption algorithm and re-save an entry, the algorithm in its metadata file will not be updated, so be sure you have the
algorithm you wish to use with that particular entry selected when you create it, because there is no way to change it.

B) The date and time display formats (and 12/24 hour time). 
This is a small convenience feature for those in the glorious U.S.A. and for those in commieland and Australia (m8). 
Though the U.S. is best, the default date format is day/month/year for ease on my behalf.

C) The interval at which CryptJournal autosaves your entries. Currently, this feature is not in the program due to an issue with closing the application that I'm having trouble resolving,
so this option has no effect. Autosaving does occur when the program is terminated while an entry is currently open in the editor; it writes it to the journal entry file with the default password
"================" (which doesn't matter to you because it skips password entry if it is the default).

D) Toggle password caching (defaults "off"). This option is currently only for show, but password caching will be implemented in the future (the new
journal entry metadata system will be the basis for that).

E) A dark theme! This seems to be a popular *theme* with applications lately so I thought I would include one.
Currently, there is no way to fully style an HTMLEditor, but the rest of the app is fully skinned. 

**4) Easter eggs!**

I'm not going to go too into detail with this. They're not too complex, and if you dive around the source you'll find them. (Hint: they're in
the Controller class).

Screenshots
============

![Startup](/screenshots/1-Startup.png?raw=true "First screen")

![Creating](/screenshots/2-Creating.png?raw=true "Creating an entry")

![Writing](/screenshots/3-Writing.png?raw=true "Writing an entry")

![Saving](/screenshots/4-Saving.png?raw=true "Saving an entry")

![Plaintext](/screenshots/5-Plaintext.png?raw=true "What that looks like when encrypted (with password U.S.A.)")

![Configuration](/screenshots/6-Configuration.png?raw=true "The options window")

![Dark theme 1](/screenshots/7-Dark_theme.png?raw=true "The dark theme on the first screen")

![Dark theme 2](/screenshots/8-Dark_theme2.png?raw=true "The dark theme on the options window")

Downloads
============

Download the current stable build [here](http://google.com/). (not yet!)

Dependencies
============

This app depends on [ControlsFX](http://fxexperience.com/controlsfx/) and my own commons library, [Easel](https://github.com/DoktuhParadox/Easel). Neither of these have to be downloaded for the binary to work.
