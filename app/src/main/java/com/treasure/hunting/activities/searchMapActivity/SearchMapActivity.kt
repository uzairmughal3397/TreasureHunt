package com.treasure.hunting.activities.searchMapActivity

import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.admin.assesmenttest.helpers.PermissionHelper
import com.treasure.hunting.R
import com.treasure.hunting.fragments.SearchMapsFragment
import com.treasure.hunting.helpers.AppConstants
import com.treasure.hunting.helpers.SharedPrefHelper
import java.util.*

class SearchMapActivity : AppCompatActivity() {
    var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_map)
        /* this initialization takes some time therefore initialing before the usages */
        tts = TextToSpeech(this) {}

        /*Checking for required permissions*/
        if (PermissionHelper.hasPermissions(
                this,
                PermissionHelper.PermissionsType.LOCATION_PERMISSION
            )
        ) {
            supportFragmentManager.beginTransaction()
                .add(R.id.clMain, SearchMapsFragment(), "search_map_frag")
                .commit()
        } else {
            PermissionHelper.requestPermissions(
                this,
                PermissionHelper.PermissionsType.LOCATION_PERMISSION
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //check if permissions are granted or not
        grantResults.forEach {
            if (requestCode == PermissionHelper.LOCATION_PERMISSION_REQUEST_CODE && it == PackageManager.PERMISSION_GRANTED)
                supportFragmentManager.beginTransaction()
                    .add(R.id.clMain, SearchMapsFragment(), "search_map_frag")
                    .commit()
            else {
                //if permissions are not granted show message and request permission again
                showMessage("Need Permissions to get current location")
            }
        }
    }

    private fun showMessage(msg: String) {
        /*If Voice narration is activated or not.*/
        if (SharedPrefHelper().readBoolean(this, AppConstants.VOICE_KEY)) {
            tts!!.language = Locale.UK
            tts!!.speak(msg, TextToSpeech.QUEUE_ADD, null)
        } else
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}