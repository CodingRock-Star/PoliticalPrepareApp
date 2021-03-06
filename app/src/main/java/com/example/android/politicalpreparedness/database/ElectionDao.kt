package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend   fun insertElections(elections: List<Election>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend  fun insertElection(election: Election)

    @Query("SELECT *From election_table")
  suspend  fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT *From election_table where isSaved =1")
   suspend fun getSavedElection(): LiveData<List<Election>>


    @Query("SELECT *FROM election_table where id=:id")
   suspend fun getElectionById(id: Int): LiveData<Election>

    @Query("DELETE FROM election_table where id =:id")
  suspend  fun deleteElection(id: Int)


}