package com.tech.codelab.blurimage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkInfo
import com.tech.codelab.blurimage.BlurApplication
import com.tech.codelab.blurimage.KEY_IMAGE_URI
import com.tech.codelab.blurimage.data.BlurAmountData
import com.tech.codelab.blurimage.data.BlurRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BlurViewModel(private val blurRepository: BlurRepository) : ViewModel() {
    internal val blurAmount = BlurAmountData.blurAmount

    val blurUiState: StateFlow<BlurUiState> = blurRepository.outputWorkInfo.map { info ->
        val outputImageUri = info.outputData.getString(KEY_IMAGE_URI)
        when {
            info.state.isFinished && !outputImageUri.isNullOrEmpty() -> {

                BlurUiState.Complete(outputUri = outputImageUri)
            }

            info.state == WorkInfo.State.CANCELLED -> {
                BlurUiState.Default
            }

            else -> {
                BlurUiState.Loading
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BlurUiState.Default)

    /**
     * Call the method from repository to create the WorkRequest to apply the blur
     * and save the resulting image
     */
    fun applyBlur(blurLevel: Int) {
        blurRepository.applyBlur(blurLevel)
    }

    /**
     * Call method from repository to cancel any ongoing WorkRequest
     */
    fun cancelWork() {
        blurRepository.cancelWork()
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