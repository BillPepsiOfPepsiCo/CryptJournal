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

All of your text is safe. Every last character you enter is thoroughly encrypted with Jasypt!

This program uses [Jasypt](http://http://www.jasypt.org/)'s StrongTextEncryptor for its backend. This means the algorithm is always"PBEWithMD5AndTripleDES." This is super duper secure.
***Super duper*** secure. This features requires the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files.

Each time an entry is saved, you are prompted for a password, so each time you save your entry it can be re-encrypted using a different password.

Not to worry, you can still view your dirty little secrets if you remember the password you used! But, if you cannot remember your password, ***you cannot recover the lost data***.

**3) Configuration!**

A new innovation in the software engineering community: options! You can currently configure:

1) The date and time display formats (and 12/24 hour time). 
This is a small convenience feature for those in the glorious U.S.A. and for those in commieland and Australia (m8). 
Though the U.S. is best, the default date format is day/month/year for ease on my behalf.

2) The interval at which CryptJournal autosaves your entries. The autosave service is started when you open an entry and canceled when you save it. If you change your autosave interval without restarting the program,
your changes will take effect the next time the autosave service is started.

3) Toggle password caching (defaults "off"). This option is currently only for show, but password caching will be implemented in the future (the new
journal entry metadata system will be the basis for that).

4) A dark theme! This seems to be a popular *theme* with applications lately so I thought I would include one.
Currently, there is no way to fully style an HTMLEditor, but the rest of the app is fully skinned. 

**4) Easter eggs!**

I'm not going to go too into detail with this. They're not too complex, and if you dive around the source you'll find them. (Hint: they're in
the Controller class).

Downloads
============

Download the current stable build [here](https://github.com/DoktuhParadox/CryptJournal/releases/). 

Dependencies
============

This app depends on [ControlsFX](http://fxexperience.com/controlsfx/), [Jasypt](http://http://www.jasypt.org/),
and my own commons library, [Easel](https://github.com/DoktuhParadox/Easel). None of these have to be downloaded for the binary to work (duh). 
