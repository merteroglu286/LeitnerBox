package com.merteroglu286.leitnerbox.presentation.base

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.merteroglu286.leitnerbox.R
import com.merteroglu286.leitnerbox.utility.LoadingDialog
import com.merteroglu286.leitnerbox.utility.enums.WhichBoxEnum
import com.merteroglu286.leitnerbox.utility.extension.cast
import com.merteroglu286.leitnerbox.utility.extension.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.reflect.ParameterizedType

@ExperimentalCoroutinesApi
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    abstract fun getViewBinding(): VB

    open fun setListeners() {}

    open fun setReceivers() {}

    open fun initUI() {}

    private lateinit var viewModel: VM

    lateinit var binding: VB


    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = getViewBinding()
        setContentView(binding.root)

        val clazz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
            .cast<Class<VM>>()

        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[clazz]
        }

        initUI()

    }

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }

        loadingDialog?.apply {
            if (isShowing.not()) {
                show()
            }
        }
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
    }

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    fun showErrorPopup(errorCode: Int, errorMessage: String) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.error_layout)
        dialog.window?.let {
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.85f)
        }

        with(dialog) {
            findViewById<TextView>(R.id.messageEditText).text = errorMessage

            findViewById<MaterialButton>(R.id.okButton).setOnClickListener { dismiss() }
        }

        dialog.show()

    }

    fun showSuccessPopup(message: String, dismiss: () -> Unit) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_layout)
        dialog.window?.let {
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.85f)
        }

        with(dialog) {
            findViewById<TextView>(R.id.messageEditText).text = message

            findViewById<MaterialButton>(R.id.okButton).setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()

        dialog.setOnDismissListener { dismiss() }

    }

    fun showConfirmPopup(
        image: Uri,
        whichBoxEnum: WhichBoxEnum,
        editImageButton: () -> Unit,
        okButton: () -> Unit,
        cancelButton: () -> Unit
    ) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_confirm_dialog)
        dialog.window?.let {
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.85f)
        }

        with(dialog) {

            val okButtonId = findViewById<MaterialButton>(R.id.okButton)
            val cancelButtonId = findViewById<MaterialButton>(R.id.cancelButton)

            when (whichBoxEnum) {
                WhichBoxEnum.NONE -> {
                    okButtonId.text = "İlk Kutuya At"
                    cancelButtonId.text = "İptal"
                }
                WhichBoxEnum.FIRST_BOX -> {
                    okButtonId.text = "İkinci Kutuya At"
                    cancelButtonId.text = "İptal"
                }
                WhichBoxEnum.SECOND_BOX -> {
                    okButtonId.text = "Üçüncü Kutuya At"
                    cancelButtonId.text = "İlk Kutuya At"
                }
                WhichBoxEnum.THIRD_BOX -> {
                    okButtonId.text = "Dördüncü Kutuya At"
                    cancelButtonId.text = "İkinci Kutuya At"
                }
                WhichBoxEnum.FOURTH_BOX -> {
                    okButtonId.text = "Tamamla"
                    cancelButtonId.text = "Üçüncü Kutuya At"
                }
                else -> {
                    okButtonId.text = "Tamamla"
                    cancelButtonId.text = "İptal"
                }
            }

            findViewById<ImageView>(R.id.imageView).setImageURI(image)
            findViewById<MaterialButton>(R.id.editImageButton).setOnClickListener {
                dialog.dismiss()
                editImageButton()
            }

            okButtonId.setOnClickListener {
                dialog.dismiss()
                okButton()
            }

            cancelButtonId.setOnClickListener {
                dialog.dismiss()
                cancelButton()
            }
        }

        dialog.show()
    }

    fun showConfirmPopup(
        imageUrl: String,
        whichBoxEnum: WhichBoxEnum,
        editImageButton: () -> Unit,
        okButton: () -> Unit,
        cancelButton: () -> Unit
    ) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_confirm_dialog)
        dialog.window?.let {
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.85f)
        }

        with(dialog) {
            findViewById<ImageView>(R.id.imageView).loadImage(imageUrl)
            val okButtonId = findViewById<MaterialButton>(R.id.okButton)
            val cancelButtonId = findViewById<MaterialButton>(R.id.cancelButton)

            when (whichBoxEnum) {
                WhichBoxEnum.NONE -> {
                    okButtonId.text = "İlk Kutuya At"
                    cancelButtonId.text = "İptal"
                }
                WhichBoxEnum.FIRST_BOX -> {
                    okButtonId.text = "İkinci Kutuya At"
                    cancelButtonId.text = "İptal"
                }
                WhichBoxEnum.SECOND_BOX -> {
                    okButtonId.text = "Üçüncü Kutuya At"
                    cancelButtonId.text = "İlk Kutuya At"
                }
                WhichBoxEnum.THIRD_BOX -> {
                    okButtonId.text = "Dördüncü Kutuya At"
                    cancelButtonId.text = "İkinci Kutuya At"
                }
                WhichBoxEnum.FOURTH_BOX -> {
                    okButtonId.text = "Tamamla"
                    cancelButtonId.text = "Üçüncü Kutuya At"
                }
                else -> {
                    okButtonId.text = "Tamamla"
                    cancelButtonId.text = "İptal"
                }
            }

            findViewById<MaterialButton>(R.id.editImageButton).setOnClickListener {
                dialog.dismiss()
                editImageButton()
            }

            okButtonId.setOnClickListener {
                dialog.dismiss()
                okButton()
            }
            cancelButtonId.setOnClickListener {
                dialog.dismiss()
                cancelButton()
            }
        }

        dialog.show()
    }


}