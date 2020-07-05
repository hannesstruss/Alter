//
//  PrettyAge.swift
//  Alter
//
//  Created by Hannes Struss on 09.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import Foundation

enum PrettyAge {
  case days(_ days: Int)
  case weeks(_ weeks: Int)
  case months(_ months: Int, weeks: Int)
  case years(_ years: Int)

  static func of(dob: Date, now: Date) -> Self {
    if dob > now {
      fatalError("dob > now")
    }

    let calendar = Calendar.current
    let daysOld = Int(dob.distance(to: now)) / (60 * 60 * 24)
    let weeksOld = daysOld / 7

    if daysOld <= 14 {
      let days: Int = Int(dob.distance(to: now)) / (60 * 60 * 24)
      return .days(days)
    }

    if weeksOld <= 12 {
      return .weeks(weeksOld)
    }

    var oneYearComponents = DateComponents()
    oneYearComponents.year = -1
    let oneYearBeforeNow = calendar.date(byAdding: oneYearComponents, to: now)!

    if dob > oneYearBeforeNow {
      let nowMonths = calendar.component(.month, from: now)
      let nowDayOfMonth = calendar.component(.day, from: now)
      let dobMonths = calendar.component(.month, from: dob)
      let dobDayOfMonth = calendar.component(.day, from: dob)
      var monthsBetween = (nowMonths - dobMonths + 12) % 12

      if monthsBetween > 0 && dobDayOfMonth > nowDayOfMonth {
        monthsBetween -= 1
      } else if monthsBetween == 0 {
        monthsBetween = 12
      }

      var monthsBeforeComponents = DateComponents()
      monthsBeforeComponents.month = -monthsBetween
      let monthsBefore = calendar.date(byAdding: monthsBeforeComponents, to: now)!
      let daysBetweenMonthsBefore = Int(dob.distance(to: monthsBefore) / (60 * 60 * 24))

      return .months(monthsBetween, weeks: daysBetweenMonthsBefore / 7)
    }

    let nowYear = calendar.component(.year, from: now)
    var years = nowYear - calendar.component(.year, from: dob)

    let dobWithTodaysYear = calendar.date(bySetting: .year, value: nowYear, of: dob)!
    if dobWithTodaysYear > now {
      years -= 1
    }
    return .years(years)
  }
}
