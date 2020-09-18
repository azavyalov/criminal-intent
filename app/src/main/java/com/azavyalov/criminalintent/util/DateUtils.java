package com.azavyalov.criminalintent.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String parseDate(Date date) {
        DateFormat df = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
        return df.format(date);
    }
}
