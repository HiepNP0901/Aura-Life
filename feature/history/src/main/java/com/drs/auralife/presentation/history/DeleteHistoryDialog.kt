package com.drs.auralife.presentation.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.drs.auralife.feature.history.R

object DeleteHistoryDialog {
    fun showDeleteFilmFromHistory(
        context: Context,
        onConfirm: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.title).text =
            context.getString(R.string.sure_delete_film)

        dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }
}
