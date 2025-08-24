package mn.univision.secretroom.presentation.screens.dynamic

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mn.univision.secretroom.data.repositories.DynamicSectionRepository
import javax.inject.Inject

@HiltViewModel
class DynamicSectionViewModel @Inject constructor(
    private val dynamicSectionRepository: DynamicSectionRepository
) : ViewModel() 