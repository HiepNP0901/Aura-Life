package com.drs.auralife.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import android.Manifest

class PermissionPhotoHandler(
    private val activity: Activity,
    private val onPhotoPicked: (Uri?) -> Unit
) {
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private const val REQUEST_CODE_PICK_IMAGE = 1002
    }

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
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

    fun handlePermissionsResult(requestCode: Int, grantResults: IntArray) {
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
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            onPhotoPicked(selectedImageUri)
        }
    }
}