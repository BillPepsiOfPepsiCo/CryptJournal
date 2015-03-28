CryptJournal
============

Kind of self-explanitory, huh?

Features
============

Comes complete with:

**1) Journaling capabilities!**

Innovation in the field is something I'm quite nutorious for. In this case, I've innovated in such a way that allows you to type something and then *have it show up on the screen*. 
Also, it uses an HTMLEditor so you can format the text that way, too, but the Java version the binary uses it a little outdated and HTMLEditors are somewhat buggy.

**2) Encryption!**

All of your text is safe (except from keyloggers and people standing behind you). Every last character you enter is encrypted when you save!

This program uses [Jasypt](http://http://www.jasypt.org/)'s StrongTextEncryptor for its encryption because if someone actually decides to use this, I'm not going to try and implement crypto myself, lol. However, the use of this requires the [Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html). Without these files, jasypt falls back on its regular strength text encryptor. Think of it like this: you have a migraine and wish to cure it, but all you have is Tylenol and you really need Excedrin Extra Strength for this motherfucker, so you need to drive to Walgreens and buy some. But that requires more effort on your part so it's really up to your discretion.

*This README.md brought to you by Tylenol, Excedrin and Walgreens.*

Each time an entry is saved, you are prompted for a password, so each time you save your entry it can be re-encrypted using a different password. Just remember it because unless you have the time and money to build a cluster to crack it, that text is gone, son.

**3) Configuration!**

A new innovation in the software engineering community: options! You can currently configure:

1) The date and time display formats (and 12/24 hour time). 
This is a small convenience feature for those in the glorious U.S.A. and for those in commieland and Australia (m8). 
Though the U.S. is best, the default date format is day/month/year for ease on my behalf.

2) The interval at which CryptJournal autosaves your entries. The autosave service is started when you open an entry and canceled when you save it. If you change your autosave interval without restarting the program,
your changes will take effect the next time the autosave service is started (save + open).

3) Toggle password caching (defaults "off"). This option is currently only for show, but password caching will be implemented in the future (the
journal entry metadata system will be the basis for that).

4) A dark theme! This seems to be a popular *theme* with applications lately so I thought I would include one.
Currently, there is no way to fully style an HTMLEditor, but the rest of the app is fully skinned. 

Downloads
============

Download the current stable build [here](https://github.com/DoktuhParadox/CryptJournal/releases/). 

Dependencies
============

This app uses these libraries: [ControlsFX](http://fxexperience.com/controlsfx/), [Jasypt](http://http://www.jasypt.org/),
and my own commons library, [Easel](https://github.com/DoktuhParadox/Easel). None of these have to be downloaded for the binary to work (duh). 
