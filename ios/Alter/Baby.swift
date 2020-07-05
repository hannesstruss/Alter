//
//  Baby.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import Foundation
import AlterCommon

struct Baby: Identifiable {
  let id: Int64
  let name: String
  let parents: String
  let bornOn: AlterCommon.Date?
  let dueOn: AlterCommon.Date?

  static func fromDbBaby(_ dbBaby: DbBaby) -> Baby {
    return Baby(
      id: dbBaby.id,
      name: dbBaby.name,
      parents: dbBaby.parents,
      bornOn: dbBaby.born_at,
      dueOn: dbBaby.due_on
    )
  }
}
