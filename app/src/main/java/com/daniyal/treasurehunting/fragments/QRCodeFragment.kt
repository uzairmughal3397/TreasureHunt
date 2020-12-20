package com.daniyal.treasurehunting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.daniyal.treasurehunting.R
import com.daniyal.treasurehunting.databinding.FragmentQRCodeBinding
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRCodeFragment : Fragment(), ZXingScannerView.ResultHandler {
    lateinit var binding:FragmentQRCodeBinding

    var lat=""
    var lng=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*getting data from previous fragment*/
        arguments.let {
           lat=it!!.getString("lat","")
           lng= it.getString("lng","")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_q_r_code, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scannerView.startCamera()
        binding.scannerView.setAutoFocus(true)
        binding.scannerView.setResultHandler(this)
        binding.btnAdd.setOnClickListener {
            val hideConfirmation = HideConfirmMapFragment()
            hideConfirmation.arguments= bundleOf(Pair("lat",lat),Pair("lng",lng))
            val transaction=activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.clMain, hideConfirmation, "fragmnetId")
            transaction.addToBackStack("hideFrag")
            transaction.commit()
        }
    }
    override fun onResume() {
        super.onResume()
        binding.scannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        binding.scannerView.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        binding.scannerView.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result?) {

        /*This is result of scanned QR code*/
            rawResult

    }
}