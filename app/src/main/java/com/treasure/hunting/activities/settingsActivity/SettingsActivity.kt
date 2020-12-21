package com.treasure.hunting.activities.settingsActivity

import android.app.Dialog
import android.content.pm.PackageManager
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.admin.assesmenttest.helpers.PermissionHelper
import com.treasure.hunting.R
import com.treasure.hunting.database.AppDatabase
import com.treasure.hunting.databinding.ActivitySettingsBinding
import com.treasure.hunting.fragments.listOfGeoCaches.ListsOfGeoCachesFragment
import com.treasure.hunting.helpers.AppConstants
import com.treasure.hunting.helpers.SharedPrefHelper
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    var tts: TextToSpeech? = null
    var appFolderName: File? = null
    var APP_FOLDER = "Treasure Hunt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        tts = TextToSpeech(this) {}

        if (PermissionHelper.hasPermissions(
                this,
                PermissionHelper.PermissionsType.STORAGE_PERMISSION
            )
        ) {
            appFolderName =
                File(Environment.getExternalStorageDirectory(), APP_FOLDER)
            if (!appFolderName!!.exists()) {
                appFolderName!!.mkdirs()
            }
        }
        else {
            PermissionHelper.requestPermissions(
                this,
                PermissionHelper.PermissionsType.STORAGE_PERMISSION
            )
        }




        binding.tvListOfFoundGeoCaches.setOnClickListener {
            val listsOfGeoCachesFragment = ListsOfGeoCachesFragment()
            listsOfGeoCachesFragment.arguments =
                bundleOf(Pair("type", AppConstants.COMPLETED_EXPLORED_STATUS))
            startFrag(listsOfGeoCachesFragment, "found_geo_caches")
        }
        binding.tvListOfRemainingGeoCaches.setOnClickListener {
            val listsOfGeoCachesFragment = ListsOfGeoCachesFragment()
            listsOfGeoCachesFragment.arguments =
                bundleOf(Pair("type", AppConstants.UN_EXPLORED_STATUS))
            startFrag(listsOfGeoCachesFragment, "remaining_geo_caches")
        }
        binding.tvSetRadius.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.set_radius_layout)
            dialog.window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
            val dialogEdt = dialog.findViewById<EditText>(R.id.edtRadius)
            val btnSetRadius = dialog.findViewById<Button>(R.id.btnRadius)
            btnSetRadius.setOnClickListener {
                if (dialogEdt.text.isNullOrEmpty()) {
                    showMessage("Radius cannot be empty")
                } else {
                    SharedPrefHelper().writeString(
                        this,
                        AppConstants.RADIUS_KEY,
                        dialogEdt.text.toString()
                    )
                    val radius= SharedPrefHelper().readString(this,AppConstants.RADIUS_KEY)
                    binding.tvCurrentRadius.text="Current Radius:$radius"
                    dialog.dismiss()
                }
            }
        }

        binding.switchVoiceOutput.isChecked =
            SharedPrefHelper().readBoolean(this, AppConstants.VOICE_KEY)

        binding.switchVoiceOutput.setOnCheckedChangeListener { compoundButton, b ->
            SharedPrefHelper().writeBoolean(this, AppConstants.VOICE_KEY, b)
            binding.switchVoiceOutput.isChecked =
                SharedPrefHelper().readBoolean(this, AppConstants.VOICE_KEY)
        }

        val radius= SharedPrefHelper().readString(this,AppConstants.RADIUS_KEY)
        binding.tvCurrentRadius.text="Current Radius:$radius"


        binding.tvExportDb.setOnClickListener {

            if (!PermissionHelper.hasPermissions(
                    this,
                    PermissionHelper.PermissionsType.STORAGE_PERMISSION
                )
            )
                PermissionHelper.requestPermissions(
                    this,
                    PermissionHelper.PermissionsType.STORAGE_PERMISSION
                )



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exportToGpx("name.gpx", appFolderName!!)
            } else
                showMessage("Export cannot be done on android 7.0")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun exportToGpx(fileName: String, file: File) {

        if (!PermissionHelper.hasPermissions(
                this,
                PermissionHelper.PermissionsType.STORAGE_PERMISSION
            )
        ) {
            PermissionHelper.requestPermissions(
                this,
                PermissionHelper.PermissionsType.STORAGE_PERMISSION
            )
            showMessage("Need Storage Permission to export database.")
            return
        }


        val header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        val name = """
            <name>$fileName</name><trkseg>
            
            """.trimIndent()

        var segments = ""
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

        AppDatabase.invoke(this).savedTaskDao().getAllSavedTask().observe(this, {
            it.forEach { savedTask ->
                segments += """<trkpt lat="${savedTask.lat}" lon="${
                    savedTask.lng
                }"><time>${df.format(Date())}</time></trkpt>"""
            }

            val footer = "</trkseg></trk></gpx>"

            try {
                val gpxfile: File = File(file, "file.gpx")
                val writer = FileWriter(gpxfile, false)
                writer.append(header)
                writer.append(name)
                writer.append(segments)
                writer.append(footer)
                writer.flush()
                writer.close()
                showMessage("File stored in your app's folder.")
            } catch (e: IOException) {
                showMessage(e.localizedMessage!!.toString())
            }
        })


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //check if permissions are granted or not
        grantResults.forEach {
            if (requestCode == PermissionHelper.STORAGE_PERMISSION_REQUEST_CODE && it == PackageManager.PERMISSION_GRANTED) {
                appFolderName =
                    File(Environment.getExternalStorageDirectory(), APP_FOLDER)
                if (!appFolderName!!.exists()) {
                    appFolderName!!.mkdirs()
                }
            } else {
                showMessage("Need Storage Permission to export database")
            }
        }
    }


    private fun showMessage(msg: String) {
        if (SharedPrefHelper().readBoolean(this, AppConstants.VOICE_KEY)) {
            tts!!.language = Locale.UK
            tts!!.speak(msg, TextToSpeech.QUEUE_ADD, null)
        } else
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun startFrag(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().add(R.id.clMain, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

}