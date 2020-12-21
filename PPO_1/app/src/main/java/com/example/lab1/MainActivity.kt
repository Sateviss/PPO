package com.example.lab1

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import android.Manifest


class MainActivity : AppCompatActivity() {

    private lateinit var telephonyManager: TelephonyManager

    @SuppressLint("SetTextI18n", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        version.text = "${getString(R.string.version_name)}: ${BuildConfig.VERSION_NAME}"
        deviceId.text = "${getString(R.string.device_id)}: ${getString(R.string.unknown)}"
        getDeviceId()
    }

    @SuppressLint("SetTextI18n", "HardwareIds")
    private fun getDeviceId() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 101)
        } else {
            deviceId.text = "${getString(R.string.device_id)}: ${telephonyManager.getDeviceId()}"
        }
    }

    @SuppressLint("SetTextI18n", "HardwareIds")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 101)
                        return
                    }
                    deviceId.text = "${getString(R.string.device_id)}: ${telephonyManager.getDeviceId()}"
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

}
