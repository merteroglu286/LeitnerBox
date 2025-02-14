package com.merteroglu286.leitnerbox.presentation.fragment.home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.BundleCompat
import androidx.lifecycle.lifecycleScope
import com.merteroglu286.leitnerbox.databinding.FragmentHomeBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import com.merteroglu286.leitnerbox.utility.enums.WhichBoxEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeVM>() {

    private var clickedBox = WhichBoxEnum.NONE
    private var boxStatuses = emptyList<Boolean>()
    private lateinit var userId: String
    private lateinit var lastImageUrl: String

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater, container, false)
    }

    override fun runOnce() {
        super.runOnce()

        getUserId()?.let {
            userId = it
            viewModel.getUser(it)
            viewModel.getCountImagesInBox(it)
        }
    }

    override fun initUI() {
        super.initUI()

        listenImageResult()
    }

    override fun setListeners() {
        super.setListeners()

        with(binding) {
            logoutButton.setOnClickListener {
                viewModel.logout()
                requireActivity().finish()
                viewModel.goSplashScreen()
            }

            uploadButton.setOnClickListener {
                showImageSourceOptions()
            }

            firstBox.setOnClickListener {
                clickedBox = WhichBoxEnum.FIRST_BOX
                viewModel.getOldestPhotoFromBox(userId, WhichBoxEnum.FIRST_BOX.value)
            }
            secondBox.setOnClickListener {
                clickedBox = WhichBoxEnum.SECOND_BOX
                viewModel.getOldestPhotoFromBox(userId, WhichBoxEnum.SECOND_BOX.value)
            }
            thirdBox.setOnClickListener {
                clickedBox = WhichBoxEnum.THIRD_BOX
                viewModel.getOldestPhotoFromBox(userId, WhichBoxEnum.THIRD_BOX.value)
            }
            fourthBox.setOnClickListener {
                clickedBox = WhichBoxEnum.FOURTH_BOX
                viewModel.getOldestPhotoFromBox(userId, WhichBoxEnum.FOURTH_BOX.value)
            }
        }
    }

    override fun setReceivers() {
        super.setReceivers()
        with(binding) {

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.countImagesInBox.collect { list ->
                    list?.let {
                        firstBoxCount.text = list[0].toString()
                        secondBoxCount.text = list[1].toString()
                        thirdBoxCount.text = list[2].toString()
                        fourthBoxCount.text = list[3].toString()
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.photoUploadMessage.collect { event ->
                    event?.getContentIfNotHandled()?.let { message ->
                        showSuccessMessage(message) {}
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.lastImageUrl.collect {
                    it?.getContentIfNotHandled()?.let { url ->

                        lastImageUrl = url

                        when (clickedBox) {
                            WhichBoxEnum.FIRST_BOX -> {
                                showConfirmPopup(url, WhichBoxEnum.FIRST_BOX, {
                                    viewModel.goImageScreen(url = url)
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.FIRST_BOX.value,
                                        WhichBoxEnum.SECOND_BOX.value
                                    )
                                }, {
                                    // nothing
                                })
                            }

                            WhichBoxEnum.SECOND_BOX -> {
                                showConfirmPopup(url, WhichBoxEnum.SECOND_BOX, {
                                    viewModel.goImageScreen(url = url)
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.SECOND_BOX.value,
                                        WhichBoxEnum.THIRD_BOX.value
                                    )
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.SECOND_BOX.value,
                                        WhichBoxEnum.FIRST_BOX.value
                                    )
                                })
                            }

                            WhichBoxEnum.THIRD_BOX -> {
                                showConfirmPopup(url, WhichBoxEnum.THIRD_BOX, {
                                    viewModel.goImageScreen(url = url)
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.THIRD_BOX.value,
                                        WhichBoxEnum.FOURTH_BOX.value
                                    )
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.THIRD_BOX.value,
                                        WhichBoxEnum.SECOND_BOX.value
                                    )
                                })
                            }

                            WhichBoxEnum.FOURTH_BOX -> {
                                showConfirmPopup(url, WhichBoxEnum.FOURTH_BOX, {
                                    viewModel.goImageScreen(url = url)
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.FOURTH_BOX.value,
                                        WhichBoxEnum.FIFTH_BOX.value
                                    )
                                }, {
                                    viewModel.movePhotoToAnotherBoxWithoutStorage(
                                        userId,
                                        url,
                                        WhichBoxEnum.FOURTH_BOX.value,
                                        WhichBoxEnum.THIRD_BOX.value
                                    )
                                })
                            }

                            else -> {}
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.boxesState.collect { state ->
                    if (state != null) {
                        boxStatuses = state
                        updateBoxBackgrounds(state)
                    }
                }
            }

        }
    }

    private fun updateBoxBackgrounds(boxStatuses: List<Boolean>) {
        val boxViews = listOf(
            binding.firstBox,
            binding.secondBox,
            binding.thirdBox,
            binding.fourthBox
        ) // Kutuları temsil eden View'ler

        boxStatuses.forEachIndexed { index, isEligible ->
            boxViews.getOrNull(index)?.let { boxView ->
                boxView.setBackgroundColor(
                    if (isEligible) Color.GREEN else Color.GRAY
                )
            }
        }
    }

    private fun showImageSourceOptions() {
        val options = arrayOf("Galeriden Seç", "Kamerayla Çek")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Fotoğraf Kaynağı Seçin")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    viewModel.checkAndRequesGalleryPermission(requestGalleryPermissionLauncher) {
                        viewModel.startGallery(galleryLauncher)
                    }
                }

                1 -> {
                    viewModel.checkAndRequesCameraPermission(requestCameraPermissionLauncher) {
                        viewModel.startCamera(cameraLauncher)
                    }
                }
            }
        }
        builder.show()
    }

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startGallery(galleryLauncher)
        } else {
            Toast.makeText(requireContext(), "Depolama izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.handleGalleryResult(it)
                clickedBox = WhichBoxEnum.NONE
                showConfirmPopup(uri, WhichBoxEnum.NONE,
                    {
                        viewModel.goImageScreen(uri)
                    },
                    {
                        viewModel.uploadImageToFirebaseStorage(
                            userId,
                            uri,
                            WhichBoxEnum.FIRST_BOX.value
                        )
                    }, {})
            }
        }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startCamera(cameraLauncher)
        } else {
            Toast.makeText(requireContext(), "Kamera izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getCurrentPhotoUri()?.let { uri ->
                clickedBox = WhichBoxEnum.NONE
                showConfirmPopup(uri, WhichBoxEnum.NONE, {
                    viewModel.goImageScreen(uri)
                }, {
                    viewModel.uploadImageToFirebaseStorage(
                        userId,
                        uri,
                        WhichBoxEnum.FIRST_BOX.value
                    )
                }, {})
            }
        } else {
            Toast.makeText(requireContext(), "Fotoğraf çekimi iptal edildi", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /*private fun listenImageResult() {
        parentFragmentManager.setFragmentResultListener("imageFragmentResult", this) { _, bundle ->
            val uriString = bundle.getString("uriString")
            uriString?.let {
                val uri = Uri.parse(it)
                binding.lastImageView.setImageURI(uri)
                Log.e("URI", uri.toString())

                when (clickedBox) {
                    WhichBoxEnum.FIRST_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.FIRST_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FIRST_BOX.value,
                                WhichBoxEnum.SECOND_BOX.value
                            )
                        }, {
                            // nothing
                        })
                    }

                    WhichBoxEnum.SECOND_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.SECOND_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.SECOND_BOX.value,
                                WhichBoxEnum.THIRD_BOX.value
                            )
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.SECOND_BOX.value,
                                WhichBoxEnum.FIRST_BOX.value
                            )
                        })
                    }

                    WhichBoxEnum.THIRD_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.THIRD_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.THIRD_BOX.value,
                                WhichBoxEnum.FOURTH_BOX.value
                            )
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.THIRD_BOX.value,
                                WhichBoxEnum.SECOND_BOX.value
                            )
                        })
                    }

                    WhichBoxEnum.FOURTH_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.FOURTH_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FOURTH_BOX.value,
                                WhichBoxEnum.FIFTH_BOX.value
                            )
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FOURTH_BOX.value,
                                WhichBoxEnum.THIRD_BOX.value
                            )
                        })
                    }

                    else -> {}
                }
            }
        }
    }*/

    private fun listenImageResult() {
        parentFragmentManager.setFragmentResultListener("imageFragmentResult", this) { _, bundle ->
            val bitmap = BundleCompat.getParcelable(bundle, "bitmap", Bitmap::class.java)
            bitmap?.let {
                val uri = createTempFileFromBitmap(it)
                when (clickedBox) {
                    WhichBoxEnum.NONE -> {
                        showConfirmPopup(uri, WhichBoxEnum.NONE, {
                            viewModel.goImageScreen(uri)
                        }, {
                            viewModel.uploadImageToFirebaseStorage(
                                userId,
                                uri,
                                WhichBoxEnum.FIRST_BOX.value
                            )
                        }, {})
                    }
                    WhichBoxEnum.FIRST_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.FIRST_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FIRST_BOX.value,
                                WhichBoxEnum.SECOND_BOX.value
                            )
                        }, {
                            // nothing
                        })
                    }

                    WhichBoxEnum.SECOND_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.SECOND_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.SECOND_BOX.value,
                                WhichBoxEnum.THIRD_BOX.value
                            )
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.SECOND_BOX.value,
                                WhichBoxEnum.FIRST_BOX.value
                            )
                        })
                    }

                    WhichBoxEnum.THIRD_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.THIRD_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.THIRD_BOX.value,
                                WhichBoxEnum.FOURTH_BOX.value
                            )
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.THIRD_BOX.value,
                                WhichBoxEnum.SECOND_BOX.value
                            )
                        })
                    }

                    WhichBoxEnum.FOURTH_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.FOURTH_BOX, {
                            viewModel.goImageScreen(uri = uri)
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FOURTH_BOX.value,
                                WhichBoxEnum.FIFTH_BOX.value
                            )
                        }, {
                            viewModel.movePhotoToAnotherBoxWithStorage(
                                userId,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FOURTH_BOX.value,
                                WhichBoxEnum.THIRD_BOX.value
                            )
                        })
                    }

                    else -> {}
                }
            }
        }
    }


    private fun createTempFileFromBitmap(bitmap: Bitmap): Uri {
        val file = File(context?.cacheDir, "temp_image_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }
        return Uri.fromFile(file)
    }


}
