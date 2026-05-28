package com.drs.auralife.core.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

class PermissionPhotoHandler(
    private val activity: Activity,
    private val activityResultLauncher: ActivityResultLauncher<Intent>,
) {
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun checkAndRequestPermissions() {
        if (permissions.all { ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED }) {
            pickImageFromGallery()
        } else {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    fun handlePermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS &&
            grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        activityResultLauncher.launch(intent)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }
}
