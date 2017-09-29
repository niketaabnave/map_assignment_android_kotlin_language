package com.sumasoft.coffeeshopfinder.activities

import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sumasoft.coffeeshopfinder.R
import android.content.Intent
import android.os.Handler
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission
import permission.auron.com.marshmallowpermissionhelper.PermissionResult
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils


class SplashActivity : ActivityManagePermission() {

    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //check for permission in android version greater than 6.0
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission(this)
        }
    }

    //add delay of 3 sec. After 2 sec show next activity
    private fun goToNextScreen() {
        Handler().postDelayed(Runnable /*
     * Showing splash screen with a timer. This will be useful when you
     * want to show case your app logo / company
     */

        {
            val intent = Intent(this@SplashActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)
    }

    fun checkLocationPermission(mContext: Context) {
        val permissionAsk = arrayOf(PermissionUtils.Manifest_ACCESS_FINE_LOCATION)
        askCompactPermissions(permissionAsk, object : PermissionResult {

            override fun permissionGranted() {
                goToNextScreen()
            }


            override fun permissionDenied() {
                finish()
            }

            override fun permissionForeverDenied() {
                run {
                    val permissionAsk = arrayOf<String>(PermissionUtils.Manifest_ACCESS_FINE_LOCATION)
                    val isGranted = isPermissionsGranted(mContext, permissionAsk)
                    if (!isGranted) {
                        finish()
                    } else {
                        println("IS GRANTED -- " + isGranted)
                        goToNextScreen()
                    }
                }
            }
        })
    }
}
