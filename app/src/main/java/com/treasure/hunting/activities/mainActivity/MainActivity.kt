package com.treasure.hunting.activities.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.treasure.hunting.R
import com.treasure.hunting.activities.hideMapActivity.HideMapActivity
import com.treasure.hunting.activities.searchMapActivity.SearchMapActivity
import com.treasure.hunting.activities.settingsActivity.SettingsActivity
import com.treasure.hunting.databinding.ActivityMainBinding
import com.treasure.hunting.helpers.AppConstants
import com.treasure.hunting.helpers.SharedPrefHelper

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)

        val radius= SharedPrefHelper().readString(this,AppConstants.RADIUS_KEY)

        if (radius.isNullOrEmpty()){
            SharedPrefHelper().writeString(this,AppConstants.RADIUS_KEY,"50")
        }


        binding.btnHide.setOnClickListener {
            startActivity(Intent(this, HideMapActivity::class.java))
        }
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchMapActivity::class.java))
        }
        binding.ivSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}