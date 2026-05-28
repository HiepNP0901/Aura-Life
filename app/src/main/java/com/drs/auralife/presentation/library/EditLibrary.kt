package com.drs.auralife.presentation.library

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.drs.auralife.R
import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository

object EditLibrary {
    fun showEditLibraryDialog(
        context: Context,
        nameLibrary: String,
        callback: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_library, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<AppCompatButton>(R.id.btnRename).setOnClickListener {
            dialog.dismiss()
            showRenameLibraryDialog(context, nameLibrary) {
                callback()
            }
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnDelete).setOnClickListener {
            dialog.dismiss()
            showDeleteLibraryDialog(context, nameLibrary) {
                callback()
            }
        }
        dialog.show()
    }

    fun showRenameLibraryDialog(
        context: Context,
        nameLibrary: String,
        callback: () -> Unit,
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
                LibraryRepository.renameLibrary(nameLibrary, editText.text.toString()) {
                    it
                        .onSuccess {
                            Toast
                                .makeText(
                                    context,
                                    context.getString(R.string.rename_successfully),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            callback()
                        }.onFailure {
                            Toast
                                .makeText(
                                    context,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                }
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
        callback: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.diglog_confirm, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<TextView>(R.id.title).text =
            context.getString(R.string.sure_delete_library)
        dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            LibraryRepository.deleteLibrary(nameLibrary) {
                it
                    .onSuccess {
                        Toast
                            .makeText(
                                context,
                                context.getString(R.string.delete_library) + nameLibrary + context.getString(
                                    R.string.successfully,
                                ),
                                Toast.LENGTH_SHORT,
                            ).show()
                        callback()
                    }.onFailure {
                        Toast
                            .makeText(
                                context,
                                context.getString(R.string.delete_library_failed),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun showDeleteFilmFromLibrary(
        context: Context,
        nameLibrary: String,
        slug: String,
        callback: () -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.diglog_confirm, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<TextView>(R.id.title).text =
            context.getString(R.string.sure_delete_film)
        dialogView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            LibraryRepository.removeFilmFromLibrary(nameLibrary, slug) {
                it
                    .onSuccess {
                        dialog.dismiss()
                        callback()
                    }.onFailure {
                        Toast
                            .makeText(
                                context,
                                context.getString(R.string.error),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
            }
        }
        dialog.show()
    }
}

