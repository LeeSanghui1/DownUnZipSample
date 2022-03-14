package com.mackerly.sample.downunzip

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.await
import java.io.*
import java.lang.Exception
import java.util.zip.ZipException

class SqlLiteDownUnzipViewModel: ViewModel(){
    private var _downloadProgress = MutableLiveData<Int>()
    val downloadProgress: LiveData<Int>
        get() = _downloadProgress

    private var downloadPath = MutableLiveData<String>()

    private var _isUnZip = MutableLiveData<Boolean>()
    val isUnZip: LiveData<Boolean>
        get() = _isUnZip


    fun requestAndDownloadFile() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()

        val sqlLiteDownloadService = retrofit.create(SqlLiteDownloadService::class.java)
        val sqlLiteDownloadCall = sqlLiteDownloadService.requestZipFile(SQLLITE_RELEASE_YEAR, SQLLITE_FILENAME)

        viewModelScope.launch {
            try {
                val responseBody = sqlLiteDownloadCall.await()

                val targetPath = Environment.getExternalStorageDirectory().path + "/Download/" + SQLLITE_FILENAME
                writeResponseBodyToDisk(responseBody, targetPath)
                downloadPath.value = targetPath
            } catch (e: Exception) {
                Log.d(TAG, "download failure")
            }
        }
    }

    fun unZip() {
        val sourcePath = downloadPath.value
        val targetPath = sourcePath?.replace(".zip", "")
        Log.d(TAG, "unZip filePath: $sourcePath , targetPath: $targetPath")
        try {
            ZipFile(sourcePath).extractAll(targetPath)
            _isUnZip.value = true
        } catch (e: ZipException) {
            Log.d(TAG, "unZip failure")
            _isUnZip.value = false
        }
    }

    private fun writeResponseBodyToDisk(responseBody: ResponseBody, targetFilePath: String) {
        var bodyIs: InputStream? = null
        var os: OutputStream? = null
        try {
            bodyIs = responseBody.byteStream()
            os = FileOutputStream(File(targetFilePath))
            var readLen: Int
            val buffer = ByteArray(1024)
            var current = 0L
            while(true) {
                readLen = bodyIs.read(buffer)
                if(readLen == -1) {
                    break
                }
                current += readLen
                os.write(buffer, 0, readLen)
                updateDownloadProgress(responseBody.contentLength(), current)
            }
        } finally {
            bodyIs?.close()
            os?.close()
        }
    }

    private fun updateDownloadProgress(total: Long, current: Long) {
        _downloadProgress.value = (current / total.toDouble() * 100).toInt()
    }

    companion object {
        private const val TAG = "SqlLiteDownUnZipVM"
        private const val BASE_URL = "https://www.sqlite.org"
        private const val SQLLITE_RELEASE_YEAR = 2022
        private const val SQLLITE_FILENAME = "sqlite-tools-linux-x86-3380000.zip"
    }
}