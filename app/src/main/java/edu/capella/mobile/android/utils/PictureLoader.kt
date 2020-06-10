package edu.capella.mobile.android.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.ImageView
import edu.capella.mobile.android.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * PictureLoader.kt :  Class takes help of java Executor service to make any network
 * call for loading image and stores it inside the cache directory of application once
 * image is loaded, it display it given ImageView. It uses ThreadPool to handle multiple
 * threads.
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  3/1/2020
 *
 */

class PictureLoader {

    private var fileCache: FileCache
    private val imageViews: MutableMap<ImageView, String> =
        Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    private var executorService: ExecutorService? = null
    private val handler: Handler = Handler() // handler to display images in UI

    private var isSampling = true

    private val stub_id: Int = R.drawable.ic_users
    private var isSingleThread = false

    var context : Context
    constructor(ctx: Context)
    {
        this.context = ctx

        var poolSize = 2
        try {
            poolSize = Runtime.getRuntime().availableProcessors()
            Util.trace("PoolSize is : $poolSize")
        } catch (t: Throwable) {
           Util.trace("PoolSize Runtime Error", "" + t.toString())
            t.printStackTrace()
        }
        executorService =
            if (isSingleThread) Executors.newSingleThreadExecutor() else Executors.newFixedThreadPool(
                poolSize
            )

        fileCache = FileCache()
        fileCache.init(context.applicationContext)
    }



    /**
     * specify whether to use single thread or Thread Pool.
     *
     * @param b the b
     */
    fun setSingleThread(b: Boolean) {
        isSingleThread = b
    }

    /**
     * Allow sampling the bitmap while loading, it will help to load large bitmaps to avoid
     * OutOfMemory Issues.
     *
     * @param sampling the sampling true / false
     */
    fun allowSampling(sampling: Boolean) {
        isSampling = sampling
    }


    /**
     * Add url and ImageView in a queue, A single thread or thread pool will take care of multiple
     * loading of Bitmaps in ImageView.
     *
     * @param url       the url of bitmap to load.
     * @param imageView the image view in which bitmap wants to show after loading.
     */
    fun displayImage(url: String, imageView: ImageView)
    {
        if(isCleanningInProgress)
        {
             Thread.sleep(10000)
        }
        imageView.setImageBitmap(null)
        imageViews[imageView] = url
        queuePhoto(url, imageView)
        if (stub_id != -1) imageView.setImageResource(stub_id)
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        val p = PhotoToLoad(url, imageView)
        //if(executorService.isShutdown()==false && executorService.isTerminated()==false)
        executorService?.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f: File? = fileCache.getFile(url)
        var b = decodeFile(f)
        if (b != null) {
            if (b.height < 5 && b.width < 5) {
                b = Bitmap.createScaledBitmap(
                    b, b.width,
                    b.height, true
                )
            }
            return b
        }
        return try {
           /* if(f?.exists() == false)
            {
                f?.createNewFile()
            }*/
            var bitmap: Bitmap? = null
            val imageUrl = URL(url)
            val conn: HttpURLConnection = imageUrl
                .openConnection() as HttpURLConnection
            conn.setConnectTimeout(60000)
            conn.setReadTimeout(60000)
            conn.setInstanceFollowRedirects(true)
            val `is`: InputStream = conn.getInputStream()
            val os: OutputStream = FileOutputStream(f)
            copyStream(`is`, os)
            os.close()
            conn.disconnect()
            bitmap = decodeFile(f)
            if (bitmap!!.height < 5 && bitmap.width < 5) {
                bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.width, bitmap.height,
                    true
                )
            }
            bitmap
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }

    /**
     * Create bitmap from specified file.
     *
     * @param f the file
     * @return the bitmap from File.
     */
    fun decodeFile(f: File?): Bitmap? {
        try {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            val stream1 = FileInputStream(f)
            BitmapFactory.decodeStream(stream1, null, o)
            stream1.close()
            val REQUIRED_SIZE = 100
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            if (isSampling) {
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE
                    ) break
                    width_tmp /= 2
                    height_tmp /= 2
                    scale *= 2
                }
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val stream2 = FileInputStream(f)
            val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
            stream2.close()
            return bitmap
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (t: Throwable) {
        }
        return null
    }

    // Task for the queue
    inner class PhotoToLoad(
        /**
         * The Url.
         */
        var url: String, i: ImageView
    ) {
        /**
         * The Image view.
         */
        var imageView: ImageView

        /**
         * Instantiates a new Photo to load.
         *
         * @param u the url of bitmap
         * @param i the ImageView for bitmap.
         */
        init {
            imageView = i
        }
    }

    /**
     * The Runnable instance, which loads bitmap from specified url asynchronously without blocking the UI thread.
     */
    internal inner class PhotosLoader
    /**
     * Instantiates a new Photos loader.
     *
     * @param photoToLoad the photo to load
     */(
        /**
         * The Photo to load.
         */
        var photoToLoad: PhotoToLoad
    ) : Runnable {

        /**
         * Run.
         */
        override fun run() {
            try {
                if (imageViewReused(photoToLoad)) return
                val bmp: Bitmap?
                bmp = getBitmap(photoToLoad.url)
                if (imageViewReused(photoToLoad)) return
                val bd = BitmapDisplayer(bmp, photoToLoad)
                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }

    }

    /**
     * Image view reused boolean.
     *
     * @param photoToLoad the photo to load
     * @return the boolean
     */
    private fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        return if (tag == null || tag != photoToLoad.url) {
            true
        } else false
    }

    /**
     * Used to display bitmap over UI thread to avoid ANR.
     */
    internal inner class BitmapDisplayer
    /**
     * Instantiates a new Bitmap displayer.
     *
     * @param b the bitmap to display
     * @param p the bean object containing url and image view.
     */(
        /**
         * The Bitmap.
         */
        var bitmap: Bitmap?,
        /**
         * The Photo to load.
         */
        var photoToLoad: PhotoToLoad
    ) :
        Runnable {

        /**
         * Thread's Run method.
         */
        override fun run() {
            if (imageViewReused(photoToLoad)) return
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap)
            } else {
                if (stub_id != -1) photoToLoad.imageView.setImageResource(stub_id)
            }
        }

    }

    /**
     * Clear's file cache used to load bitmap's.
     */
    fun clearCache() {
       /* try {
            //fileCache.clear()
            (context.applicationContext as CapellaApplication).clearOldData()
        }catch (t: Throwable)
        {
            Util.trace("Cleaning cache Failed $t" )
        }*/
        clearOldData()
    }

    private fun copyStream(`is`: InputStream, os: OutputStream) {
        val buffer_size = 1024
        try {
            val bytes = ByteArray(buffer_size)
            while (true) {
                val count: Int = `is`.read(bytes, 0, buffer_size)
                if (count == -1) break
                os.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
        }
    }


    var isCleanningInProgress: Boolean = false
    fun clearOldData()
    {
        isCleanningInProgress = true
        val thread  = Thread{
            kotlin.run { deleteCache(context.applicationContext) }
        }
        thread.start()
    }

    private fun deleteCache(context: Context) {
        try {


            val dir: File =  fileCache.getCacheDirectory()!! //context.cacheDir

                deleteDir(dir)
        } catch (e: Throwable) {
            e.printStackTrace()
        }finally {
            isCleanningInProgress = false
            fileCache.init(context.applicationContext)
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }



}