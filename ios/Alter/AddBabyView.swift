//
//  AddBabyView.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import SwiftUI
import AlterCommon

struct AddBabyView: View {
  @Binding var show: Bool
  @State private var bornOn: AlterCommon.Date? = nil
  @State private var dueOn: AlterCommon.Date? = nil
  @State private var parents: String = ""
  @State private var name: String = ""

  let babyRepository: IosBabyRepository

  private let defaultDate = TodayKt.today().toPlatformDate()

  var body: some View {
    NavigationView {
      List {
        TextField("Name", text: $name)
        TextField("Parents", text: $parents)
        OptionalDatePicker(name: "Birthday", defaultDate: defaultDate, binding: $bornOn.toPlatformDate())
        OptionalDatePicker(name: "Due Date", defaultDate: defaultDate, binding: $dueOn.toPlatformDate())
      }
      .navigationBarTitle("New Baby", displayMode: .inline)
      .navigationBarItems(
        leading: Button(action: {
          self.show = false
        }, label: {
          Text("Cancel")
        }),
        trailing: Button(action: {
          self.babyRepository.addBaby(
            name: self.name,
            parents: self.parents,
            bornOn: self.bornOn,
            dueOn: self.dueOn
          )
          self.show = false
        }, label: {
          Text("Add")
        }).disabled(!self.isFormValid)
      )
    }
  }

  private var isFormValid: Bool {
    return !name.isBlank && !parents.isBlank
  }
}

extension Binding where Value == AlterCommon.Date? {
  func toPlatformDate() -> Binding<Foundation.Date?> {
    return Binding<Foundation.Date?>(
      get: {
        return self.wrappedValue?.toPlatformDate()
      }, set: {
        if ($0 == nil) {
          self.wrappedValue = nil
        } else {
          let alterDate = TodayKt.toAlterDate($0!)
          self.wrappedValue = alterDate
        }

      }
    )
  }
}

fileprivate extension StringProtocol {
  var isBlank: Bool {
    return trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
  }
}

//struct AddBabyView_Previews: PreviewProvider {
//  static var previews: some View {
//    AddBabyView(show: .constant(true), babyRepository: BabyRepository())
//  }
//}
