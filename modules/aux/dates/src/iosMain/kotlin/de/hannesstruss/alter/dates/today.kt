package de.hannesstruss.alter.dates

import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDayCalendarUnit
import platform.Foundation.NSMonthCalendarUnit
import platform.Foundation.NSYearCalendarUnit

actual fun today(): Date {
  return NSDate.new()!!.toAlterDate()
}

actual typealias PlatformDate = NSDate

actual fun Date.toPlatformDate(): PlatformDate {
  val comps = NSDateComponents()
  comps.day = day.toLong()
  comps.month = month.toLong()
  comps.year = year.toLong()

  return checkNotNull(NSCalendar.currentCalendar.dateFromComponents(comps)) {
    "Creating NSDate from $year-$month-$day failed"
  }
}

actual fun PlatformDate.toAlterDate(): Date {
  val calendar = NSCalendar.currentCalendar
  val comps =
    calendar.components(NSDayCalendarUnit or NSMonthCalendarUnit or NSYearCalendarUnit, this)

  return Date(
    year = comps.year.toInt(),
    month = comps.month.toInt(),
    day = comps.day.toInt()
  )
}
