package com.tech.codelab.blurimage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.tech.codelab.blurimage.workers.BlurWorker
import com.tech.codelab.blurimage.workers.CleanupWorker
import com.tech.codelab.blurimage.workers.SaveImageToFileWorker
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test

class WorkerInstrumentationTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun cleanupWorker_doWork_resultSuccess() {
        val worker = TestListenableWorkerBuilder<CleanupWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }

    @Test
    fun blurWorker_doWork_resultSuccessReturnsUri() {
        val mockUriInput =
            workDataOf(KEY_IMAGE_URI to "android.resource://com.example.bluromatic/drawable/android_cupcake")
        val worker = TestListenableWorkerBuilder<BlurWorker>(context)
            .setInputData(mockUriInput)
            .build()
        runBlocking {
            val result = worker.doWork()
            val resultUri = result.outputData.getString(KEY_IMAGE_URI)
            assertTrue(result is ListenableWorker.Result.Success)
            assertTrue(result.outputData.keyValueMap.containsKey(KEY_IMAGE_URI))
            assertTrue(
                resultUri?.startsWith("file:///data/user/0/com.example.bluromatic/files/blur_filter_outputs/blur-filter-output-")
                    ?: false
            )
        }

    }

    fun saveImageToFileWorker_doWork_resultSuccessReturnsUri() {
        val mockUriInput =
            workDataOf(KEY_IMAGE_URI to "android.resource://com.example.bluromatic/drawable/android_cupcake")
        val worker =
            TestListenableWorkerBuilder<SaveImageToFileWorker>(context)
                .setInputData(mockUriInput)
                .build()

        runBlocking {
            val result = worker.doWork()
            val resultUri = result.outputData.getString(KEY_IMAGE_URI)
            assertTrue(result is ListenableWorker.Result.Success)
            assertTrue(result.outputData.keyValueMap.containsKey(KEY_IMAGE_URI))
            assertTrue(resultUri?.startsWith("content://media/external/images/media/") ?: false)
        }

    }


}