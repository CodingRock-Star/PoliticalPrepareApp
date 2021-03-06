package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.ApiResult
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class ElectionsRepository(private val database: ElectionDatabase) {

   private val _electionWithId =MutableLiveData<Election>()
   val electionWithId:LiveData<Election>
    get() = _electionWithId

    private val _savedElection =MutableLiveData<List<Election>>()
    val savedElection:LiveData<List<Election>>
    get() = _savedElection

    private val _election =MutableLiveData<List<Election>>()
    val election:LiveData<List<Election>>
    get() = _election

    suspend fun getSavedElectionData(){
        _savedElection.value= database.electionDao.getSavedElection().value
    }

    suspend fun getAllElectionList(){
        _election.value= database.electionDao.getAllElections().value
    }

    private val _voterInfo =MutableLiveData<VoterInfoResponse>()
    val voterInfo :LiveData<VoterInfoResponse>
    get() = _voterInfo

    private val _serverErrorResponse = MutableLiveData<String>()
    val errorResponse: LiveData<String>
        get() = _serverErrorResponse

    companion object {
        private var TAG: String = ElectionsRepository::class.java.simpleName
    }

    suspend fun getVoterInfo(electionId: Int, address: String) {
        try {
            withContext(Dispatchers.IO) {
                val voterinfoResponse = CivicsApi.retrofitService.getVoterInfo(electionId, address)
                if (voterinfoResponse.isSuccessful && voterinfoResponse.body() != null) {
                    _voterInfo.postValue(voterinfoResponse.body())
                } else {
                    if (voterinfoResponse.code() in (300..503)) {
                        _serverErrorResponse.value = ApiResult.Retry(voterinfoResponse.message()).error
                    }
                    _serverErrorResponse.value = ApiResult.Error(voterinfoResponse.message()).error
                }
            }
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    suspend fun insertElectionToDataBase(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insertElection(election)
        }
    }

    suspend fun refreshElectionDataAndSaveToDataBase() {
        try {
            withContext(Dispatchers.IO) {
                val electionResponse = CivicsApi.retrofitService.getElection()
                withContext(Dispatchers.IO) {
                    saveToDataBase(electionResponse)
                }
            }
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    private fun handleException(ex: Exception) {
        when (ex) {
            is IOException -> {
                _serverErrorResponse.value = ApiResult.Error("Network Error").error
            }
            else -> {
                _serverErrorResponse.value = ApiResult.Error("UnKnown Error Exception").error
            }
        }

    }

    private suspend fun saveToDataBase(electionResponse: Response<ElectionResponse>) {
        if (electionResponse.isSuccessful && electionResponse.body() != null) {
            database.electionDao.insertElections(electionResponse.body()!!.elections)
        } else {
            if (electionResponse.code() in (300..503)) {
                _serverErrorResponse.value = ApiResult.Retry(electionResponse.message()).error
            }
            _serverErrorResponse.value = ApiResult.Error(electionResponse.message()).error
        }
    }

    suspend fun getElection(id: Int) {
        _electionWithId.value=database.electionDao.getElectionById(id).value
    }
}