package com.kdroid.imagefilter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coroutineScope.launch {
            val deferredImageBitmap: Deferred<Bitmap> =
                coroutineScope.async(Dispatchers.IO) { getOriginalBitmap() }
            val bitmapImage = deferredImageBitmap.await()
            val deferredFilterView = async(Dispatchers.Default) {
                Filter.apply(bitmapImage)
            }

            val filteredImage = deferredFilterView.await()
            setImageToView(filteredImage)
        }

    }

    private fun setImageToView(bitmapImage: Bitmap) {
        findViewById<ImageView>(R.id.image_view).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
        findViewById<ImageView>(R.id.image_view).setImageBitmap(bitmapImage)

    }

    private fun getOriginalBitmap() =
        URL(IMAGE_URL).openStream().use {
            BitmapFactory.decodeStream(it)
        }

    companion object URL {
        const val IMAGE_URL =
            "https://raw.githubusercontent.com/DevTides/JetpackDogsApp/master/app/src/main/res/drawable/dog.png"
    }
}