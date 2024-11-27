package com.example.classcash.viewmodels.treasurer

import android.net.Uri
import androidx.lifecycle.*
import com.example.classcash.viewmodels.TopScreenViewModel

class ProfileRepository(
    private val authViewModel: AuthViewModel,
    private val topScreenViewModel: TopScreenViewModel
) {
    private val _treasurerName = MutableLiveData<String>()
    val treasurerName: LiveData<String> get() = _treasurerName

    private val _classroomName = MutableLiveData<String>()
    val classroomName: LiveData<String> get() = _classroomName

    private val _profileImage = MutableLiveData<Uri?>()
    val profileImage: LiveData<Uri?> get() = _profileImage

    // Fetch and store the treasurer name
    fun fetchTreasurerName() {
        _treasurerName.value = authViewModel.getTreasurerName()
    }

    // Fetch and store the classroom name
    fun fetchClassroomName() {
        _classroomName.value = topScreenViewModel.getClassroomName()
    }

    // Update the profile image
    fun updateProfileImage(imageUri: Uri?) {
        _profileImage.value = imageUri
    }
}
