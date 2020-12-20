package com.admin.assesmenttest.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionHelper {
    private var preferences: SharedPreferences? = null

    //request code for all permissions
    const val ALL_PERMISSION_REQUEST_CODE = 400

    //request code for specific permissions
    const val STORAGE_PERMISSION_REQUEST_CODE = 401
    const val PHONE_PERMISSION_REQUEST_CODE = 403
    const val CAMERA_PERMISSION_REQUEST_CODE = 404
    const val LOCATION_PERMISSION_REQUEST_CODE = 410
    const val SMS_PERMISSION_REQUEST_CODE = 420

    //
    private const val PERMISSION_DENIED_PERMANENTLY = "permission_denied_permanently"

    //add all permissions required by app
//    private var allPermissionsArr = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.CAMERA,
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS)

    //specific Type permissions
    private val storagePermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    private val phonePermissions = arrayOf(Manifest.permission.CALL_PHONE)
    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
//    private val smsPermission = arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS)

    //permission descriptions
//    private val allPermissionsDesc = "Please allow these permissions.\n" +
//            "\n1. Camera permission is required to take picture." +
//            "\n2. Storage permission is required so we can store you pics on you storage."+
//            "\n3. SMS permission is required so we can communicate when no internet connection."
    private const val storagePermissionsDesc = "\'Storage\' permissions are required to store you pictures in your phone storage"
//    private const val phonePermissionsDesc = "\'Phone\' permissions are required for this feature to work properly."
    private const val cameraPermissionsDesc = "\'Camera\' permissions are required for this feature to work properly."
    private const val locationPermissionsDesc =
        "\'Location\' permissions is required to get your current location for better journey experience"
//    private const val smsPermissionsDesc = "\'SMS\' permissions is required to send sms when you are offline"

    //
    private fun getPreferences(mActivity: Activity): SharedPreferences? {
        if (preferences == null) {
            preferences = mActivity.getSharedPreferences("Permissions", Context.MODE_PRIVATE)
        }
        return preferences
    }

    fun setPermissionsDeniedPermanently(mActivity: Activity, denied: Boolean) {
        getPreferences(
            mActivity
        )!!.edit().putBoolean(PERMISSION_DENIED_PERMANENTLY, denied).apply()
    }

    fun getPermissionsDeniedPermanently(mActivity: Activity): Boolean {
        return getPreferences(
            mActivity
        )!!.getBoolean(PERMISSION_DENIED_PERMANENTLY, false)
    }

    //
    private fun getPermissionsByType(permissionsType: PermissionsType): Array<String> {
        return when (permissionsType) {
            PermissionsType.LOCATION_PERMISSION -> locationPermissions
            PermissionsType.CAMERA_PERMISSION -> cameraPermissions
            PermissionsType.STORAGE_PERMISSION -> storagePermissions
        }
    }

    private fun getPermissionsRCByType(permissionsType: PermissionsType): Int {
        return when (permissionsType) {
            PermissionsType.LOCATION_PERMISSION -> LOCATION_PERMISSION_REQUEST_CODE
            PermissionsType.CAMERA_PERMISSION -> CAMERA_PERMISSION_REQUEST_CODE
            PermissionsType.STORAGE_PERMISSION -> STORAGE_PERMISSION_REQUEST_CODE
        }
    }

    private fun getPermissionsTextByType(permissionsType: PermissionsType): String {
        return when (permissionsType) {
            PermissionsType.LOCATION_PERMISSION -> locationPermissionsDesc
            PermissionsType.CAMERA_PERMISSION -> cameraPermissionsDesc
            PermissionsType.STORAGE_PERMISSION -> storagePermissionsDesc
        }
    }


    //check permissions by type
    fun hasPermissions(context: Context?, permissionsType: PermissionsType): Boolean {
        if (context == null) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions =
                getPermissionsByType(
                    permissionsType
                )
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    //check rational should be shown for specific permission type


    //request all permissions required by app
//    fun requestPermissions(mActivity: Activity?) {
//        if (mActivity == null) {
//            return
//        }
//        if (allPermissionsArr.isEmpty()) {
//            return
//        }
//        //
//        ActivityCompat.requestPermissions(
//            mActivity,
//            allPermissionsArr,
//            ALL_PERMISSION_REQUEST_CODE
//        )
//    }

//    fun requestPermissions(fragment: Fragment?) {
//        if (fragment == null) {
//            return
//        }
//        if (allPermissionsArr.isEmpty()) {
//            return
//        }
//        //
//        fragment.requestPermissions(
//            allPermissionsArr,
//            ALL_PERMISSION_REQUEST_CODE
//        )
//    }

    //request specific permissions
    fun requestPermissions(mActivity: Activity?, permissions: Array<String>, requestCode: Int) {
        if (mActivity == null) {
            return
        }
        //
        if (permissions.isEmpty()) {
            return
        }
        //
        ActivityCompat.requestPermissions(
            mActivity,
            permissions,
            requestCode
        )
    }

    fun requestPermissions(fragment: Fragment?, permissions: Array<String>, requestCode: Int) {
        if (fragment == null) {
            return
        }
        //
        if (permissions.isEmpty()) {
            return
        }
        //
        fragment.requestPermissions(
            permissions,
            requestCode
        )
    }

    //request permissions by type
    fun requestPermissions(mActivity: Activity?, permissionsType: PermissionsType) {
        if (mActivity == null) {
            return
        }
        val requestCode =
            getPermissionsRCByType(
                permissionsType
            )
        val permissions =
            getPermissionsByType(
                permissionsType
            )
        if (permissions.isEmpty()) {
            return
        }
        //
        ActivityCompat.requestPermissions(
            mActivity,
            permissions,
            requestCode
        )
    }

    fun requestPermissions(fragment: Fragment?, permissionsType: PermissionsType) {
        if (fragment == null) {
            return
        }
        val requestCode =
            getPermissionsRCByType(
                permissionsType
            )
        val permissions =
            getPermissionsByType(
                permissionsType
            )
        if (permissions.isEmpty()) {
            return
        }
        //
        fragment.requestPermissions(
            permissions,
            requestCode
        )
    }

    //settings dialog
    fun showSettingsDialog(mActivity: Activity?) {
        if (mActivity == null) {
            return
        }
        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle("Allow Permissions In Settings")
        builder.setMessage("Permissions are permanently denied. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
            dialog.cancel()
            openSettings(
                mActivity
            )
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings(mActivity: Activity?) {
        if (mActivity == null) {
            return
        }
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", mActivity.packageName, null)
        intent.data = uri
        mActivity.startActivityForResult(intent, 101)
    }


    //Types of permissions
    enum class PermissionsType {
        LOCATION_PERMISSION,
        CAMERA_PERMISSION,
        STORAGE_PERMISSION
    }
}