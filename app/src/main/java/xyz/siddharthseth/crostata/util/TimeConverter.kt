package xyz.siddharthseth.crostata.util

import android.app.Application
import java.text.SimpleDateFormat
import java.util.*

class TimeConverter(val app: Application, val calendar: Calendar) {
    private val simpleDateFormatRFC3339 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT)
    private val simpleDateFormatDayDate = SimpleDateFormat("EEE dd/MM/yy", Locale.getDefault())
    private val simpleDateFormatDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    private val simpleDateFormatDateWoYear = SimpleDateFormat("MMM dd", Locale.getDefault())
    private val simpleDateFormatDateShortForm =
        SimpleDateFormat("MMM dd, ''yy", Locale.getDefault())
    private val simpleDateFormatTime =
        if (android.text.format.DateFormat.is24HourFormat(app))
            SimpleDateFormat("HH:mm", Locale.getDefault())
        else SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun getRFC3339DateString(timestamp: Long): String {
        calendar.time = Date(timestamp)
        return simpleDateFormatRFC3339.format(calendar.time)
    }

    fun getFormattedDateWithDay(dateString: String): String {
        val date = simpleDateFormatRFC3339.parse(dateString)
        val startTimestamp: Long = date.time

        calendar.time = Date(startTimestamp)
        return simpleDateFormatDayDate.format(calendar.time)
    }

    fun getFormattedDate(dateString: String, isShortForm: Boolean = false): String {
        val startTimestamp: Long = simpleDateFormatRFC3339.parse(dateString).time

        calendar.time = Date(startTimestamp)
        return if (isShortForm) simpleDateFormatDateShortForm.format(calendar.time) else simpleDateFormatDate.format(
            calendar.time
        )
    }

    fun getFormattedDateWoYear(dateString: String): String {
        val date = simpleDateFormatRFC3339.parse(dateString)
        val startTimestamp: Long = date.time

        calendar.time = Date(startTimestamp)
        return simpleDateFormatDateWoYear.format(calendar.time)
    }

    fun getFormattedTime(dateString: String): String {
        val date = simpleDateFormatRFC3339.parse(dateString)
        val startTimestamp: Long = date.time

        calendar.time = Date(startTimestamp)
        return simpleDateFormatTime.format(calendar.time).uppercase(Locale.getDefault())
    }

//    fun getDuration(timeStamp: Long, inWords: Boolean, isPrecise: Boolean = true): String {
//        var hours = 0L
//        var minutes = 0L
//        var seconds = 0L
//        if (timeStamp > 0L) {
//            hours = (timeStamp / 1000) / 3600
//            minutes = ((timeStamp / 1000) % 3600) / 60
//            seconds = ((timeStamp / 1000) % 60)
//        }
//        return if (!inWords) {
//            hours.toString() + ":" +
//                    (if (minutes > 9) minutes.toString() else "0$minutes") + ":" +
//                    (if (seconds > 9) seconds.toString() else "0$seconds")
//        } else {
//            if (hours < 1 && minutes < 1) {
//                app.resources.getQuantityString(
//                    R.plurals.symbol_time_second,
//                    seconds.toInt(),
//                    seconds
//                )
//            } else if (hours < 1 && seconds == 0L) {
//                app.resources.getQuantityString(
//                    R.plurals.symbol_time_minute,
//                    minutes.toInt(),
//                    minutes
//                )
//            } else if (hours < 1) {
//                if (!isPrecise)
//                    app.resources.getQuantityString(
//                        R.plurals.symbol_time_minute,
//                        minutes.toInt(),
//                        minutes
//                    ) +
//                            " " +
//                            app.resources.getQuantityString(
//                                R.plurals.symbol_time_second,
//                                seconds.toInt(),
//                                seconds
//                            )
//                else app.resources.getQuantityString(
//                    R.plurals.symbol_time_minute,
//                    minutes.toInt(),
//                    minutes
//                )
//            } else
//                app.resources.getQuantityString(R.plurals.symbol_time_hour, hours.toInt(), hours) +
//                        " " +
//                        app.resources.getQuantityString(
//                            R.plurals.symbol_time_minute,
//                            minutes.toInt(),
//                            minutes
//                        )
//        }
//    }

    fun getTimestamp(dateString: String): Long {
        val date = simpleDateFormatRFC3339.parse(dateString)
        return date.time
    }

    fun isInSameMonth(dateString: String): Boolean {
        calendar.time = Date(System.currentTimeMillis())
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val date = simpleDateFormatRFC3339.parse(dateString)
        calendar.time = date

        return calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear
    }

//    fun getGreeting(name: String): String {
//        calendar.time = Date(System.currentTimeMillis())
//        return when (Locale.getDefault()) {
//            Locale.FRANCE -> String.format(
//                when (calendar.get(Calendar.HOUR_OF_DAY)) {
//                    17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3 -> app.getString(R.string.home_greeting_evening)
//                    else -> app.getString(R.string.home_greeting_morning)
//                }, name
//            )
//            else -> String.format(
//                when (calendar.get(Calendar.HOUR_OF_DAY)) {
//                    12, 13, 14, 15, 16 -> app.getString(R.string.home_greeting_afternoon)
//                    17, 18, 19, 20, 21, 22, 23, 0, 1, 2, 3 -> app.getString(R.string.home_greeting_evening)
//                    else -> app.getString(R.string.home_greeting_morning)
//                }, name
//            )
//        }
//    }
//
//
//    fun getDayEvent(): String {
//        calendar.time = Date(System.currentTimeMillis())
//        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
//            12, 13, 14, 15, 16 -> app.getString(R.string.time_event_afternoon)
//            17, 18, 19, 20, 21 -> app.getString(R.string.time_event_evening)
//            22, 23, 0, 1, 2, 3 -> app.getString(R.string.time_event_night)
//            else -> app.getString(R.string.time_event_morning)
//        }
//    }
//
//    fun getMonthYear(date: String): String {
//        calendar.time = simpleDateFormatRFC3339.parse(date)
//        val currentMonth =
//            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
//                .toUpperCase(Locale.getDefault())
//        val currentYear = calendar.get(Calendar.YEAR)
//        return "$currentMonth, $currentYear"
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getAge(date: String): String {
//        val birthDate = LocalDate.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
//        val currentDate = LocalDate.now()
//        val period = Period.between(birthDate, currentDate)
//        return String.format(app.getString(R.string.template_time_converter_age), period.years)
//    }
}