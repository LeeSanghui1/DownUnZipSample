package com.mackerly.sample.downunzip

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.mackerly.sample.downunzip.databinding.ActivitySqlliteDownUnzipBinding

class SqlLiteDownUnzipActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySqlliteDownUnzipBinding
    private val viewModel: SqlLiteDownUnzipViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sqllite_down_unzip)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        binding.btnRequestZipFile.setOnClickListener { viewModel.requestAndDownloadFile() }
        binding.btnUnZip.setOnClickListener { viewModel.unZip() }
    }

    private fun requestPermissions() {
        val wPermission: Int = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (wPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

}