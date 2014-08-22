package com.doktuhparadox.cryptjournal.util;

import com.doktuhparadox.easel.io.FileProprietor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created and written with IntelliJ IDEA CE 14.
 * Package: com.doktuhparadox.cryptjournal.util
 * Module of: CryptJournal
 * Author: Brennan Forrest (DoktuhParadox)
 * Date of creation: 8/10/14 at 4:12 PM.
 */
public final class Logger implements com.doktuhparadox.easel.utils.log.Logger {

	private static final Logger cryptJournalLogger = new Logger();
	private final File logFile = new File("CryptJournalLog.txt");

	private Logger() {
		if (logFile.delete()) System.out.println("\"Cleared\" log file");
	}

	@Override
	public void log(LogLevel logLevel, String s) {
		FileProprietor fileProprietor = new FileProprietor(logFile);
		String info = String.format("[CryptJournalLogger@%s] %s", logLevel.toString(), s);
		System.out.println(info);
		fileProprietor.append(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss ").format(new Date()).concat(info), true);
	}

	public static void logInfo(String s) {
		cryptJournalLogger.log(LogLevel.INFO, s);
	}

	public static void logFine(String s) {
		cryptJournalLogger.log(LogLevel.FINE, s);
	}

	public static void logFiner(String s) {
		cryptJournalLogger.log(LogLevel.FINER, s);
	}

	public static void logWarning(String s) {
		cryptJournalLogger.log(LogLevel.WARNING, s);
	}

	public static void logError(String s) {
		cryptJournalLogger.log(LogLevel.ERROR, s);
	}

	public static void logCritical(String s) {
		cryptJournalLogger.log(LogLevel.CRITICAL, s);
	}
}
