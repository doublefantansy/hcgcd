package hzkj.cc.base

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
  val YMD = "yyyy-MM-dd"
  val YMDH = "yyyy-MM-dd HH"
  val YMDHMS = "yyyy-MM-dd HH:mm:ss"
  val YMDHMS_SPE = "yyyy/MM/dd HH:mm:ss"
  val HMS = "HH:mm:ss"
  fun dateToFormatString(
    date: Date?,
    pattern: String = YMDHMS
  ): String? {
    return date?.let {
      SimpleDateFormat().apply {
        applyPattern(pattern)
      }
          .format(date)
    }
  }

  fun formatStringToDate(
    string: String?,
    pattern: String = YMDHMS
  ): Date? {
    return string?.let {
      SimpleDateFormat().apply {
        applyPattern(pattern)
      }
          .parse(string)
    }
  }

  fun formatStringToString(
    string: String?,
    pattern: String
  ): String? {
    return SimpleDateFormat().apply {
      applyPattern(pattern)
    }
        .format(formatStringToDate(string))
  }

  fun getFirstDayInMonth(
    calendar: Calendar,
    pattern: String = YMD
  ): String? {
    return (dateToFormatString(calendar.apply {
      set(Calendar.DAY_OF_MONTH, 1)
    }.time, pattern))
  }

  fun formatTimeStr(str: String): String? {
    var newStr = StringBuffer()
    var strs = str.split("-")
    for (p in 0..strs.size - 1) {
      var s = strs[p]
      if (s.toInt() < 10) {
        newStr.append("0$s")
      } else {
        newStr.append("$s")
      }
      if (p != strs.size - 1) {
        newStr.append("-")
      }
    }
    return newStr.toString()
  }
}