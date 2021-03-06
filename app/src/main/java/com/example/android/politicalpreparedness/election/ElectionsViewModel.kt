package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch


class ElectionsViewModel(private val application :Application):ViewModel() {
    private val database = getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    fun getUpComingElection():LiveData<List<Election>> {
        viewModelScope.launch {
            electionsRepository.getAllElectionList()
        }
       return electionsRepository.election
    }
    fun getSavedElectionList():LiveData<List<Election>>{
        viewModelScope.launch {
            electionsRepository.getSavedElectionData()
        }
        return electionsRepository.savedElection
    }

    init {
        viewModelScope.launch { electionsRepository.refreshElectionDataAndSaveToDataBase() }
    }

}