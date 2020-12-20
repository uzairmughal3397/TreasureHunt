package com.daniyal.treasurehunting.activities.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daniyal.treasurehunting.R
import com.daniyal.treasurehunting.activities.hideMapActivity.HideMapActivity
import com.daniyal.treasurehunting.activities.searchMapActivity.SearchMapActivity
import com.daniyal.treasurehunting.activities.settingsActivity.SettingsActivity
import com.daniyal.treasurehunting.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)


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