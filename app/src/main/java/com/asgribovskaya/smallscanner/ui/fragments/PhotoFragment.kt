package com.asgribovskaya.smallscanner.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asgribovskaya.smallscanner.databinding.FragmentPhotoBinding
import com.asgribovskaya.smallscanner.ui.viewmodels.PhotoViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat.getDateTimeInstance
import java.util.Date


class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding
        get() = _binding!!

    private val pickPhoto =
        registerForActivityResult(PickVisualMedia()) { uri: Uri? ->
            viewModel.updateUri(uri!!)
        }

    private val takePhoto = registerForActivityResult(TakePicture()) { photoTaken: Boolean ->
        if (photoTaken && takenPhotoUri != null) {
            viewModel.updateUri(takenPhotoUri!!)
        }
    }

    private var takenPhotoUri: Uri? = null

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
        setupListeners()
        setUpCollectors()
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
                takePhoto.launch(getUri())
//                TODO: VM uri
            }
        }
    }

    private fun getUri(): Uri {
        val photoName = "IMG_SMALLSCANNER_${getDateTimeInstance().format(Date())}.jpg"
        val photoFile = File(requireContext().applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), photoName)
        takenPhotoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.asgribovskaya.smallscanner.fileprovider",
            photoFile
        )
        return takenPhotoUri!!
    }

    private fun setUpCollectors() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.photo.collect { photoData ->
                binding.ivPhotoProducts.setImageURI(photoData.uri)
            }
        }
    }
}