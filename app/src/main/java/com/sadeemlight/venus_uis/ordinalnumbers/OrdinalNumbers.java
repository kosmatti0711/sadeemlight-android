package com.sadeemlight.venus_uis.ordinalnumbers;

import java.util.Locale;

public class OrdinalNumbers {
	private static final String DEFAULT_INDICATOR = ".";

	private static OrdinalNumbers instance;
	private Locale locale;

	private OrdinalNumbers() {
		locale = Locale.getDefault();
	}

	public static OrdinalNumbers getInstance() {
		if (instance == null) {
			instance = new OrdinalNumbers();
		}

		return instance;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String format(int number) {
		return format(number, Gender.MALE);
	}

	public String format(int number, Gender gender) {
		return format(number, gender, locale);
	}

	public String format(int number, Gender gender, Locale locale) {
		String language = locale.getLanguage();
		if ("en".equals(language)) {
			return enOrdinalNumberFormatter(number);
		} else if ("es".equals(language)) {
			return esOrdinalNumberFormatter(number, gender);
		} else if ("fr".equals(language)) {
			return frOrdinalNumberFormatter(number, gender);
		} else if ("nl".equals(language)) {
			return nlOrdinalNumberFormatter(number);
		} else if ("it".equals(language)) {
			return itOrdinalNumberFormatter(number, gender);
		} else if ("pt".equals(language)) {
			return ptOrdinalNumberFormatter(number, gender);
		} else if ("ga".equals(language)) {
			return gaOrdinalNumberFormatter(number);
		} else if ("ja".equals(language)) {
			return jaOrdinalNumberFormatter(number);
		} else if ("zh".equals(language)) {
			return zhOrdinalNumberFormatter(number);
		} else if ("ca".equals(language)) {
			return caOrdinalNumberFormatter(number, gender);
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(number);
			stringBuilder.append(DEFAULT_INDICATOR);
			return stringBuilder.toString();
		}
	}

	private String enOrdinalNumberFormatter(int number) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);
		int hundredModule = number % 100;
		int decimalModule = number % 10;

		if (11 <= hundredModule && hundredModule <= 13) {
			stringBuilder.append("th");
		} else {
			switch (decimalModule) {
				case 1:
					stringBuilder.append("st");
					break;
				case 2:
					stringBuilder.append("nd");
					break;
				case 3:
					stringBuilder.append("rd");
					break;
				default:
					stringBuilder.append("th");
			}
		}

		return stringBuilder.toString();
	}

	private String esOrdinalNumberFormatter(int number, Gender gender) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);

		if (Gender.MALE == gender) {
			stringBuilder.append("\u00BA");
		} else if (Gender.FEMALE == gender) {
			stringBuilder.append("\u00AA");
		}

		return stringBuilder.toString();
	}

	private String frOrdinalNumberFormatter(int number, Gender gender) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);

		if (number == 1) {
			if (Gender.MALE == gender) {
				stringBuilder.append("er");
			} else if (Gender.FEMALE == gender) {
				stringBuilder.append("re");
			}
		} else {
			stringBuilder.append("e");
		}

		return stringBuilder.toString();
	}

	private String nlOrdinalNumberFormatter(int number) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);
		stringBuilder.append("e");

		return stringBuilder.toString();
	}

	private String itOrdinalNumberFormatter(int number, Gender gender) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);

		if (Gender.MALE == gender) {
			stringBuilder.append("\u00BA");
		} else if (Gender.FEMALE == gender) {
			stringBuilder.append("\u00AA");
		}

		return stringBuilder.toString();
	}

	private String ptOrdinalNumberFormatter(int number, Gender gender) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);

		if (Gender.MALE == gender) {
			stringBuilder.append("\u00BA");
		} else if (Gender.FEMALE == gender) {
			stringBuilder.append("\u00AA");
		}

		return stringBuilder.toString();
	}

	private String gaOrdinalNumberFormatter(int number) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);
		stringBuilder.append("\u00FA");

		return stringBuilder.toString();
	}

	private String jaOrdinalNumberFormatter(int number) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);
		stringBuilder.append("\u756A");

		return stringBuilder.toString();
	}

	private String zhOrdinalNumberFormatter(int number) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);
		stringBuilder.append("\u7B2C");

		return stringBuilder.toString();
	}

	private String caOrdinalNumberFormatter(int number, Gender gender) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(number);

		if (Gender.MALE == gender) {
			switch (number) {
				case 1:
					stringBuilder.append("r");
					break;
				case 2:
					stringBuilder.append("n");
					break;
				case 3:
					stringBuilder.append("r");
					break;
				case 4:
					stringBuilder.append("t");
					break;
				default:
					stringBuilder.append("è");
			}
		} else if (Gender.FEMALE == gender) {
			stringBuilder.append("a");
		}

		return stringBuilder.toString();
	}
}
