package com.asgribovskaya.smallscanner.ui.fragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asgribovskaya.smallscanner.R
import com.asgribovskaya.smallscanner.databinding.FragmentPhotoBinding
import com.asgribovskaya.smallscanner.ui.viewmodels.PhotoViewModel
import com.asgribovskaya.smallscanner.ui.utils.PictureUtils
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date


class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding
        get() = _binding!!

    private val pickPhoto =
        registerForActivityResult(PickVisualMedia()) { uri: Uri? ->
            viewModel.detectInBitmap(getScaledBitmapFromUri(uri!!))
        }

    private val takePhoto =
        registerForActivityResult(TakePicture()) { photoTaken: Boolean ->
            if (photoTaken) viewModel.detectInBitmap(getScaledBitmapFromUri(photoUri))
        }

    private lateinit var photoUri: Uri

    private val viewModel: PhotoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAssets()
        setupListeners()
        setupCollectors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.apply {
            btnPhotoGallery.setOnClickListener {
                pickPhoto.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            btnPhotoCamera.setOnClickListener {
                takePhoto.launch(getUriOfAddedItem())
            }
        }
    }

    private fun getUriOfAddedItem(): Uri {
        val resolver = requireContext().applicationContext.contentResolver
        val imagesCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val photoName = "img_SmallScanner_${DateFormat.getDateTimeInstance().format(Date())}.jpg"
        val newPhoto = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, photoName)
        }
        photoUri = resolver.insert(imagesCollection, newPhoto)!!
        return photoUri
    }

    private fun setupCollectors() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.photo.collect { photoData ->
                photoData.detections?.let {
                    val color = resources.getColor(R.color.red, null)
                    binding.ivPhotoProducts.setImageBitmap(
                        PictureUtils.drawBoundingBoxes(
                            photoData,
                            color
                        )
                    )
                }
            }
        }
    }

    private fun setupAssets() {
        viewModel.setAssetManager(requireContext().applicationContext.assets)
    }

    private fun getScaledBitmapFromUri(uri: Uri): Bitmap {
        val resolver = requireContext().applicationContext.contentResolver
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(resolver, uri)
        }
        return PictureUtils.scaleBitmap(bitmap)
    }
}