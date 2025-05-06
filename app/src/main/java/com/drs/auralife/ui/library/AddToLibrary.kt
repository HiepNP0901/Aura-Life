package com.drs.auralife.ui.library

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.util.TypedValueCompat.dpToPx
import com.drs.auralife.R
import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository
import com.drs.auralife.data.model.film.Movie
import com.google.android.material.bottomsheet.BottomSheetDialog

object AddToLibrary {
    fun showAddLibraryDialog(
        context: Context,
        film: Movie,
    ) {
        LibraryRepository.getLibrary { libraries ->
            val bottomSheetDialog = BottomSheetDialog(context)

            @SuppressLint("InflateParams")
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_library_selection, null)
            bottomSheetDialog.setContentView(dialogView)
            val container = dialogView.findViewById<LinearLayout>(R.id.containerLibraries)
            val btnCreateLibrary = dialogView.findViewById<Button>(R.id.btnCreateLibrary)
            btnCreateLibrary.setOnClickListener {
                bottomSheetDialog.dismiss()
                showCreateLibraryDialog(context, film)
            }
            libraries.forEach { library ->
                container.addView(
                    AppCompatButton(context).apply {
                        layoutParams =
                            LinearLayout
                                .LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                ).apply {
                                    var dp5 = dpToPx(5f, resources.displayMetrics).toInt()
                                    var dp10 = dpToPx(10f, resources.displayMetrics).toInt()
                                    setMargins(dp5, 0, dp5, dp10)
                                    setBackgroundResource(R.drawable.rounded)
                                    isAllCaps = false
                                }
                        text = library.name
                        setOnClickListener {
                            addToLibrary(context, film, library.name)
                            bottomSheetDialog.dismiss()
                        }
                    },
                )
            }
            bottomSheetDialog.show()
        }
    }

    fun showCreateLibraryDialog(
        context: Context,
        film: Movie,
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rename, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val inputLibraryName = dialogView.findViewById<EditText>(R.id.editText)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnConfirm)
        title.text = context.getString(R.string.create_new_library)
        inputLibraryName.hint = context.getString(R.string.enter_new_library_name)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnCreate.setOnClickListener {
            val newLibraryName = inputLibraryName.text.toString()
            if (newLibraryName.isNotBlank()) {
                LibraryRepository.createLibrary(
                    newLibraryName,
                    film.posterUrl.toString(),
                    film.slug,
                    film.episodeCurrent.toString(),
                ) {
                    it
                        .onSuccess {
                            Toast
                                .makeText(
                                    context,
                                    context.getString(
                                        if (it) {
                                            R.string.added_to_library_successfully
                                        } else {
                                            R.string.library_is_exits
                                        },
                                    ),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }.onFailure {
                            Toast
                                .makeText(context, R.string.failed_to_add_to_library, Toast.LENGTH_SHORT)
                                .show()
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

    fun addToLibrary(
        context: Context,
        film: Movie,
        libraryName: String,
    ) {
        LibraryRepository.addLibraryData(
            libraryName,
            film.posterUrl.toString(),
            film.slug,
            film.episodeCurrent.toString(),
        ) {
            it
                .onSuccess {
                    Toast
                        .makeText(
                            context,
                            context.getString(
                                if (it) {
                                    R.string.added_to_library_successfully
                                } else {
                                    R.string.library_already_exists
                                },
                            ),
                            Toast.LENGTH_SHORT,
                        ).show()
                }.onFailure {
                    Toast
                        .makeText(context, R.string.failed_to_add_to_library, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}
