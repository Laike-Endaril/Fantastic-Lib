package com.fantasticsource.tools;

import java.time.Instant;

public class Timestamp
{
    protected final Instant instant;
    protected final int year, month, day, hour, minute, second, millisecond;
    protected final String yearString, monthString, dayString, hourString, minuteString, secondString, millisecondString;

    protected Timestamp(Instant instant)
    {
        this.instant = instant;

        String s = instant.toString();
        yearString = s.substring(0, 4);
        monthString = s.substring(5, 7);
        dayString = s.substring(8, 10);
        hourString = s.substring(11, 13);
        minuteString = s.substring(14, 16);
        secondString = s.substring(17, 19);
        millisecondString = s.substring(20, 23);

        year = Integer.parseInt(yearString);
        month = Integer.parseInt(monthString);
        day = Integer.parseInt(dayString);
        hour = Integer.parseInt(hourString);
        minute = Integer.parseInt(minuteString);
        second = Integer.parseInt(secondString);
        millisecond = Integer.parseInt(millisecondString);
    }

    public static Timestamp getInstance()
    {
        return getInstance(Instant.now());
    }

    public static Timestamp getInstance(Instant instant)
    {
        return instant == null ? null : new Timestamp(instant);
    }

    public Instant getInstant;

    public int getYear()
    {
        return year;
    }

    public int getMonth()
    {
        return month;
    }

    public int getDay()
    {
        return day;
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public int getSecond()
    {
        return second;
    }

    public int getMillisecond()
    {
        return millisecond;
    }

    public String getYearString()
    {
        return yearString;
    }

    public String getMonthString()
    {
        return monthString;
    }

    public String getDayString()
    {
        return dayString;
    }

    public String getHourString()
    {
        return hourString;
    }

    public String getMinuteString()
    {
        return minuteString;
    }

    public String getSecondString()
    {
        return secondString;
    }

    public String getMillisecondString()
    {
        return millisecondString;
    }

    @Override
    public String toString()
    {
        return toString(true, true, true);
    }

    public String toString(boolean date, boolean time, boolean ms)
    {
        return (date ? yearString + "-" + monthString + "-" + dayString : "") + (date && time ? " " : "") + (time ? hourString + ":" + minuteString + ":" + secondString + (ms ? "." + millisecondString : "") : "");
    }

    public int compareTo(Timestamp other)
    {
        return instant.compareTo(other.instant);
    }
}
