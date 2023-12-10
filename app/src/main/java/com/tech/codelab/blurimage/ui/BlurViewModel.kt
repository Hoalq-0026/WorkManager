package com.tech.codelab.blurimage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tech.codelab.blurimage.BlurApplication
import com.tech.codelab.blurimage.data.BlurAmountData
import com.tech.codelab.blurimage.data.BlurRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BlurViewModel(private val blurRepository: BlurRepository) : ViewModel() {
    internal val blurAmount = BlurAmountData.blurAmount

    val blurUiState: StateFlow<BlurUiState> = MutableStateFlow(BlurUiState.Default)

    /**
     * Call the method from repository to create the WorkRequest to apply the blur
     * and save the resulting image
     */
    fun applyBlur(blurLevel: Int) {
        blurRepository.applyBlur(blurLevel)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val blurRepository =
                    (this[APPLICATION_KEY] as BlurApplication).container.blurRepository
                BlurViewModel(blurRepository)
            }
        }
    }
}

sealed interface BlurUiState {

    object Default : BlurUiState

    object Loading : BlurUiState

    data class Complete(val outputUri: String) : BlurUiState
}