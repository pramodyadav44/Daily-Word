package com.pramod.todaysword.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Context.restartApp() {
    val intent = Intent(Intent.ACTION_MAIN)
    startActivity(intent)
    when (this) {
        is Activity -> {
            finish()
        }
        is Fragment -> {
            requireActivity().finish()
        }
        else -> {
            Toast.makeText(
                this,
                "Unable to finish previous activity or fragment activity",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun Context.openWebsite(url: String) {
    val webpageUri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpageUri)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "No browser application found", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openGmail(emails: Array<String>, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
        .apply {
            data = Uri.Builder().scheme("mailto").build()
            putExtra(Intent.EXTRA_EMAIL, emails)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Please install Gmail app", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openGoogleReviewPage() {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName")
    )
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Please install or update Google play app", Toast.LENGTH_SHORT).show()
    }
}