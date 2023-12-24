package com.mahmoudibrahem.arplayground

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

class MainViewModel : ViewModel() {

    private var mUserRequestedInstall = true

    fun requestARServiceInstall(activity: Activity) {
        try {
            when (ArCoreApk.getInstance().requestInstall(activity, mUserRequestedInstall)) {
                ArCoreApk.InstallStatus.INSTALLED -> {
                    return
                }
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    mUserRequestedInstall = false
                    return
                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            return
        } catch (e: Exception) {
            return
        }
    }



    fun getModelsList(): List<ModelItem> {
        return  listOf(
            ModelItem(
                id = 0,
                image = R.drawable.lion,
                name = "lion",
                scaleFactor = 150f
            ),
            ModelItem(
                id = 1,
                image = R.drawable.cow,
                name = "cow",
                scaleFactor = 3.5f
            ),
            ModelItem(
                id = 2,
                image = R.drawable.dog,
                name = "dog",
                scaleFactor = null
            ),
            ModelItem(
                id = 3,
                image = R.drawable.tiger,
                name = "tiger",
                scaleFactor = 1f
            ),
            ModelItem(
                id = 4,
                image = R.drawable.camel,
                name = "camel",
                scaleFactor = null
            ),
            ModelItem(
                id = 5,
                image = R.drawable.mink,
                name = "mink",
                scaleFactor = 2f
            ),
        )
    }
}