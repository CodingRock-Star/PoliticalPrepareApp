package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    val voterInfo = electionsRepository.voterInfo

    var Url = MutableLiveData<String>()

    private val electionId = MutableLiveData<Int>()

    val election=getElectionWithId()

    fun getElectionWithId():LiveData<Election>{
        viewModelScope.launch {
            electionId.value?.toInt()?.let { electionsRepository.getElection(it) }
        }
     return electionsRepository.electionWithId
    }


    fun getElection(id: Int) {
        electionId.value = id
    }


    fun SaveElection(election: Election) {
        election.isSaved = !election.isSaved
        viewModelScope.launch {
            electionsRepository.insertElectionToDataBase(election)
        }
    }

    fun getVoterInfo(electionId: Int, address: String) =
            viewModelScope.launch {
                electionsRepository.getVoterInfo(electionId, address)
            }


    fun intentUrl(url: String) {
        Url.value = url
    }

}