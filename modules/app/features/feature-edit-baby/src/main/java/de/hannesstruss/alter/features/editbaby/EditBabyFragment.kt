package de.hannesstruss.alter.features.editbaby

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.picker.MaterialDatePicker
import com.google.android.material.picker.MaterialPickerOnPositiveButtonClickListener
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.AddBaby
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeDateOfBirth
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeName
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeParents
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.PickDateOfBirth
import de.hannesstruss.alter.features.editbaby.databinding.EditBabyFragmentBinding
import de.hannesstruss.alter.flowextensions.mergeFlows
import de.hannesstruss.alter.view.textIfChanged
import flowbinding.android.clicks
import flowbinding.android.textChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class EditBabyFragment :
  FeatureDependencyProvidingFragment<EditBabyState, EditBabyEvent, EditBabyViewModel, EditBabyFragmentBinding>(),
  EditBabyViewEffects {
  companion object {
    private const val DatePickerTag = "DatePicker"
  }

  override val bindingInflater = infer(EditBabyFragmentBinding::inflate)
  override val viewModelClass = EditBabyViewModel::class.java

  private lateinit var datePicker: MaterialDatePicker<Long>

  override fun createFeatureComponent(): FeatureComponent<EditBabyViewModel> {
    return DaggerEditBabyComponent.factory().create(
      getFeatureDependencies(),
      this
    )
  }

  override fun EditBabyFragmentBinding.events(): Flow<EditBabyEvent> {
    return mergeFlows(
      txtName.textChanges().map { ChangeName(it.toString()) },
      txtParents.textChanges().map { ChangeParents(it.toString()) },
      btnPickDateOfBirth.clicks().map { PickDateOfBirth },
      datePicker.positiveButtonClicks()
        .map { ChangeDateOfBirth(datePicker.selectedDate!!) },
      btnAdd.clicks().map { AddBaby }
    )
  }

  override fun EditBabyFragmentBinding.onViewBound() {
    datePicker =
      childFragmentManager.find(DatePickerTag) ?: MaterialDatePicker.Builder.datePicker().build()
  }

  override fun EditBabyFragmentBinding.render(state: EditBabyState) {
    txtDob.setText(state.birthDate.toString())
    txtName.textIfChanged = state.name
    txtParents.textIfChanged = state.parents
  }

  override fun showDatePicker() {
    datePicker.show(childFragmentManager, DatePickerTag)
  }

  private fun <T : Fragment> FragmentManager.find(tag: String): T? {
    @Suppress("UNCHECKED_CAST")
    return findFragmentByTag(tag) as? T
  }

  private fun <S> MaterialDatePicker<S>.positiveButtonClicks(): Flow<Unit> = callbackFlow {
    val listener = MaterialPickerOnPositiveButtonClickListener<S> { offer(Unit) }
    addOnPositiveButtonClickListener(listener)
    awaitClose {
      removeOnPositiveButtonClickListener(listener)
    }
  }

  private val MaterialDatePicker<Long>.selectedDate: LocalDate?
    get() {
      return selection?.let {
        // Material Date Picker uses system time zone instead of UTC.
        val zoneId = ZoneId.systemDefault()

        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId).toLocalDate()
      }
    }
}
