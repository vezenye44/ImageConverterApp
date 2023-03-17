package com.example.imageconverterapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.imageconverterapp.data.BitmapConverterImpl
import com.example.imageconverterapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ConverterContract.View {

    private lateinit var binding: ActivityMainBinding
    private val presenter: ConverterContract.Presenter by lazy {
        ConvertedPresenter(BitmapConverterImpl())
    }
    private val openLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(), presenter.openLauncherCallback
    )
    private val saveLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("image/png"), presenter.saveLauncherCallback
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        presenter.attach(this)
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    private fun initView() {
        binding.activityMainOpenFilesBtn.setOnClickListener {
            presenter.onOpenImageClick()
        }
        binding.activityMainConvertBtn.setOnClickListener {
            presenter.onConvertImageClick()
        }
        binding.activityMainSaveBtn.setOnClickListener {
            presenter.onSaveImageClick()
        }
    }

    override fun launchOpenFile() {
        openLauncher.launch("image/png")
    }

    override fun launchSaveFile() {
        saveLauncher.launch("my-file.png")
    }

    override fun openFile(uri: Uri) {
        val data = contentResolver.openInputStream(uri)?.use {
            val bitmap = BitmapFactory.decodeStream(it)
            presenter.openFileSuccess(bitmap)
        } ?: throw IllegalStateException("Can't open input stream")
    }

    override fun saveFile(uri: Uri, bitmapXByteArray: ByteArray) {
        contentResolver.openOutputStream(uri)?.use {
            it.write(bitmapXByteArray)
            presenter.saveFileSuccess()
        } ?: throw IllegalStateException("Can't open output stream")
    }

    override fun convertBtnEnable(isEnable: Boolean) {
        binding.activityMainConvertBtn.isEnabled = isEnable
    }

    override fun saveBtnEnable(isEnable: Boolean) {
        binding.activityMainSaveBtn.isEnabled = isEnable
    }

    override fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
    }

    override fun showOriginalImage(bitmap: Bitmap) {
        binding.activityMainOriginalImageView.setImageBitmap(bitmap)
    }

    override fun showConvertedImage(bitmap: Bitmap) {
        binding.activityMainConvertedImageView.setImageBitmap(bitmap)
    }
}