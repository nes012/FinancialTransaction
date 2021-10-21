package nesty.anzhy.test.util

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//converts milliseconds to the desired date format
fun convertTimestampToTime(timestamp: Long?): String {
    if (timestamp == null) return ""
    val stamp = Timestamp(timestamp * 1000)
    val date = Date(stamp.time)
    val pattern = "dd/MMM/yyyy, HH:mm"
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}