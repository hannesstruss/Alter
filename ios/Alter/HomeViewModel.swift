//
//  HomeViewModel.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import Foundation
import Combine
import AlterCommon

class HomeViewModel: ObservableObject {
  let babyRepository: IosBabyRepository
  @Published var state = HomeState()
  private var id: Int = 0
  private var disposables = Set<AnyCancellable>()

  init(babyRepository: IosBabyRepository) {
    self.babyRepository = babyRepository

    babyRepository.getLocalBabies()
      .sink(receiveValue: { babies in
        self.state.babies = babies
        print("\(babies.map { $0.name })")
      })
      .store(in: &disposables)
  }
}
