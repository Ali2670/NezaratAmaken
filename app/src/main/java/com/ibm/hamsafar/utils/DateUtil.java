package com.ibm.hamsafar.utils;


import com.ibm.hamsafar.fdate.FDate;
import com.ibm.hamsafar.fdate.ShamsiCalendar;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


public class DateUtil {

    public static String getCurrentDate() {
        FDate curDate = new FDate(System.currentTimeMillis());
        return curDate.toString();
    }

    public static String getCurrentTime() {
        FDate curDate = new FDate(System.currentTimeMillis());
        return getCompleteTimeString(curDate);
    }

    public static String getCurrentTimeString() {
        return getCompleteTimeString(new FDate(System.currentTimeMillis()));
    }

    public static String getCompleteTimeString(FDate fdate) {
        StringBuffer b = new StringBuffer();
        b.append(fdate.getHour() < 10 ? "0" + fdate.getHour() : String.valueOf(fdate.getHour()));
        b.append(":");
        b.append(fdate.getMinute() < 10 ? "0" + fdate.getMinute() : String.valueOf(fdate.getMinute()));
        b.append(":");
        b.append(fdate.getSecond() < 10 ? "0" + fdate.getSecond() : String.valueOf(fdate.getSecond()));
        return b.toString();
    }

    public static int getCurrentYear() {
        FDate curDate = new FDate(System.currentTimeMillis());
        return curDate.getYear();
    }

    public static int getCurrentMonth() {
        FDate curDate = new FDate(System.currentTimeMillis());
        return curDate.getMonth();
    }

    public static int getCurrentDay() {
        FDate curDate = new FDate(System.currentTimeMillis());
        return curDate.getDate();
    }

    public static String gatMiladiDate(int year, int month, int day) {
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(gc.getTime());
    }

    public static Date toDate(String formattedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        ParsePosition pos = new ParsePosition(0);
        Date d = dateFormat.parse(formattedDate, pos);
        return d;
    }

    public static String invertDate(String fdate) {
        String yyyy = null;
        String mm = null;
        String dd = null;
        if (fdate != null && fdate.length() != 0) {
            StringTokenizer strTokenizer = new StringTokenizer(fdate, "/");
            if (strTokenizer.hasMoreTokens()) {
                yyyy = strTokenizer.nextToken();
                if (strTokenizer.hasMoreTokens()) {
                    mm = strTokenizer.nextToken();
                    if (strTokenizer.hasMoreTokens()) {
                        dd = strTokenizer.nextToken();
                        return dd + "/" + mm + "/" + yyyy;
                    }
                }
            }

            return fdate;
        } else {
            return "";
        }
    }

    public static String changeFarsiToMiladi(String farsiDate) {
        Date miladiDate = ShamsiCalendar.shamsiToMiladi(farsiDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(miladiDate);
    }

    public static String changeMiladiToFarsi(String miladiDate) {
        return ShamsiCalendar.miladiToShamsi(toDate(miladiDate));
    }

    public static int getTotalDays(FDate startDate, int durationMonth, int durationDay) {
        FDate endDate = new FDate(startDate.toMiladi());

        int i;
        for (i = 0; i < durationMonth; ++i) {
            endDate.nextMonth();
        }

        for (i = 0; i < durationDay; ++i) {
            endDate.nextDay();
        }

        int totalDays = endDate.minusDate(startDate.toString());
        return totalDays;
    }

    public static int getDurationMonth(FDate startDate, int totalDays) {
        FDate endDate = new FDate(startDate.toString());
        FDate curDate = new FDate(startDate.toString());

        for (int i = 0; i < totalDays; ++i) {
            endDate.nextDay();
        }

        System.out.println("END DATE IS " + endDate.toString());
        int durationMonthVal = 0;

        while (curDate.getYear() < endDate.getYear() || curDate.getYear() == endDate.getYear() && curDate.getMonth() < endDate.getMonth()) {
            ++durationMonthVal;
            curDate.nextMonth();
        }

        if (curDate.after((FDate) endDate)) {
            --durationMonthVal;
            curDate.prevMonth();
        }

        return durationMonthVal;
    }

    public static int getDurationMonth(FDate startDate, FDate endDate) {
        int durationMonth;
        for (durationMonth = 0; startDate.compareTo((FDate) endDate) < 0; ++durationMonth) {
            startDate.nextMonth();
        }

        return durationMonth;
    }

    public static int getDurationMonth(String startDate, String endDate) {
        return getDurationMonth((FDate) (new FDate(startDate)), (FDate) (new FDate(endDate)));
    }

    public static int getDurationDay(FDate startDate, int totalDays) {
        FDate endDate = new FDate(startDate.toString());
        FDate curDate = new FDate(startDate.toString());
        int durationMonthVal = 0;

        for (int i = 0; i < totalDays; ++i) {
            endDate.nextDay();
        }

        while (curDate.getYear() < endDate.getYear() || curDate.getYear() == endDate.getYear() && curDate.getMonth() < endDate.getMonth()) {
            ++durationMonthVal;
            curDate.nextMonth();
        }

        if (curDate.after((FDate) endDate)) {
            --durationMonthVal;
            curDate.prevMonth();
        }

        int durationDayVal = 0;

        while (curDate.before((FDate) endDate)) {
            ++durationDayVal;
            curDate.nextDay();
        }

        return durationDayVal;
    }

    public static int getDurationMonth(String startDate, int totalDays) {
        FDate fDate = new FDate(startDate);
        return getDurationMonth((FDate) fDate, totalDays);
    }

    public static int getDurationDay(String startDate, int totalDays) {
        FDate fDate = new FDate(startDate);
        return getDurationDay((FDate) fDate, totalDays);
    }

    public static String stringDayMountYear() {
        return ShamsiCalendar.weekDayName(ShamsiCalendar.dayOfWeek((String) getCurrentDate())) + " " + ShamsiCalendar.monthDayName(getCurrentDay()) + ShamsiCalendar.monthName(getCurrentMonth()) + " سال " + getCurrentYear();
    }

    public static String stringDayMountYear(String date) {
        FDate fDate = new FDate(date);
        return Tools.ChangeNumbers(/*ShamsiCalendar.weekDayName(ShamsiCalendar.dayOfWeek(date))
                + " " + */ShamsiCalendar.monthDayName(fDate.getDate()) + " " +
                ShamsiCalendar.monthName(fDate.getMonth()) + " " + fDate.getYear());
    }


    public static String decreaseYear(String tavalodDate, int cnt) {
        String year = tavalodDate.substring(0, 4);
        int ny = Integer.decode(year).intValue() - cnt;
        return ny + tavalodDate.substring(4);
    }

    public static String decreaseCurrentYear(int cnt) {
        String cur = getCurrentDate();
        String year = cur.substring(0, 4);
        int ny = Integer.decode(year).intValue() - cnt;
        return ny + cur.substring(4);
    }

    public static String increaseYear(String tavalodDate, int cnt) {
        String year = tavalodDate.substring(0, 4);
        int ny = Integer.decode(year).intValue() + cnt;
        return ny + tavalodDate.substring(4);
    }

    public static String increaseCurrentYear(int cnt) {
        String cur = getCurrentDate();
        String year = cur.substring(0, 4);
        int ny = Integer.decode(year).intValue() + cnt;
        return ny + cur.substring(4);
    }
}
