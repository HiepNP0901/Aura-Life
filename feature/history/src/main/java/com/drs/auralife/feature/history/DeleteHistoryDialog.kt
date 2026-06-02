package com.drs.auralife.feature.history

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.drs.auralife.core.designsystem.R as DsR

object DeleteHistoryDialog {
    fun showDeleteFilmFromHistory(
        context: Context,
        onConfirm: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(DsR.layout.dialog_confirm, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(DsR.id.title).text =
            context.getString(R.string.sure_delete_film)

        dialogView.findViewById<AppCompatButton>(DsR.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(DsR.id.btnConfirm).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }
}
