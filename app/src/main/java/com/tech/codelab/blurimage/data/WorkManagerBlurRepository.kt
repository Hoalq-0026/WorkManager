package com.tech.codelab.blurimage.data

import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.tech.codelab.blurimage.KEY_BLUR_LEVEL
import com.tech.codelab.blurimage.KEY_IMAGE_URI
import com.tech.codelab.blurimage.getImageUri
import com.tech.codelab.blurimage.workers.BlurWorker
import com.tech.codelab.blurimage.workers.CleanupWorker
import com.tech.codelab.blurimage.workers.SaveImageToFileWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class WorkManagerBlurRepository(context: Context) : BlurRepository {
    private val imageUri: Uri = context.getImageUri()
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo?> = MutableStateFlow(null)


    /**
     * Create the WorkRequests to apply the blur and save the resulting image
     * @param blurLevel The amount to blur image
     */
    override fun applyBlur(blurLevel: Int) {
        // Add workRequest to Cleanup tempoary images
        var continuation = workManager.beginWith(OneTimeWorkRequest.Companion.from(CleanupWorker::class.java))

        // Create WorkRequest to blur the image
        val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()

        // For input data object
        blurBuilder.setInputData(createInputDataForWorkRequest(blurLevel, imageUri))


        continuation = continuation.then(blurBuilder.build())

        // Add WorkRequest to save the image to the filesystem
        val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>().build()

        // Start the work
//        workManager.enqueue(blurBuilder.build())
        continuation = continuation.then(save)
        continuation.enqueue()

    }

    /**
     * Cancel any ongoing WorkRequest
     */
    override fun cancelWork() {
        TODO("Not yet implemented")
    }

    /**
     * Creates the input data bundle which includes the blur level to
     * update the amount of blur to be applied and the Uri to operate on
     * @return Data which contains the Image Uri as a string and blur level as an Integer
     */
    private fun createInputDataForWorkRequest(blurLevel: Int, imageUri: Uri): Data {
        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_URI, imageUri.toString()).putInt(KEY_BLUR_LEVEL, blurLevel)
        return builder.build()
    }
}