package com.example.dalcommunity.activity.petitionactivity

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor

// ImageContentProvider.kt
class ImageContentProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Not needed for this example
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Not needed for this example
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Not needed for this example
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // Not needed for this example
        return 0
    }

    override fun getType(uri: Uri): String? {
        // Not needed for this example
        return null
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val context = context ?: return null
        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.let { ParcelFileDescriptorUtil.pipeFrom(it) }
    }
}
