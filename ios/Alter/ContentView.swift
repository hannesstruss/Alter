//
//  ContentView.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import SwiftUI
import AlterCommon

struct ContentView: View {
  let babyRepository: IosBabyRepository
  @State var showAddBaby: Bool = false
  @ObservedObject var viewModel: HomeViewModel

  init() {
    babyRepository = IosBabyRepository()
    viewModel = HomeViewModel(babyRepository: babyRepository)
  }

  var body: some View {
    NavigationView {
      List(viewModel.state.babies) { baby in
        NavigationLink(destination: BabyDetailView(babyRepository: self.babyRepository, baby: baby)) {
          HStack {
            Text(baby.name)
            if (baby.bornOn != nil) {
              Text(Age.Companion().of(dob: baby.bornOn!, today: TodayKt.today()).format())
            }
          }
        }
      }
      .navigationBarTitle("Babies", displayMode: .inline)
      .navigationBarItems(
        trailing: Button(action: {
          self.showAddBaby = true
        }) {
          Image(systemName: "plus")
        }.sheet(isPresented: self.$showAddBaby) {
          AddBabyView(show: self.$showAddBaby, babyRepository: self.babyRepository)
        }
      )
    }
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    ContentView()
  }
}
