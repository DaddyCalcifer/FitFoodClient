package com.fitfood.clientapp

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraPreview(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Camera.PreviewCallback {
    private var camera: Camera? = null
    private var previewCallback: ((ByteArray) -> Unit)? = null

    init {
        holder.addCallback(this)
    }

    fun setPreviewCallback(callback: (ByteArray) -> Unit) {
        previewCallback = callback
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera = Camera.open().apply {
            setPreviewDisplay(holder)
            setPreviewCallback(this@CameraPreview)
            startPreview()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        data?.let { previewCallback?.invoke(it) }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        camera?.stopPreview()
        camera?.startPreview()
    }
}
