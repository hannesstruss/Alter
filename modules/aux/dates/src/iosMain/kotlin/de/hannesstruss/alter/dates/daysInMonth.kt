package de.hannesstruss.alter.dates

import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDayCalendarUnit
import platform.Foundation.NSMonthCalendarUnit

internal actual fun daysInMonth(year: Int, month: Int): Int {
  val today = NSDate.new()!!
  val calendar = NSCalendar.currentCalendar
  val days = calendar.rangeOfUnit(
    NSDayCalendarUnit,
    NSMonthCalendarUnit,
    today
  )
  val nsRange = memScoped {
    days.ptr.getPointer(this).get(0)
  }
  return nsRange.length.toInt()
}
