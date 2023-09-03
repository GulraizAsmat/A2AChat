package app.unduit.a2achatapp.helpers

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import app.unduit.a2achatapp.R

class ProgressDialog(context: Context) : AlertDialog(context, R.style.progressDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress_bar)
    }

    fun progressBarVisibility(isShow: Boolean) {
        if (isShow) {
            show()
        } else {
            dismiss()
        }
    }
}