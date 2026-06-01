package com.drs.auralife.presentation.library

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
import com.drs.auralife.feature.library.R
import com.drs.auralife.domain.model.Library
import com.google.android.material.bottomsheet.BottomSheetDialog

object AddToLibraryDialog {
    fun showAddLibraryDialog(
        context: Context,
        libraries: List<Library>,
        onAddToLibrary: (libraryName: String) -> Unit,
        onCreateLibrary: (name: String) -> Unit,
    ) {
        val bottomSheetDialog = BottomSheetDialog(context)

        @SuppressLint("InflateParams")
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_library_selection, null)
        bottomSheetDialog.setContentView(dialogView)
        val container = dialogView.findViewById<LinearLayout>(R.id.containerLibraries)
        val btnCreateLibrary = dialogView.findViewById<Button>(R.id.btnCreateLibrary)
        btnCreateLibrary.setOnClickListener {
            bottomSheetDialog.dismiss()
            showCreateLibraryDialog(context, onCreateLibrary)
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
                                val dp5 = dpToPx(5f, resources.displayMetrics).toInt()
                                val dp10 = dpToPx(10f, resources.displayMetrics).toInt()
                                setMargins(dp5, 0, dp5, dp10)
                                setBackgroundResource(R.drawable.rounded)
                                isAllCaps = false
                            }
                    text = library.name
                    setOnClickListener {
                        onAddToLibrary(library.name)
                        bottomSheetDialog.dismiss()
                    }
                },
            )
        }
        bottomSheetDialog.show()
    }

    fun showCreateLibraryDialog(
        context: Context,
        onCreateLibrary: (name: String) -> Unit,
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
                onCreateLibrary(newLibraryName)
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
}
