//
//  BabyDetailView.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import Foundation
import SwiftUI
import AlterCommon

struct BabyDetailView: View {
  let babyRepository: IosBabyRepository
  let baby: Baby

  @State private var showDeleteConfirmation: Bool = false
  @State private var wasDeleted: Bool = false

  var body: some View {
    VStack {
      Text("\(baby.name)")
      Text("BDay: \(baby.bornOn?.description ?? "No Birthday")")
      Text("Due: \(baby.dueOn?.description ?? "No due date")")
      Text("\(baby.parents)")
      if (self.wasDeleted) {
        Text("Deleted.")
      }
    }
    .navigationBarItems(trailing: Button(action: {
        self.showDeleteConfirmation = true
      }, label: {
        Text("Delete")
      })
      .alert(isPresented: self.$showDeleteConfirmation) {
        Alert(
          title: Text("Really?"),
          message: Text("Are you sure you want to delete \(self.baby.name)?"),
          primaryButton: .destructive(Text("Delete"), action: {
            self.showDeleteConfirmation = false
            self.babyRepository.delete(id: self.baby.id)
            self.wasDeleted = true
          }),
          secondaryButton: .cancel()
        )
      }
    )
  }
}
