package com.example.classcash.viewmodels.treasurer

import android.net.Uri
import androidx.lifecycle.*

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    val treasurerName: LiveData<String> = repository.treasurerName
    val classroomName: LiveData<String> = repository.classroomName
    val profileImage: LiveData<Uri?> = repository.profileImage

    // Wrapper methods for repository functions
    fun displayTreasurerName() {
        repository.fetchTreasurerName()
    }

    fun displayClassName() {
        repository.fetchClassroomName()
    }

    fun addImage(imageUri: Uri?) {
        repository.updateProfileImage(imageUri)
    }
}
