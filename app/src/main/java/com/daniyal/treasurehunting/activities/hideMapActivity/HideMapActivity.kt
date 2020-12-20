package com.daniyal.treasurehunting.activities.hideMapActivity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.admin.assesmenttest.helpers.PermissionHelper
import com.daniyal.treasurehunting.R
import com.daniyal.treasurehunting.databinding.ActivityHideMapBinding
import com.daniyal.treasurehunting.fragments.HideMapFragment
import com.google.android.material.snackbar.Snackbar

lateinit var mainBinding: ActivityHideMapBinding

class HideMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_hide_map)

        if (PermissionHelper.hasPermissions(
                this,
                PermissionHelper.PermissionsType.LOCATION_PERMISSION
            )
        ) {
            supportFragmentManager.beginTransaction().add(R.id.clMain, HideMapFragment(), "map_frag")
                .commit()
        } 
        else {
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
                    .add(R.id.clMain, HideMapFragment(), "map_frag")
                    .commit()
            else {
                //if permissions are not granted show message and request permission again
                val snackBar =
                    Snackbar.make(
                        mainBinding.root,
                        "Need Permissions to get current location",
                        Snackbar.LENGTH_INDEFINITE
                    )
                snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
                snackBar.setAction("Retry") {
                    PermissionHelper.requestPermissions(
                        this,
                        PermissionHelper.PermissionsType.LOCATION_PERMISSION
                    )
                }
                snackBar.show()
            }
        }
    }
}