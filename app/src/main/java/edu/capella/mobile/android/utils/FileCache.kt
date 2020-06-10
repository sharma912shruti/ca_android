package edu.capella.mobile.android.utils

import android.content.Context
import java.io.File

/**
 * FileCache.kt : Class used to store temporary data as a cache on SD Card.
 *
 * @author  :  Jayesh Lahare
 * @version :  1.0
 * @since   :  2/22/2020
 *
 *
 */
class FileCache {
    private var cacheDir: File? = null



    /**
     * Instantiates a new File cache.
     *
     * @param context the context
     */
    fun init(context: Context) {
//        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//            cacheDir = File(createCacheDir(), CACHE_DIRECTORY_NAME)
//        } else

        cacheDir = context.getCacheDir()
        if (!cacheDir!!.exists())
        {
            cacheDir!!.mkdirs()

        }

        val dataDir = File(cacheDir, "Data")
        if(!dataDir!!.exists()) {
            dataDir.mkdir()
        }
        cacheDir = dataDir
    }
    fun initPrivate(context: Context) {
        cacheDir = context.getCacheDir()
        if (!cacheDir!!.exists())
        {
            cacheDir!!.mkdirs()
        }
        val privateDir = File(cacheDir, "Private")

        if (!privateDir!!.exists()) {
            privateDir.mkdir()
        }
        cacheDir = privateDir
    }
    fun getCacheDirectory(): File?
    {
        return cacheDir
    }

    /**
     * Get stored file for specified url.
     *
     * @param url the path of the file
     * @return the file object.
     */
    fun getFile(url: String): File? {
        val filename = url.hashCode().toString()
        return File(cacheDir, filename)
    }

    /**
     * Delete all the files available in cache directory.
     */
    fun clear() {
        val files: Array<File> = cacheDir!!.listFiles() ?: return
        for (f in files) f.delete()
    }


//    /**
//     * Returns path of base directory of Capella application.
//     * @return object of File.
//     */
//    private fun createCacheDir(): File? {
//        return Util.getCapellaDir()
//    }

}
