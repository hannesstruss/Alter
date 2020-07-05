//
//  AppDelegate.swift
//  Alter
//
//  Created by Hannes Struss on 07.12.19.
//  Copyright © 2019 Hannes. All rights reserved.
//

import UIKit
import AlterCommon
import Combine

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {



  func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    print("And new: \(AlterCommonNiceKt.getStringFromAlterCommon())")

//    let repo = IosBabyRepository()
//    repo.getBabies().executeAsList().forEach { baby in
//      print("Baby: \(baby.name)")
//    }

//    let pub: AnyPublisher<RuntimeQuery<DbBaby>, Never> = queryAsPublisher(repo.getBabies())
//    _ = pub.sink(receiveValue: { query in
//      query.executeAsList().forEach { baby in
//        print("Combine baby: \(baby.name)")
//      }
//    })

    return true
  }

  // MARK: UISceneSession Lifecycle

  func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
    // Called when a new scene session is being created.
    // Use this method to select a configuration to create the new scene with.
    return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
  }

  func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
    // Called when the user discards a scene session.
    // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
    // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
  }


}

