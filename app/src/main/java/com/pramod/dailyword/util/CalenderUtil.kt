package com.pramod.dailyword.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class CalenderUtil {
    companion object {
        const val DATE_FORMAT_DISPLAY = "dd MMM"
        const val DATE_WITH_YEAR_FORMAT_DISPLAY = "dd MMM yy"
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss a"

        @JvmStatic
        fun convertCalenderToString(calender: Calendar, dateFormat: String = DATE_FORMAT): String {
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
            return simpleDateFormat.format(calender.time)
        }

        @JvmStatic
        fun convertStringToCalender(dateString: String?, dateFormat: String): Calendar? {
            if (dateString == null) {
                return null;
            }
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
            val date = simpleDateFormat.parse(dateString)
            if (date != null) {
                val calender = Calendar.getInstance(Locale.US)
                calender.time = date
                return calender
            }
            return null
        }

        @JvmStatic
        fun convertDateStringToSpecifiedDateString(
            dateString: String?,
            dateFormat: String = DATE_FORMAT,
            requiredDateFormat: String = DATE_FORMAT_DISPLAY
        ): String? {
            if (dateString == null) {
                return null;
            }
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
            val date = simpleDateFormat.parse(dateString)
            if (date != null) {
                val reqSimpleDateFormat = SimpleDateFormat(requiredDateFormat, Locale.US)
                return reqSimpleDateFormat.format(date)
            }
            return null
        }

        @JvmStatic
        fun isYesterday(dateString: String?, dateFormat: String = DATE_FORMAT): Boolean {
            if (dateString == null) {
                return false;
            }
            val cal: Calendar = Calendar.getInstance(Locale.US)
            cal.roll(Calendar.DATE, false)
            return convertCalenderToString(cal, dateFormat) == dateString
        }

        @JvmStatic
        fun isToday(dateString: String?, dateFormat: String = DATE_FORMAT): Boolean {
            if (dateString == null) {
                return false;
            }
            val cal: Calendar = Calendar.getInstance(Locale.US)
            return convertCalenderToString(cal, dateFormat) == dateString
        }


        @JvmStatic
        fun createCalendarForHourOfDayAndMin(hourOfDay: Int, minute: Int = 0): Calendar {
            val calender = Calendar.getInstance(Locale.US)
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            calender.set(Calendar.SECOND, 0)
            return calender
        }

        @JvmStatic
        fun getDayFromDateString(date: String?, format: String): String? {
            if (date == null) {
                return null;
            }
            val calendar = convertStringToCalender(date, format)
            if (calendar != null) {
                return SimpleDateFormat("dd", Locale.US).format(calendar.time)
            }
            return date.substring(0, 2)
        }


        @JvmStatic
        fun getMonthFromDateString(date: String, format: String): String {
            val calendar = convertStringToCalender(date, format)
            if (calendar != null) {
                return SimpleDateFormat("MMM", Locale.US).format(calendar.time)
            }
            return date.substring(2, 5)
        }


        fun subtractDaysFromCalendar(
            dateString: String?,
            howManyDays: Int,
            dateFormat: String = DATE_FORMAT
        ): String {
            val calendar: Calendar = if (dateString != null) {
                convertStringToCalender(
                    dateString,
                    DATE_FORMAT
                )!!
            } else {
                Calendar.getInstance()
            }
            calendar.add(Calendar.DATE, -abs(howManyDays))
            return convertCalenderToString(
                calendar,
                DATE_FORMAT
            )
        }


    }
}