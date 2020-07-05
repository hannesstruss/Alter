//
//  OptionalDatePicker.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import SwiftUI

struct OptionalDatePicker: View {
  @Binding var binding: Date?
  private var pickerBinding: Binding<Date>
  private let name: String
  private let defaultDate: Date
  private let dateFormatter = DateFormatter()

  init(name: String, defaultDate: Date, binding: Binding<Date?>) {
    self.name = name
    self.defaultDate = defaultDate
    self._binding = binding
    self.pickerBinding = Binding<Date>(
      get: {
        return binding.wrappedValue ?? Date()
      }, set: {
        binding.wrappedValue = $0
      }
    )
    dateFormatter.dateStyle = .short
  }

  var body: some View {
    VStack(alignment: .leading) {
      if binding != nil {
        HStack {
          Button(action: {
            self.binding = nil
          }) {
            Image(systemName: "trash")
          }

          Text(dateFormatter.string(from: binding ?? defaultDate))
        }

        DatePicker(selection: pickerBinding, displayedComponents: .date) {
          EmptyView()
        }

      } else {
        Button(action: { self.binding = self.defaultDate }) {
          Text("Add \(self.name)")
        }
      }
    }
  }
}
