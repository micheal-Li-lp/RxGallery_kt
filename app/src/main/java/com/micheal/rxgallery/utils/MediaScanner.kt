package com.micheal.rxgallery.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri

class MediaScanner(context: Context) {
    private lateinit var mediaScanConn: MediaScannerConnection
    private var fileType: String? = null
    private var filePaths: Array<String>? = null
    private var scanCallback: ScanCallback? = null

    init {
        val client = object : MediaScannerConnection.MediaScannerConnectionClient{
            override fun onMediaScannerConnected() {
                Logger.i("onMediaScannerConnected")
                filePaths?.forEach {
                    mediaScanConn.scanFile(it, fileType)
                }
            }
            override fun onScanCompleted(p0: String?, p1: Uri?) {
                Logger.i("onScanCompleted")
                mediaScanConn.disconnect()
                scanCallback?.onScanCompleted(filePaths)
                fileType = null
                filePaths = null
            }
        }

        mediaScanConn = MediaScannerConnection(context, client)

    }


    /**
     * 扫描文件标签信息
     *
     * @param filePath 文件路径
     * @param fileType 文件类型
     */

    fun scanFile(filePath: String, fileType: String, callback: ScanCallback) {
        this.filePaths = arrayOf(filePath)
        this.fileType = fileType
        this.scanCallback = callback
        //连接之后调用MusicSannerClient的onMediaScannerConnected()方法
        mediaScanConn.connect()
    }

    /**
     * @param filePaths 文件路径
     * @param fileType  文件类型
     */
    fun scanFile(filePaths: Array<String>, fileType: String, callback: ScanCallback) {
        this.filePaths = filePaths
        this.fileType = fileType
        this.scanCallback = callback
        mediaScanConn.connect()
    }

    fun unScanFile() {
        mediaScanConn.disconnect()
    }

    interface ScanCallback {
        fun onScanCompleted(images: Array<String>?)
    }



}