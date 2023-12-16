/*
    Author: LilWons
    Date: 2023-12-09

    Description: Utility class for logic functions.
 */
package com.example.casestudy3

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Util {
    companion object {
        /*
        Creates dialog asking to grant permissions for parameter permissions.
        Response to dialog handled in onRequestPermissionsResult()
        */
        fun requestPermissions(activity: Activity, requestCode: Int, permissions: Array<String>) {
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode
            )
        }
        /*
        Checks if permission is granted for parameter permissions.
        Returns boolean.
         */
        fun permsCheck(context: Context, permissions:String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permissions
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}