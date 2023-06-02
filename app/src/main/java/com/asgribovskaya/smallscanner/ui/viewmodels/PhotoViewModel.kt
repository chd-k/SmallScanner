package com.asgribovskaya.smallscanner.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.asgribovskaya.smallscanner.data.model.PhotoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PhotoViewModel : ViewModel() {

    private val _photo = MutableStateFlow(PhotoData())
    val photo: StateFlow<PhotoData> = _photo

    fun updateUri(uri: Uri) {
        _photo.value = PhotoData(uri)
    }
}