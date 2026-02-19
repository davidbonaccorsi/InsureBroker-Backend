package com.insurebroker.util;

import java.time.LocalDate;

public class CNPValidator {
    private static final int[] CONTROL_KEY = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};

    public static boolean isValid(String cnp) {
        if (cnp == null || cnp.length() != 13) {
            return false;
        }

        if (!cnp.matches("\\d{13}")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnp.charAt(i)) * CONTROL_KEY[i];
        }

        int controlDigit = sum % 11;
        if (controlDigit == 10) {
            controlDigit = 1;
        }

        return controlDigit == Character.getNumericValue(cnp.charAt(12));
    }

    public static LocalDate extractDateOfBirth(String cnp) {
        int sexDigit = Character.getNumericValue(cnp.charAt(0));
        int year = Integer.parseInt(cnp.substring(1, 3));
        int month = Integer.parseInt(cnp.substring(3, 5));
        int day = Integer.parseInt(cnp.substring(5, 7));

        int century;
        switch (sexDigit) {
            case 1: case 2: century = 1900; break;
            case 3: case 4: century = 1800; break;
            case 5: case 6: century = 2000; break;
            default: century = 1900;
        }

        return LocalDate.of(century + year, month, day);
    }
}