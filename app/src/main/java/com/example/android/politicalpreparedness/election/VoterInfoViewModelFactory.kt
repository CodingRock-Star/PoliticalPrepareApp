package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

//Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if(modelClass.isAssignableFrom(VoterInfoViewModel::class.java)){
          return VoterInfoViewModel(application) as T
      }
        throw IllegalArgumentException("Unable to construct View Model")
    }

}