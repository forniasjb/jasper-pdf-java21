package com.sandbox.jasperpdfjava21.utils;

import java.math.BigDecimal;

public class NumberToWordsConverter {

    private static final String[] units = {
            "", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN",
            "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"
    };

    private static final String[] tens = {
            "", "", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"
    };

    public static String convertAmountToWords(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "ZERO PESOS ONLY";
        }

        long longPart = amount.longValue();
        int cents = amount.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        String words = convert(longPart) + " PESOS";

        if (cents > 0) {
            words += " AND " + convert(cents) + " CENTAVOS";
        } else {
            words += " ONLY";
        }

        return words.trim();
    }

    private static String convert(long n) {
        if (n < 20) {
            return units[(int) n];
        }
        if (n < 100) {
            return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " + units[(int) (n % 10)] : "");
        }
        if (n < 1000) {
            return units[(int) (n / 100)] + " HUNDRED" + ((n % 100 != 0) ? " " + convert(n % 100) : "");
        }
        if (n < 1000000) {
            return convert(n / 1000) + " THOUSAND" + ((n % 1000 != 0) ? " " + convert(n % 1000) : "");
        }
        if (n < 1000000000L) {
            return convert(n / 1000000) + " MILLION" + ((n % 1000000 != 0) ? " " + convert(n % 1000000) : "");
        }
        if (n < 1000000000000L) {
            return convert(n / 1000000000L) + " BILLION"
                    + ((n % 1000000000L != 0) ? " " + convert(n % 1000000000L) : "");
        }
        if (n < 1000000000000000L) {
            return convert(n / 1000000000000L) + " TRILLION"
                    + ((n % 1000000000000L != 0) ? " " + convert(n % 1000000000000L) : "");
        }
        return "";
    }
}
