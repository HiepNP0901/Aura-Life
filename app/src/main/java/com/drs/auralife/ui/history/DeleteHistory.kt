package com.drs.auralife.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.drs.auralife.R
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.history.HistoryRepository
import com.drs.auralife.utils.HistoryUtils

object DeleteHistory {
    @SuppressLint("SetTextI18n")
    fun showDeleteFilmFromHistory(context: Context, slug: String, callback: () -> Unit) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.diglog_confirm, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.title).text =
            context.getString(R.string.sure_delete_film)

        dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            if (Authentication.isLoggedIn()) {
                HistoryRepository.deleteHistory(slug)
            }
            else {
                HistoryUtils.removeHistory(context, slug)
            }
            dialog.dismiss()
            callback()
        }

        dialog.show()
    }
}