package com.merteroglu286.leitnerbox.presentation.fragment.image

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.merteroglu286.leitnerbox.databinding.FragmentImageBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import com.merteroglu286.leitnerbox.utility.extension.getBitmap
import com.merteroglu286.leitnerbox.utility.extension.setBackgroundFromUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ImageFragment  : BaseFragment<FragmentImageBinding, ImageVM>() {

    private val args: ImageFragmentArgs by navArgs()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentImageBinding {
        return FragmentImageBinding.inflate(layoutInflater, container, false)
    }

    override fun initUI() {
        super.initUI()

        if (args.uriString != "null"){
            binding.drawingView.setImageUri(uri = args.uriString.toUri())
        }else{
            binding.drawingView.setBackgroundFromUrl(args.urlString)
        }
    }

    override fun setListeners() {
        super.setListeners()

        with(binding) {
            // Renk seçiciler
            blackColor.setOnClickListener {
                drawingView.setPen()  // Kalem moduna geç
                drawingView.setPenColor(Color.BLACK)
            }
            redColor.setOnClickListener {
                drawingView.setPen()
                drawingView.setPenColor(Color.RED)
            }
            blueColor.setOnClickListener {
                drawingView.setPen()
                drawingView.setPenColor(Color.BLUE)
            }
            greenColor.setOnClickListener {
                drawingView.setPen()
                drawingView.setPenColor(Color.GREEN)
            }
            yellowColor.setOnClickListener {
                drawingView.setPen()
                drawingView.setPenColor(Color.YELLOW)
            }

            // Kontrol butonları
            undoButton.setOnClickListener { drawingView.undo() }
            redoButton.setOnClickListener { drawingView.redo() }
            eraserButton.setOnClickListener { drawingView.setEraser() }
            penButton.setOnClickListener { drawingView.setPen() }

            // Kalınlık kontrolü
            binding.strokeWidthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    drawingView.setStrokeWidth(progress.toFloat())
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            /*saveImageButton.setOnClickListener {
                val uri = drawingView.saveDrawingToUri()
                if (uri != null) {

                    val result = Bundle().apply {
                        putString("uriString", uri.toString())
                    }
                    parentFragmentManager.setFragmentResult("imageFragmentResult", result)
                    viewModel.navigateBack()
                } else {
                    Toast.makeText(requireContext(), "Failed to save the drawing", Toast.LENGTH_LONG).show()
                }
            }*/

            binding.saveImageButton.setOnClickListener {

                val bitmap = drawingView.getBitmap()
                val result = Bundle().apply {
                    putParcelable("bitmap", bitmap)
                }
                parentFragmentManager.setFragmentResult("imageFragmentResult", result)
                viewModel.navigateBack()
            }


        }
    }




}