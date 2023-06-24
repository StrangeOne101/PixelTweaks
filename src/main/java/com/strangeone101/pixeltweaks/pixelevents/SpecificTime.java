package com.strangeone101.pixeltweaks.pixelevents;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.strangeone101.pixeltweaks.PixelTweaks;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpecificTime {

    public int year = -1;
    public int month = -1;
    public int day_of_week = -1;
    public int day = -1;
    public int hour = -1;
    public int minute = -1;

    public Calendar getTime() {
        Calendar calendar = Calendar.getInstance();
        if (year != -1) calendar.set(Calendar.YEAR, year);
        if (month != -1) calendar.set(Calendar.MONTH, month);
        if (day_of_week != -1) calendar.set(Calendar.DAY_OF_WEEK, day_of_week);
        if (day != -1) calendar.set(Calendar.DAY_OF_MONTH, day);
        if (hour != -1) calendar.set(Calendar.HOUR_OF_DAY, hour);
        if (minute != -1) calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    public boolean validate() {
        try {
            Calendar instance = Calendar.getInstance();
            if (year != -1) instance.set(Calendar.YEAR, year);
            if (month != -1) instance.set(Calendar.MONTH, month);
            if (day_of_week != -1) instance.set(Calendar.DAY_OF_WEEK, day_of_week);
            if (day != -1) instance.set(Calendar.DAY_OF_MONTH, day);
            if (hour != -1) instance.set(Calendar.HOUR_OF_DAY, hour);
            if (minute != -1) instance.set(Calendar.MINUTE, minute);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isBetween(SpecificTime start, SpecificTime end) {

        Calendar startCalendar = start.getTime();
        Calendar endCalendar = end.getTime();
        Calendar now = Calendar.getInstance();

        endCalendar.add(Calendar.SECOND, 1);

        return now.after(startCalendar) && now.before(endCalendar);
    }

    public static class Deserializer implements JsonDeserializer<SpecificTime> {

        @Override
        public SpecificTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SpecificTime time = new SpecificTime();
            if (!json.isJsonObject()) return null;
            if (json.getAsJsonObject().has("year")) time.year = json.getAsJsonObject().get("year").getAsInt();
            if (json.getAsJsonObject().has("month")) {
                JsonElement month = json.getAsJsonObject().get("month");
                if (month.isJsonPrimitive() && month.getAsJsonPrimitive().isNumber()) {
                    time.month = month.getAsInt() - 1;
                } else { //Parse the month from string
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(month.getAsString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    time.month = cal.get(Calendar.MONTH);
                }
            }
            if (json.getAsJsonObject().has("day")) time.day = json.getAsJsonObject().get("day").getAsInt() - 1;
            if (json.getAsJsonObject().has("day_of_week")) {
                JsonElement day = json.getAsJsonObject().get("day_of_week");
                if (day.isJsonPrimitive() && day.getAsJsonPrimitive().isNumber()) {
                    time.day_of_week = day.getAsInt() - 1;
                } else { //Parse the day from string
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("EEE", Locale.ENGLISH).parse(day.getAsString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    time.day_of_week = cal.get(Calendar.DAY_OF_WEEK);
                }
            }
            if (json.getAsJsonObject().has("hour")) time.hour = json.getAsJsonObject().get("hour").getAsInt();
            if (json.getAsJsonObject().has("minute")) time.minute = json.getAsJsonObject().get("minute").getAsInt();
            if (time.validate()) return time;
            PixelTweaks.LOGGER.error("Invalid time: " + json.toString());

            return time;
        }
    }

}
