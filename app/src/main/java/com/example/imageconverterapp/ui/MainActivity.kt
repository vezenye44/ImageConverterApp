package com.example.imageconverterapp.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.imageconverterapp.data.BitmapConverterImpl
import com.example.imageconverterapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ConverterContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: ConverterContract.Presenter

    private lateinit var openLauncher: ActivityResultLauncher<String>
    private lateinit var saveLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = extractPresenter()
        presenter.attach(this)

        initLaunchers()
        initView()
    }

    private fun initLaunchers() {
        openLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent(), presenter.openLauncherCallback
        )
        saveLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("image/png"), presenter.saveLauncherCallback
        )
    }

    private fun extractPresenter(): ConverterContract.Presenter {
        return lastCustomNonConfigurationInstance as? ConverterContract.Presenter
            ?: ConvertedPresenter(BitmapConverterImpl())
    }

    override fun onRetainCustomNonConfigurationInstance(): ConverterContract.Presenter {
        return presenter
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

    override fun openFile(uri: Uri): Bitmap {
        contentResolver.openInputStream(uri)?.use {
            return BitmapFactory.decodeStream(it)
        } ?: throw IllegalStateException("Can't open input stream")
    }

    override fun saveFile(uri: Uri, bitmapXByteArray: ByteArray) {
        contentResolver.openOutputStream(uri)?.use {
            it.write(bitmapXByteArray)
        } ?: throw IllegalStateException("Can't open output stream")
    }

    override fun convertBtnEnable(isEnable: Boolean) {
        binding.activityMainConvertBtn.isEnabled = isEnable
    }

    override fun saveBtnEnable(isEnable: Boolean) {
        binding.activityMainSaveBtn.isEnabled = isEnable
    }

    override fun clearConvertedImage() {
        binding.activityMainConvertedImageView.setImageDrawable(null)
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