package com.drs.auralife.utils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageEncoderDecoder {
    // Encode Bitmap to Base64 string
    fun encodeToBase64(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        size: Int = 80
    ): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(compressFormat, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Decode Base64 string to Bitmap
    fun decodeFromBase64(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
        catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
