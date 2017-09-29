package com.sumasoft.coffeeshopfinder.utils

import android.R.string.cancel
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.app.Activity
import android.content.Context
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability



/**
 * Created by sumasoft on 26/09/17.
 */

object MapUtils {

    //check for google play service is available on device
    fun isGooglePlayServicesAvailable(mContext: Context, activity: Activity): Boolean {
        try {
            val googleAPI = GoogleApiAvailability.getInstance()
            val result = googleAPI.isGooglePlayServicesAvailable(mContext)
            if (result != ConnectionResult.SUCCESS) {
                try {
                    if (googleAPI.isUserResolvableError(result)) {
                        googleAPI.getErrorDialog(activity, result,
                                0).show()
                    }
                }catch (e : Exception){

                }
                return false
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        return true
    }

    //check for gps is enabled or not. If not enabled then redirect to settings of device to enable location manually
    fun isGpsEnabled(mContext: Context, activity: Activity) {
        try {
            val manager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val builder = AlertDialog.Builder(mContext)
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> activity.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
                        .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                val alert = builder.create()
                alert.show()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


}
