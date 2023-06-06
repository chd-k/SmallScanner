package com.asgribovskaya.smallscanner.ui.viewmodels

import android.content.res.AssetManager
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asgribovskaya.smallscanner.data.model.PhotoData
import com.asgribovskaya.smallscanner.data.repositories.DetectionModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val _photo = MutableStateFlow(PhotoData())
    val photo: StateFlow<PhotoData> = _photo

    fun detectInBitmap(bitmap: Bitmap) = viewModelScope.launch {
        _photo.value = PhotoData(bitmap, DetectionModel.runObjectDetection(bitmap))
    }

    fun setAssetManager(assetManager: AssetManager) {
        DetectionModel.setAssetManager(assetManager)
    }
}