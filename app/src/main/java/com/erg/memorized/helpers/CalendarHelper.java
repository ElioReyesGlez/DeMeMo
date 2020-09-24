package com.erg.memorized.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.erg.memorized.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class CalendarHelper {
    private static final String TAG = "CalendarHelper";
    public static final int CALENDAR_HELPER_PERMISSION_REQUEST_CODE = 93;

    public static void makeNewCalendarEntry(Activity context, String title, String description,
                                            long startTime, String duration,
                                            boolean allDay, boolean hasAlarm, boolean daily,
                                            boolean weekly, boolean monthly, int calendarId,
                                            String until, int selectedReminderValue) {

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DURATION, duration);
        values.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);


        if (allDay) {
            values.put(CalendarContract.Events.ALL_DAY, true);
        }

        if (hasAlarm) {
            values.put(CalendarContract.Events.HAS_ALARM, true);
        }

        if (daily) {
            values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=" + until);
        }
        if (weekly) {
            values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=" + until);
        }
        if (monthly) {
            values.put(CalendarContract.Events.RRULE, "FREQ=MONTHLY;UNTIL=" + until);
        }

        //Get current timezone
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Log.i(TAG, "Timezone retrieved => " + TimeZone.getDefault().getID());

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Log.i(TAG, "Uri returned => " + uri.toString());
        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        if (hasAlarm) {
            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            reminders.put(CalendarContract.Reminders.MINUTES, selectedReminderValue);

            Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

            Log.i(TAG, "Uri returned => " + uri2.toString());
        }
    }

    public static void requestCalendarReadWritePermission(Activity context) {
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_CALENDAR);

        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CALENDAR);

        }

        if (permissionList.size() > 0) {
            String[] permissionArray = new String[permissionList.size()];

            for (int i = 0; i < permissionList.size(); i++) {
                permissionArray[i] = permissionList.get(i);
            }

            ActivityCompat.requestPermissions(context,
                    permissionArray,
                    CALENDAR_HELPER_PERMISSION_REQUEST_CODE);
        }

    }

    public static HashMap<String, String> getUserCalendars(Activity context) {

        if (haveCalendarReadWritePermissions(context)) {

            String[] projection = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse(Constants.URI_CALENDARS);

            ContentResolver contentResolver = context.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection,
                    null, null, null);

            if (managedCursor.moveToFirst()) {
                String calName;
                String calID;
                int cont = 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                HashMap<String, String> calendarIdTable = new HashMap<>();
                do {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
                    Log.v(TAG, "CalendarName:" + calName + " ,id:" + calID);
                    calendarIdTable.put(calName, calID);
                    cont++;
                } while (managedCursor.moveToNext());
                managedCursor.close();
                return calendarIdTable;
            }
        }
        return null;
    }

    public static boolean haveCalendarReadWritePermissions(Activity context) {
        int permissionCheckRead = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR);

        return permissionCheckRead == PackageManager.PERMISSION_GRANTED
                && permissionCheckWrite == PackageManager.PERMISSION_GRANTED;
    }
}
