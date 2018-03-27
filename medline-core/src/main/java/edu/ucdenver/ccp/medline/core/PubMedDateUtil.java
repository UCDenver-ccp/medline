package edu.ucdenver.ccp.medline.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PubMedDateUtil {

	private static final Logger logger = Logger.getLogger(PubMedDateUtil.class.getName());

	public static int getMonth(String month) {
		// remove any leading zeros
		while (month.startsWith("0")) {
			month = month.substring(1);
		}
		if (month.matches("\\d+")) {
			return Integer.parseInt(month);
		}
		switch (month) {
		case "Jan":
			return 1;
		case "Win":
			return 1;
		case "Feb":
			return 2;
		case "Mar":
			return 3;
		case "Spr":
			return 4;
		case "Apr":
			return 4;
		case "May":
			return 5;
		case "Jun":
			return 6;
		case "Sum":
			return 7;
		case "Jul":
			return 7;
		case "Aug":
			return 8;
		case "Sep":
			return 9;
		case "Aut":
			return 10;
		case "Fal":
			return 10;
		case "Oct":
			return 10;
		case "Nov":
			return 11;
		case "Dec":
			return 12;
		default:
			logger.log(Level.WARNING, "Unhandled month string: " + month);
			return 1;
		}
	}

	public static int getMonthForSeason(String season) {
		switch (season.toLowerCase()) {
		case "winter":
			return 1;
		case "spring":
			return 4;
		case "summer":
			return 7;
		case "autumn":
			return 10;
		case "fall":
			return 10;
		default:
			logger.log(Level.WARNING, "Unhandled season string: " + season);
			return 1;
		}
	}

	public static int[] parseMedlineDate(String medlineDate) {
		Pattern p = Pattern.compile("^(\\d{4}) ([A-Z][a-z][a-z])$");
		Matcher m = p.matcher(medlineDate);
		if (m.find()) {
			int year = Integer.parseInt(m.group(1));
			int month = PubMedDateUtil.getMonth(m.group(2));
			return new int[] { month, year };
		}
		p = Pattern.compile("^(\\d{4}) ([A-Z][a-z][a-z]) \\d+");
		m = p.matcher(medlineDate);
		if (m.find()) {
			int year = Integer.parseInt(m.group(1));
			int month = PubMedDateUtil.getMonth(m.group(2));
			return new int[] { month, year };
		}
		p = Pattern.compile("(\\d{4}) ([A-Z][a-z][a-z])-");
		m = p.matcher(medlineDate);
		if (m.find()) {
			int year = Integer.parseInt(m.group(1));
			int month = PubMedDateUtil.getMonth(m.group(2));
			return new int[] { month, year };
		}
		p = Pattern.compile("(?i)(\\d{4}) (Spring|Summer|Fall|Autumn|Winter)");
		m = p.matcher(medlineDate);
		if (m.find()) {
			int year = Integer.parseInt(m.group(1));
			int month = PubMedDateUtil.getMonthForSeason(m.group(2));
			return new int[] { month, year };
		}
		p = Pattern.compile("(\\d{4})-\\d{4}");
		m = p.matcher(medlineDate);
		if (m.find()) {
			int year = Integer.parseInt(m.group(1));
			int month = 1;
			return new int[] { month, year };
		}

		logger.log(Level.WARNING, "Unhandled medline date format: " + medlineDate);
		return new int[] { 0, 0 };
	}

}
