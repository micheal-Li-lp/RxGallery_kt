package com.micheal.rxgallery.utils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object IOUtils {
    @JvmStatic
    fun close(stream: OutputStream?){
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    @JvmStatic
    fun close(stream :InputStream?){
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    @JvmStatic
    fun flush(stream: OutputStream?){
        if (stream != null) {
            try {
                stream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

}