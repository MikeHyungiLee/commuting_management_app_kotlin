package com.hyungilee.commutingmanagement.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private var cntNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.getAllCommutingData().observe(this, Observer {
            if(cntNum == 0) {
                result_txt.text = "データアップデート準備"
                cntNum++
            }else{
                result_txt.text = "完了"
            }
        })

        data_add_btn.setOnClickListener {
            val commutingData = CommutingData(0L, "Mike", "5/5", "出勤", "07:00", "08:00")
            mainViewModel.saveCommutingData(commutingData)
        }
    }
}
