//
//  IosBabyRepository+Extensions.swift
//  Alter
//
//  Created by Hannes Struss on 09.05.20.
//  Copyright © 2020 Hannes. All rights reserved.
//

import Foundation
import Combine
import AlterCommon

extension IosBabyRepository {
  func getLocalBabies() -> AnyPublisher<[Baby], Never> {
    return queryAsPublisher(getBabies())
      .map { query in
        let babies = query.executeAsList().map { dbBaby in
          return Baby.fromDbBaby(dbBaby)
        }

        return babies
      }
      .eraseToAnyPublisher()
  }
}
