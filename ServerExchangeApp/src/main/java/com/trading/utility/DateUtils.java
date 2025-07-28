package com.trading.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    //private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    private static final TimeZone IST = TimeZone.getTimeZone("Asia/Kolkata");

    static {
        formatter.setTimeZone(IST);
    }

    public static List<DateRange> splitIntoYearlyRanges(Date fromDate, Date toDate) throws ParseException {

        // Current IST datetime
        Calendar currentCal = Calendar.getInstance(IST);
        Date currentDate = currentCal.getTime();

        // Adjust toDate if it is after current date in IST
        if (toDate.after(currentDate)) {
            int hour = currentCal.get(Calendar.HOUR_OF_DAY);
            int minute = currentCal.get(Calendar.MINUTE);

            boolean after330pm = (hour > 15) || (hour == 15 && minute >= 30);

            Calendar adjustedToCal = Calendar.getInstance(IST);
            if (after330pm) {
                adjustedToCal.setTime(currentDate);  // toDate = current date
            } else {
                // toDate = one day before current date
                adjustedToCal.setTime(currentDate);
                adjustedToCal.add(Calendar.DATE, -1);
                adjustedToCal.set(Calendar.HOUR_OF_DAY, 15); // 3 PM
                adjustedToCal.set(Calendar.MINUTE, 30);
                adjustedToCal.set(Calendar.SECOND, 0);
                adjustedToCal.set(Calendar.MILLISECOND, 0);
            }
            toDate = adjustedToCal.getTime();
        }

        List<DateRange> yearlyRanges = new ArrayList<>();

        // Start calendar with fromDate, set time to 09:00:00 IST
        Calendar startCal = Calendar.getInstance(IST);
        startCal.setTime(fromDate);
        startCal.set(Calendar.HOUR_OF_DAY, 9);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        // End calendar
        Calendar endCal = Calendar.getInstance(IST);
        endCal.setTime(toDate);

        // Loop to split by each year
        while (!startCal.after(endCal)) {
            // End of current year 31 Dec 23:59:59
            Calendar yearEndCal = (Calendar) startCal.clone();
            yearEndCal.set(Calendar.MONTH, Calendar.DECEMBER);
            yearEndCal.set(Calendar.DAY_OF_MONTH, 31);
            yearEndCal.set(Calendar.HOUR_OF_DAY, 23);
            yearEndCal.set(Calendar.MINUTE, 59);
            yearEndCal.set(Calendar.SECOND, 59);
            yearEndCal.set(Calendar.MILLISECOND, 999);

            Date rangeEnd;
            if (yearEndCal.getTime().before(endCal.getTime())) {
                rangeEnd = yearEndCal.getTime();
            } else {
                rangeEnd = endCal.getTime();
            }

            yearlyRanges.add(new DateRange(startCal.getTime(), rangeEnd));

            // Move startCal to next day 09:00:00 after rangeEnd
            startCal.setTime(rangeEnd);
            startCal.add(Calendar.DATE, 1);
            startCal.set(Calendar.HOUR_OF_DAY, 9);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
        }

        return yearlyRanges;
    }

    public static List<DateRange> splitIntoHalfYearlyRanges(Date fromDate, Date toDate) throws ParseException {

        List<DateRange> ranges = new ArrayList<>();

        // Current IST date
        Calendar currentCal = Calendar.getInstance(IST);
        Date currentDate = currentCal.getTime();

        // Adjust toDate if it's in the future
        if (toDate.after(currentDate)) {
            int hour = currentCal.get(Calendar.HOUR_OF_DAY);
            int minute = currentCal.get(Calendar.MINUTE);

            boolean after330pm = (hour > 15) || (hour == 15 && minute >= 30);

            Calendar adjustedToCal = Calendar.getInstance(IST);
            if (after330pm) {
                adjustedToCal.setTime(currentDate);  // today
            } else {
                adjustedToCal.setTime(currentDate);
                adjustedToCal.add(Calendar.DATE, -1);  // yesterday
                adjustedToCal.set(Calendar.HOUR_OF_DAY, 15);
                adjustedToCal.set(Calendar.MINUTE, 30);
                adjustedToCal.set(Calendar.SECOND, 0);
                adjustedToCal.set(Calendar.MILLISECOND, 0);
            }
            toDate = adjustedToCal.getTime();
        }

        // Initialize start calendar
        Calendar startCal = Calendar.getInstance(IST);
        startCal.setTime(fromDate);
        startCal.set(Calendar.HOUR_OF_DAY, 9);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        // Initialize end calendar
        Calendar endCal = Calendar.getInstance(IST);
        endCal.setTime(toDate);

        // Loop and split into 6-month intervals
        while (!startCal.after(endCal)) {
            Calendar rangeEndCal = (Calendar) startCal.clone();

            int currentMonth = startCal.get(Calendar.MONTH);

            if (currentMonth < Calendar.JULY) {
                // Jan–Jun → set to June 30
                rangeEndCal.set(Calendar.MONTH, Calendar.JUNE);
                rangeEndCal.set(Calendar.DAY_OF_MONTH, 30);
            } else {
                // Jul–Dec → set to Dec 31
                rangeEndCal.set(Calendar.MONTH, Calendar.DECEMBER);
                rangeEndCal.set(Calendar.DAY_OF_MONTH, 31);
            }

            rangeEndCal.set(Calendar.HOUR_OF_DAY, 23);
            rangeEndCal.set(Calendar.MINUTE, 59);
            rangeEndCal.set(Calendar.SECOND, 59);
            rangeEndCal.set(Calendar.MILLISECOND, 999);

            Date rangeEnd = rangeEndCal.getTime().before(endCal.getTime()) ? rangeEndCal.getTime() : endCal.getTime();

            ranges.add(new DateRange(startCal.getTime(), rangeEnd));

            // Move to start of next half-year
            startCal.setTime(rangeEnd);
            startCal.add(Calendar.DATE, 1); // next day
            startCal.set(Calendar.HOUR_OF_DAY, 9);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
        }

        return ranges;
    }


    public static class DateRange {
        private final Date from;
        private final Date to;

        public DateRange(Date from, Date to) {
            this.from = from;
            this.to = to;
        }

        public Date getFrom() {
            return from;
        }

        public Date getTo() {
            return to;
        }

        @Override
        public String toString() {
            return "DateRange{" +
                    "from=" + formatter.format(from) +
                    ", to=" + formatter.format(to) +
                    '}';
        }
    }


}

