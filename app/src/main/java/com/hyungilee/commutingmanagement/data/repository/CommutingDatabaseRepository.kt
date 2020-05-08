package com.hyungilee.commutingmanagement.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDao
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CommutingDatabaseRepository(private val db: CommutingManagementDatabase) {
    private var commutingDatabaseDao: CommutingManagementDao = db.commutingManagementDao()
    private var allCommutingData: LiveData<List<CommutingData>>
    private var commutingDataModels: ArrayList<CommutingData> = arrayListOf()

    init {
        allCommutingData = commutingDatabaseDao.getAllCommutingData()
    }

    fun saveCommutingData(commutingData: CommutingData) = runBlocking {
        this.launch(Dispatchers.IO) {
            commutingDatabaseDao.saveCommutingData(commutingData)
        }
    }

    fun getAllCommutingData(): LiveData<List<CommutingData>> {
        return allCommutingData
    }

    private fun loadSampleData(){
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val query = reference.child("");
        query.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                commutingDataModels.add(snapshot.getValue(CommutingData::class.java)!!)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

}