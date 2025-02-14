/*
package com.merteroglu286.leitnerbox.presentation.fragment.home

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.merteroglu286.leitnerbox.R
import com.merteroglu286.leitnerbox.databinding.FragmentHomeBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import com.merteroglu286.leitnerbox.utility.enums.WhichBoxEnum
import com.merteroglu286.leitnerbox.utility.extension.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeVM>() {

    private var clickedBox = WhichBoxEnum.NONE
    private var lastImageUrl = ""


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
                showConfirmPopup(uri, WhichBoxEnum.NONE,
                    {
                        clickedBox = WhichBoxEnum.NONE
                        viewModel.goImageScreen(uri)
                    },
                    {
                        getUserId()?.let { userId ->
                            viewModel.uploadImageToFirebaseStorage(
                                userId,
                                uri,
                                WhichBoxEnum.FIRST_BOX.value
                            )
                        }
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
                showConfirmPopup(uri, WhichBoxEnum.NONE, {
                    clickedBox = WhichBoxEnum.NONE
                    viewModel.goImageScreen(uri)
                }, {
                    getUserId()?.let { userId ->
                        viewModel.uploadImageToFirebaseStorage(
                            userId,
                            uri,
                            WhichBoxEnum.FIRST_BOX.value
                        )
                    }
                }, {})
            }
        } else {
            Toast.makeText(requireContext(), "Fotoğraf çekimi iptal edildi", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater, container, false)
    }

    override fun initUI() {
        super.initUI()

        Log.d("clickedBox", clickedBox.value.toString())
        listenImageResult()
    }

    override fun runOnce() {
        super.runOnce()
        getUserId()?.let {
            viewModel.getEligibleOldestPhotoForSpecificDays(it, WhichBoxEnum.FIRST_BOX.value)
            viewModel.getEligibleOldestPhotoForSpecificDays(it, WhichBoxEnum.SECOND_BOX.value)
            viewModel.getEligibleOldestPhotoForSpecificDays(it, WhichBoxEnum.THIRD_BOX.value)
            viewModel.getEligibleOldestPhotoForSpecificDays(it, WhichBoxEnum.FOURTH_BOX.value)
            viewModel.getUser(it)
            viewModel.getCountImagesInBox(it)
        }
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

        }
    }

    override fun setReceivers() {
        super.setReceivers()
        with(binding) {

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.currentPhotoUri.collect { uri ->
                    if (uri != null) {
                        showSuccessMessage(uri.toString(), {})
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
                viewModel.lastImageUrl.collect { imageUrl ->
                    if (imageUrl != null) {
                        lastImageUrl = imageUrl
                        lastImageView.loadImage(imageUrl)
                    }
                }
            }

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
                viewModel.imageInFirstBox.collect { event ->
                    event?.getContentIfNotHandled()?.let { imageUrl ->
                        firstBox.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_green
                            )
                        )
                        firstBox.setOnClickListener {
                            showConfirmPopup(imageUrl, WhichBoxEnum.FIRST_BOX, {
                                clickedBox = WhichBoxEnum.FIRST_BOX
                                viewModel.goImageScreen(url = imageUrl)
                            }, {
                                getUserId()?.let {
                                    viewModel.movePhotoToAnotherBox(
                                        it,
                                        imageUrl,
                                        WhichBoxEnum.FIRST_BOX.value,
                                        WhichBoxEnum.SECOND_BOX.value
                                    )
                                }
                            }, {})
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.imageInSecondBox.collect { event ->
                    event?.getContentIfNotHandled()?.let { imageUrl ->
                        secondBox.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_green
                            )
                        )
                        secondBox.setOnClickListener {
                            showConfirmPopup(imageUrl, WhichBoxEnum.SECOND_BOX, {
                                clickedBox = WhichBoxEnum.SECOND_BOX
                                viewModel.goImageScreen(url = imageUrl)
                            }, {
                                getUserId()?.let {
                                    viewModel.movePhotoToAnotherBox(
                                        it,
                                        imageUrl,
                                        WhichBoxEnum.SECOND_BOX.value,
                                        WhichBoxEnum.THIRD_BOX.value
                                    )
                                }
                            }, {})
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.imageInThirdBox.collect { event ->
                    event?.getContentIfNotHandled()?.let { imageUrl ->
                        thirdBox.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_green
                            )
                        )
                        thirdBox.setOnClickListener {
                            showConfirmPopup(imageUrl, WhichBoxEnum.THIRD_BOX, {
                                clickedBox = WhichBoxEnum.THIRD_BOX
                                viewModel.goImageScreen(url = imageUrl)
                            }, {
                                getUserId()?.let {
                                    viewModel.movePhotoToAnotherBox(
                                        it,
                                        imageUrl,
                                        WhichBoxEnum.THIRD_BOX.value,
                                        WhichBoxEnum.FOURTH_BOX.value
                                    )
                                }
                            }, {})
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.imageInFourthBox.collect { event ->
                    event?.getContentIfNotHandled()?.let { imageUrl ->
                        fourthBox.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_green
                            )
                        )

                        fourthBox.setOnClickListener {
                            showConfirmPopup(imageUrl, WhichBoxEnum.FOURTH_BOX, {
                                clickedBox = WhichBoxEnum.FOURTH_BOX
                                viewModel.goImageScreen(url = imageUrl)
                            }, {
                                showSuccessMessage("Tamamlanan sorulara taşınacak.", {})
                            }, {})
                        }
                    }
                }
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

    private fun listenImageResult() {
        parentFragmentManager.setFragmentResultListener("imageFragmentResult", this) { _, bundle ->
            val uriString = bundle.getString("uriString")
            uriString?.let {
                val uri = Uri.parse(it)
                // Gelen URI'yi işleyin, örneğin bir ImageView'de gösterin

                when (clickedBox) {
                    WhichBoxEnum.NONE -> {
                        getUserId()?.let { userId ->
                            viewModel.uploadImageToFirebaseStorage(
                                userId,
                                uri,
                                WhichBoxEnum.FIRST_BOX.value
                            )
                        }
                    }

                    WhichBoxEnum.FIRST_BOX -> {
                        Log.d("mertLog",lastImageUrl)
                        showConfirmPopup(uri, WhichBoxEnum.FIRST_BOX, {
                            viewModel.goImageScreen(uri)
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FIRST_BOX.value,
                                WhichBoxEnum.SECOND_BOX.value
                            )
                        }, {}
                        )
                    }

                    WhichBoxEnum.SECOND_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.SECOND_BOX, {
                            viewModel.goImageScreen(uri)
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.SECOND_BOX.value,
                                WhichBoxEnum.THIRD_BOX.value
                            )
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.SECOND_BOX.value,
                                WhichBoxEnum.FIRST_BOX.value
                            )
                        }
                        )
                    }

                    WhichBoxEnum.THIRD_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.THIRD_BOX, {
                            viewModel.goImageScreen(uri)
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.THIRD_BOX.value,
                                WhichBoxEnum.FOURTH_BOX.value
                            )
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.THIRD_BOX.value,
                                WhichBoxEnum.SECOND_BOX.value
                            )
                        }
                        )
                    }

                    WhichBoxEnum.FOURTH_BOX -> {
                        showConfirmPopup(uri, WhichBoxEnum.FOURTH_BOX, {
                            viewModel.goImageScreen(uri)
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FOURTH_BOX.value,
                                WhichBoxEnum.FIFTH_BOX.value
                            )
                        }, {
                            viewModel.updateImage(
                                getUserId()!!,
                                uri,
                                lastImageUrl,
                                WhichBoxEnum.FOURTH_BOX.value,
                                WhichBoxEnum.THIRD_BOX.value
                            )
                        }
                        )
                    }

                    WhichBoxEnum.FIFTH_BOX -> {
                        //viewModel.updateImage(getUserId()!!, uri, lastImageUrl, WhichBoxEnum.FIFTH_BOX.value)
                    }
                }
            }
        }
    }
}*/

