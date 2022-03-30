package com.example.harmonyapp.alertDialog

interface DialogListener {
    fun onYesClicked(obj: Any?)
    fun onNoClicked(error: String?)
}