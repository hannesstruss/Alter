package de.hannesstruss.alter.features.editbaby

import android.view.View
import com.google.android.material.picker.MaterialDatePicker
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import de.hannesstruss.alter.flowextensions.mergeFlows
import flowbinding.android.clicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent

class EditBabyFragment :
  FeatureDependencyProvidingFragment<EditBabyState, EditBabyEvent, EditBabyViewModel>() {
  companion object {
    private const val DatePickerTag = "DatePicker"
  }

  override val layout = R.layout.edit_baby_fragment
  override val viewModelClass = EditBabyViewModel::class.java

  override fun createFeatureComponent(): FeatureComponent<EditBabyViewModel> {
    return DaggerEditBabyComponent.factory().create(getFeatureDependencies())
  }

  override fun events(): Flow<EditBabyEvent> {
    return mergeFlows(
      requireView().findViewById<View>(R.id.btnPickDateOfBirth).clicks().map { EditBabyEvent.PickDateOfBirth }
    )
  }

  override fun render(state: EditBabyState) {
    val datePickerExists = requireFragmentManager().findFragmentByTag(DatePickerTag) != null
    val datePicker: MaterialDatePicker<Long> =
      requireFragmentManager().findFragmentByTag(DatePickerTag) as? MaterialDatePicker<Long>
        ?: run {
          MaterialDatePicker.Builder.datePicker().build()
        }

    if (state.showDateOfBirthPicker && !datePicker.isVisible) {
      datePicker.show(requireFragmentManager(), DatePickerTag)
      datePicker.addOnPositiveButtonClickListener {  }
    } else if (!state.showDateOfBirthPicker && datePickerExists && datePicker.isVisible) {
      datePicker.dismiss()
    }
  }
}

