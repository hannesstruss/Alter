package de.hannesstruss.alter.di

import de.hannesstruss.alter.features.babydetail.BabyDetailDependencies
import de.hannesstruss.alter.features.list.BabyListDependencies

interface FeatureDependencies :
  BabyListDependencies,
  BabyDetailDependencies
