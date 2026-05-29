package com.drs.auralife.presentation.library

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.drs.auralife.R

object EditLibraryDialog {
    fun showEditLibraryDialog(
        context: Context,
        nameLibrary: String,
        onRename: (newName: String) -> Unit,
        onDelete: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_library, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<AppCompatButton>(R.id.btnRename).setOnClickListener {
            dialog.dismiss()
            showRenameLibraryDialog(context, nameLibrary, onRename)
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnDelete).setOnClickListener {
            dialog.dismiss()
            showDeleteLibraryDialog(context, nameLibrary, onDelete)
        }
        dialog.show()
    }

    fun showRenameLibraryDialog(
        context: Context,
        nameLibrary: String,
        onConfirm: (newName: String) -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_rename, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<TextView>(R.id.title).text =
            context.getString(R.string.rename_library)
        val editText = dialogView.findViewById<TextView>(R.id.editText)
        editText.text = nameLibrary
        dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            if (editText.text.toString().isNotBlank()) {
                onConfirm(editText.text.toString())
                dialog.dismiss()
            } else {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.library_name_cannot_be_empty),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
        dialog.show()
    }

    fun showDeleteLibraryDialog(
        context: Context,
        nameLibrary: String,
        onConfirm: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<TextView>(R.id.title).text =
            context.getString(R.string.sure_delete_library)
        dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun showDeleteFilmFromLibrary(
        context: Context,
        nameLibrary: String,
        slug: String,
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
