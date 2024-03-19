package com.csci5708.dalcommunity.util

import android.os.ParcelFileDescriptor
import java.io.IOException
import java.io.InputStream

object ParcelFileDescriptorUtil {
    fun pipeFrom(input: InputStream): ParcelFileDescriptor {
        try {
            val pipeFd = ParcelFileDescriptor.createPipe()
            val writer = ParcelFileDescriptor.AutoCloseOutputStream(pipeFd[1])
            Thread {
                try {
                    input.copyTo(writer)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        input.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    try {
                        writer.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.start()
            return pipeFd[0]
        } catch (e: IOException) {
            throw RuntimeException("Failed to create pipe", e)
        }
    }

    // If you need to implement other pipe methods, you can add them here
}
