package com.scouting.app.misc

import android.os.Environment

object FilePaths {
    private val MAIN_PARENT_DIRECTORY = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS
    ).path + "/scouting/"
    val TEMPLATE_DIRECTORY = MAIN_PARENT_DIRECTORY + "templates/"
    val DATA_DIRECTORY = MAIN_PARENT_DIRECTORY + "data/"
}