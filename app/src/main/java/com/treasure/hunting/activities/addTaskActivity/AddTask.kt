package com.treasure.hunting.activities.addTaskActivity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.admin.assesmenttest.helpers.PermissionHelper
import com.treasure.hunting.R
import com.treasure.hunting.activities.addTaskActivity.adapter.AddTaskViewPagerAdapter
import com.treasure.hunting.activities.hideMapActivity.mainBinding
import com.treasure.hunting.databinding.ActivityAddTaskBinding
import com.treasure.hunting.fragments.QRCodeFragment
import com.treasure.hunting.fragments.listDropDownFrag.ListDropDownFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class AddTask : AppCompatActivity() {
    lateinit var binding: ActivityAddTaskBinding
    var lat=""
    var lng=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task)

        lat=intent.getStringExtra("lat").toString()
        lng=intent.getStringExtra("lng").toString()


        if (PermissionHelper.hasPermissions(
                this,
                PermissionHelper.PermissionsType.CAMERA_PERMISSION
            )
        ) {
            setUpViewPager()
        } else {
            PermissionHelper.requestPermissions(
                this,
                PermissionHelper.PermissionsType.CAMERA_PERMISSION
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
            if (requestCode == PermissionHelper.CAMERA_PERMISSION_REQUEST_CODE && it == PackageManager.PERMISSION_GRANTED)
                setUpViewPager()
            else {
                //if permissions are not granted show message and request permission again
                val snackBar =
                    Snackbar.make(
                        mainBinding.root,
                        "Need Permissions to capture QR Code",
                        Snackbar.LENGTH_INDEFINITE
                    )
                snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
                snackBar.setAction("Retry") {
                    PermissionHelper.requestPermissions(
                        this,
                        PermissionHelper.PermissionsType.CAMERA_PERMISSION
                    )
                }
                snackBar.show()
            }
        }
    }

    private fun setUpViewPager() {
        val viewPagerAdapter = AddTaskViewPagerAdapter(this)

        val qrCodeFragment=QRCodeFragment()
        qrCodeFragment.arguments= bundleOf(Pair("lat",lat),Pair("lng", lng))

        val listDropDownFragment=ListDropDownFragment()
        listDropDownFragment.arguments= bundleOf(Pair("lat",lat),Pair("lng", lng))

        viewPagerAdapter.addFragment(qrCodeFragment, "QR Code")
        viewPagerAdapter.addFragment(listDropDownFragment, "List/DropDown")
        binding.viewpager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
        }.attach()
    }
}