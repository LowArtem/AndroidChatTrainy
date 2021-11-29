package com.trialbot.trainyapplication.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.resultDialog(
    result: Boolean,
    textSuccess: String = "The operation was successful",
    textFailed: String = "The operation failed",
    successfulAction: () -> Unit = {  }
) {
    AlertDialog.Builder(this).apply {
        if (result) {
            setTitle("Success")
            setMessage(textSuccess)
            setNeutralButton("Ok") { _, _ -> successfulAction() }
        } else {
            setTitle("Fail")
            setMessage(textFailed)
            setNeutralButton("Ok") { _, _ ->  }
        }

        setCancelable(true)
    }.create().show()
}

// Не показывает алерт, если все успешно, выполняет действие, если оно задано
fun Context.resultDialogWithoutSuccessText(
    result: Boolean,
    textFailed: String,
    successfulAction: () -> Unit = { }
) {
    if (!result) {
        AlertDialog.Builder(this).apply {
            setTitle("Fail")
            setMessage(textFailed)
            setNeutralButton("Ok") { _, _ -> }
            setCancelable(true)
        }.create().show()
    } else {
        successfulAction()
    }
}

fun Context.confirmDialog(textConfirm: String, positiveAction: () -> Unit) {
    AlertDialog.Builder(this).apply {
        setTitle("Confirmation")
        setMessage(textConfirm)

        setPositiveButton("Yes") { _, _ ->
            positiveAction()
        }

        setNegativeButton("Cancel") {_, _ -> }

        setCancelable(true)
    }.create().show()
}