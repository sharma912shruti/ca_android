package edu.capella.mobile.android.network


import android.content.Context
import android.util.Log
import android.util.Pair
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.utils.FileCache
import edu.capella.mobile.android.utils.Util
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.*
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

/**
 * NetworkService.kt : A class to perform synchronous network operation with different HTTP methods
 * like GET / POST / PUT and JSON Request.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 */


object NetworkService {

    /**
     * The constant INTERNAL_NETWORK_ERROR.
     */
    val INTERNAL_NETWORK_ERROR = "Internal Server Error."

    val READ_TIME_OUT =    BuildConfig.READ_TIMEOUT   // /*10000*/ 50000
    val CONNECTION_TIME_OUT =   BuildConfig.CONNECTION_TIMEOUT// /*15000*/ 50000
    /**
     * Method to call GET method with or without parameters For HTTPS urls.
     *
     * @param url            the url
     * @param postParameters the parameters required to create query string, can be null.
     * @param headers the headers parameters required for connecting with server, can be null.
     * @return the pair,  the response in Pair format.
     * @see Pair
     */
    fun sendHttpsGet(url: String, postparameters: HashMap<String, Any>? , headers : HashMap<String, Any?>?): Pair<String, Any> {
        val dataReceived = StringBuffer()
        try {
            val cookieManager = CookieManager()
            CookieHandler.setDefault(cookieManager)

            var finalUrl = url

            if (postparameters != null)
                finalUrl += getQueryString(postparameters)

            Util.trace("Url ", "Url : $finalUrl")

            val urlLink = URL(finalUrl)


            var connection : HttpsURLConnection? = urlLink.openConnection() as HttpsURLConnection

            if(headers !== null)
                connection = getHttpsConnectionWitHeader(connection, headers)


            val sslsocketfactory: SSLSocketFactory =
                SSLSocketFactory.getDefault() as SSLSocketFactory
            connection?.sslSocketFactory = sslsocketfactory

            connection?.instanceFollowRedirects = false


            //connection.setUseCaches(true);
            connection?.readTimeout = READ_TIME_OUT
            connection?.connectTimeout = CONNECTION_TIME_OUT
            connection?.requestMethod = "GET"


            connection?.setRequestProperty("Content-Type", "application/json")

            connection?.useCaches = false
            connection?.allowUserInteraction = false


            var responseCode = connection?.responseCode
            var responseMsg = connection?.responseMessage
            Util.trace("Response Code here is : $responseCode")


            if(responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode ==HttpURLConnection.HTTP_MOVED_TEMP )
            {
                var count = 0
                do {
                    if(count>=3) // STUCK in REDIRECTION LOOP
                        break

                    when (responseCode) {
                        HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP -> {
                            var location = connection?.getHeaderField("Location")
                            // location = URLDecoder.decode(location, "UTF-8")
                            Util.trace("Response location $location")
                            var newUrl = url

                            if(location!= "")
                            {
                                newUrl = location!!
                                location =""
                            }
                            connection = getTempConnection(newUrl, location!!)

                            responseCode = connection?.responseCode
                            responseMsg = connection?.responseMessage
                            Util.trace("Response Code new is : $responseCode")
                            count++

                        }
                    }
                } while (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode ==HttpURLConnection.HTTP_MOVED_TEMP )
            }


            if (responseCode != 200)
            {

                return if (responseMsg!!.isNotEmpty())
                    Pair.create(NetworkConstants.NETWORK_ERROR, responseMsg)
                else
                    Pair.create(NetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR)

            }



            // var bi = BufferedInputStream(connection.inputStream)
            var bi =  connection?.inputStream

            val buffer = ByteArray(1024)

            var ch: Int
            do
            {

                ch = bi!!.read(buffer)
                if(ch!=-1)
                    dataReceived.append(String(buffer, 0, ch))
            } while(ch !=-1)


            bi.close()
            connection?.disconnect()

           // Util.trace("Response", "" + dataReceived.toString())

            return Pair.create(NetworkConstants.NETWORK_SUCCESS, dataReceived.toString())

        } catch (t: Throwable) {
            Util.trace("Error = $t")

            t.printStackTrace()
            return Pair.create(NetworkConstants.NETWORK_ERROR, t.toString())

        }

    }

    private fun getTempConnection(finalUrl:String , location:String ) : HttpsURLConnection?
    {
        val base = URL(finalUrl)
        val next = URL(base , location)
        val urlLink = URL(next.toExternalForm())

        var connection : HttpsURLConnection? = urlLink.openConnection() as HttpsURLConnection


        /*if(headers !== null)
            connection = getHttpsConnectionWitHeader(connection, headers)*/

        val sslsocketfactory: SSLSocketFactory =
            SSLSocketFactory.getDefault() as SSLSocketFactory
        connection?.sslSocketFactory = sslsocketfactory

        connection?.instanceFollowRedirects = false


        //connection.setUseCaches(true);
        connection?.readTimeout = READ_TIME_OUT
        connection?.connectTimeout = CONNECTION_TIME_OUT
        connection?.requestMethod = "GET"


        connection?.setRequestProperty("Content-Type", "application/json")

        connection?.useCaches = false
        connection?.allowUserInteraction = false
        return  connection
    }


    /**
     * Method to call GET method with or without parameters For HTTP urls.
     *
     * @param url            the url
     * @param postParameters the parameters required to create query string, can be null.
     * @param headers the headers parameters required for connecting with server, can be null.
     * @return the pair,  the response in Pair format.
     * @see Pair
     */
    fun sendHttpGet(url: String, postparameters: HashMap<String, Any>? , headers : HashMap<String, Any?>?): Pair<String, Any> {
        val dataReceived = StringBuffer()
        try {
            var finalUrl = url

            if (postparameters != null)
                finalUrl += getQueryString(postparameters)

            Util.trace("Url ", "Url : $finalUrl")

            val urlLink = URL(finalUrl)


            var connection : HttpURLConnection? = urlLink.openConnection() as HttpURLConnection


            if(headers !== null)
                connection = getHttpConnectionWitHeader(connection, headers)


            //connection.setUseCaches(true);
            connection?.readTimeout = READ_TIME_OUT
            connection?.connectTimeout = CONNECTION_TIME_OUT
            connection?.requestMethod = "GET"


            connection?.setRequestProperty("Content-Type", "application/json")

            connection?.useCaches = false
            connection?.allowUserInteraction = false


            val responseCode = connection?.responseCode
            val responseMsg = connection?.responseMessage


            if (responseCode != 200) {
                return if (responseMsg!!.isNotEmpty())
                    Pair.create(NetworkConstants.NETWORK_ERROR, responseMsg)
                else
                    Pair.create(NetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR)

            }

           // Util.trace("Response Code $responseCode")

            // var bi = BufferedInputStream(connection.inputStream)
            var bi =  connection?.inputStream

            val buffer = ByteArray(1024)

            var ch: Int
            do
            {

                ch = bi!!.read(buffer)
                if(ch!=-1)
                    dataReceived.append(String(buffer, 0, ch))
            } while(ch !=-1)


            bi.close()
            connection?.disconnect()

           // Util.trace("Response", "" + dataReceived.toString())

            return Pair.create(NetworkConstants.NETWORK_SUCCESS, dataReceived.toString())

        } catch (t: Throwable) {
            Util.trace("Error = $t")

            t.printStackTrace()
            return Pair.create(NetworkConstants.NETWORK_ERROR, t.toString())

        }

    }

    /**
     * Method to call POST method with or without parameters for HTTPS Urls
     *
     * @param urlString            the url
     * @param postParameters the parameters required to post data, can be null.
     * @param headers the headers parameters required for connecting with server, can be null.
     * @return the pair,  the response in Pair format.
     * @see Pair
     */
    fun sendHttpsPost(
        urlString: String,
        postParameters: HashMap<String, Any>?,
        headers : HashMap<String, Any?>?
    ): Pair<String, Any> {
        val dataReceived = StringBuffer()

        requireNotNull(postParameters) { "Post parameters can not be null" }

        try {
            val url = URL(urlString)

            Util.trace("Url ", "Url : $url")

            var conn = url.openConnection() as HttpsURLConnection?
            if(headers !== null)
            {
                conn = getHttpsConnectionWitHeader(conn, headers)
            }

            val sslsocketfactory: SSLSocketFactory =
                SSLSocketFactory.getDefault() as SSLSocketFactory
            conn?.sslSocketFactory = sslsocketfactory

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "POST"
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //conn?.setRequestProperty("Accept", "application/json")
           // conn?.setRequestProperty("Accept", "*/*")

            conn?.doInput = true
            conn?.doOutput = true

            val writer = conn?.outputStream
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            if(postParameters.size>0) {
                writer?.write(getPostString(postParameters).toByteArray())
            }
            writer?.flush()
            writer?.close()

            conn?.connect()

            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage

            Util.trace("Response Code $responseCode")
           // Util.trace("Response msg $responseMsg")

            if (responseCode != 200) {
                return if (responseMsg!!.isNotEmpty())
                    Pair.create(NetworkConstants.NETWORK_ERROR, responseMsg)
                else
                    Pair.create(NetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR)

            }

           // Util.trace("Response Code $responseCode")

            var bi = BufferedInputStream(conn?.inputStream)

            val buffer = ByteArray(1024)

            var ch: Int
            do
            {
                ch = bi.read(buffer)
                if(ch!=-1)
                  dataReceived.append(String(buffer, 0, ch))
            } while(ch !=-1)


            bi.close()
            conn?.disconnect()

            //Util.trace("Response Received", " = $dataReceived")

            return Pair.create(NetworkConstants.NETWORK_SUCCESS, dataReceived.toString())


        } catch (t: Throwable) {
            Log.e("Error ", " = $t")
            t.printStackTrace()
            return Pair.create(NetworkConstants.NETWORK_ERROR, t.toString())
        }

    }

    /**
     * Method to call POST method with or without parameters for HTTP Urls
     *
     * @param urlString            the url
     * @param postParameters the parameters required to post data, can be null.
     * @param headers the headers parameters required for connecting with server, can be null.
     * @return the pair,  the response in Pair format.
     * @see Pair
     */
    fun sendHttpPost(
        urlString: String,
        postParameters: HashMap<String, Any>?,
        headers : HashMap<String, Any?>?
    ): Pair<String, Any> {
        val dataReceived = StringBuffer()

        requireNotNull(postParameters) { "Post parameters can not be null" }

        try {
            val url = URL(urlString)

            Util.trace("Url ", "Url : $url")

            var conn = url.openConnection() as HttpURLConnection?
            if(headers !== null)
            {
                conn = getHttpConnectionWitHeader(conn, headers)
            }

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "POST"
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn?.setRequestProperty("Accept", "application/json")

            conn?.doInput = true
            conn?.doOutput = true

            val writer = conn?.outputStream
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer?.write(getPostString(postParameters).toByteArray())
            writer?.flush()
            writer?.close()

            conn?.connect()


            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage


            if (responseCode != 200) {
                return if (responseMsg!!.isNotEmpty())
                    Pair.create(NetworkConstants.NETWORK_ERROR, responseMsg)
                else
                    Pair.create(NetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR)

            }

         //   Util.trace("Response Code $responseCode")

            var bi = BufferedInputStream(conn?.inputStream)

            val buffer = ByteArray(1024)

            var ch: Int
            do
            {

                ch = bi.read(buffer)
                if(ch!=-1)
                    dataReceived.append(String(buffer, 0, ch))
            } while(ch !=-1)


            bi.close()
            conn?.disconnect()

           // Util.trace("Response Received", " = $dataReceived")

            return Pair.create(NetworkConstants.NETWORK_SUCCESS, dataReceived.toString())


        } catch (t: Throwable) {
            Log.e("Error ", " = $t")
            t.printStackTrace()
            return Pair.create(NetworkConstants.NETWORK_ERROR, t.toString())
        }

    }



    /**
     * Method to call POST method with or without parameters for HTTPS Urls, used tio download
     * file from given url.
     *
     * @param urlString            the url
     * @param postParameters the parameters required to post data, can be null.
     * @param headers the headers parameters required for connecting with server, can be null.
     * @return the pair,  the response in Pair format. pair.second contains path of file location
     * @see Pair
     */
    fun getFileHttpsPost(
        context: Context,
        urlString: String,
        postParameters: HashMap<String, Any>?,
        headers : HashMap<String, Any?>?
    ): Pair<String, Any> {


        requireNotNull(postParameters) { "Post parameters can not be null" }

        try {
            val url = URL(urlString)

            Util.trace("Url ", "Url : $url")



            var conn = url.openConnection() as HttpsURLConnection?
            if(headers !== null)
            {
                conn = getHttpsConnectionWitHeader(conn, headers)
            }

            val sslsocketfactory: SSLSocketFactory =
                SSLSocketFactory.getDefault() as SSLSocketFactory
            conn?.sslSocketFactory = sslsocketfactory

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "POST"
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn?.setRequestProperty("Accept", "application/json")

            conn?.doInput = true
            conn?.doOutput = true

            val writer = conn?.outputStream
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer?.write(getPostString(postParameters).toByteArray())
            writer?.flush()
            writer?.close()

            conn?.connect()


            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage

            if (responseCode != 200) {
                return if (responseMsg!!.isNotEmpty())
                    Pair.create(NetworkConstants.NETWORK_ERROR, responseMsg)
                else
                    Pair.create(NetworkConstants.NETWORK_ERROR, INTERNAL_NETWORK_ERROR)

            }

          //  Util.trace("Header is " +conn?.getHeaderField("Content-Type"))
           // Util.trace("Disposition " +conn?.getHeaderField("Content-Disposition"))

           // Util.trace("Response Code $responseCode")

            var bi = BufferedInputStream(conn?.inputStream)

            val buffer = ByteArray(1024)

            var fileCache = FileCache()
            fileCache.initPrivate(context)
            var file: File? = fileCache.getFile(getFileNameFromContentType(""+conn?.getHeaderField("Content-Type")))

            var bos : FileOutputStream = FileOutputStream(file)


            var ch: Int
            do {
                ch = bi.read(buffer)
                if (ch != -1) {
                    bos.write(buffer,0,ch)
                }
            } while (ch != -1)


            bi.close()
            bos.close()

            conn?.disconnect()

           // Util.trace("Response Received", " = ok " + file?.path)

            return Pair.create(NetworkConstants.NETWORK_SUCCESS, file?.path)


        } catch (t: Throwable) {
            Log.e("Error ", " = $t")
            t.printStackTrace()
            return Pair.create(NetworkConstants.NETWORK_ERROR, t.toString())
        }
    }


    /**
     * Method to call url From HTTPS protocol with JSON request, with or without parameters.
     *
     * @param urlString  the url
     * @param jsonData   the JSON string as a request, can be null.
     * @return the pair,  the response in Pair format.
     * @see android.util.Pair
     */
    fun sendHttpsPutJSON( urlString: String?, jsonData: String? , headers : HashMap<String, Any?>? ): Pair<String, Any>?
    {
        val dataReceived = StringBuffer()
        requireNotNull(jsonData) { "Json data can not be null" }
        return try
        {
            val url = URL(urlString)
            Util.trace("Url ", "Url : $url")
            var conn = url.openConnection() as HttpsURLConnection?
            if(headers !== null)
            {
                conn = getHttpsConnectionWitHeader(conn, headers)
            }

            val sslsocketfactory: SSLSocketFactory =
                SSLSocketFactory.getDefault() as SSLSocketFactory
            conn?.sslSocketFactory = sslsocketfactory

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "PUT"

            conn?.setRequestProperty("Content-Type", "application/json")

            conn?.doInput = true
            conn?.doOutput = true
            val writer    = conn?.outputStream
            writer?.write(jsonData.toByteArray())
            writer?.flush()
            writer?.close()
            conn?.connect()
            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage
            if (responseCode != 200) {
                return if (responseMsg?.length!! > 0) Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    responseMsg
                ) else Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    INTERNAL_NETWORK_ERROR
                )
            }
            //Util.trace("Response Code $responseCode")
            val bi = BufferedInputStream(conn?.inputStream)
            val buffer = ByteArray(1024)
            var ch: Int
            while (bi.read(buffer).also { ch = it } != -1) {
                dataReceived.append(String(buffer, 0, ch))
            }
           // Util.trace("Received", "" + dataReceived.toString())
            bi.close()
            conn?.disconnect()
            Pair.create(
                NetworkConstants.NETWORK_SUCCESS,
                dataReceived.toString()
            )
        } catch (t: Throwable) {
            Util.trace("Error ", " = $t")
            t.printStackTrace()
            Pair.create(
                NetworkConstants.NETWORK_ERROR,
                t.toString()
            )
        }
    }


    /**
     * Method to call url From HTTPS protocol with JSON request, with or without parameters.
     *
     * @param urlString  the url
     * @param jsonData   the JSON string as a request, can be null.
     * @return the pair,  the response in Pair format.
     * @see android.util.Pair
     */
    fun sendHttpsPostJSON( urlString: String?, jsonData: String? , headers : HashMap<String, Any?>? ): Pair<String, Any>?
    {
        val dataReceived = StringBuffer()
        requireNotNull(jsonData) { "Json data can not be null" }
        return try
        {
            val url = URL(urlString)
            Util.trace("Url ", "Url : $url")
            var conn = url.openConnection() as HttpsURLConnection?
            if(headers !== null)
            {
                conn = getHttpsConnectionWitHeader(conn, headers)
            }

            val sslsocketfactory: SSLSocketFactory =
                SSLSocketFactory.getDefault() as SSLSocketFactory
            conn?.sslSocketFactory = sslsocketfactory

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "POST"

            conn?.setRequestProperty("Content-Type", "application/json")

            conn?.doInput = true
            conn?.doOutput = true
            val writer    = conn?.outputStream
            writer?.write(jsonData.toByteArray())
            writer?.flush()
            writer?.close()
            conn?.connect()
            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage
            if (responseCode != 200) {
                return if (responseMsg?.length!! > 0) Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    responseMsg
                ) else Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    INTERNAL_NETWORK_ERROR
                )
            }
            //Util.trace("Response Code $responseCode")
            val bi = BufferedInputStream(conn?.inputStream)
            val buffer = ByteArray(1024)
            var ch: Int
            while (bi.read(buffer).also { ch = it } != -1) {
                dataReceived.append(String(buffer, 0, ch))
            }
            Util.trace("Received", "" + dataReceived.toString())
            bi.close()
            conn?.disconnect()
            Pair.create(
                NetworkConstants.NETWORK_SUCCESS,
                dataReceived.toString()
            )
        } catch (t: Throwable) {
            Util.trace("Error ", " = $t")
            t.printStackTrace()
            Pair.create(
                NetworkConstants.NETWORK_ERROR,
                t.toString()
            )
        }
    }
    /**
     * Method to call url From HTTP protocol with JSON request, with or without parameters.
     *
     * @param urlString  the url
     * @param jsonData   the JSON string as a request, can be null.
     * @return the pair,  the response in Pair format.
     * @see android.util.Pair
     */
    fun sendHttpPutJSON( urlString: String?, jsonData: String? , headers : HashMap<String, Any?>? ): Pair<String, Any>?
    {
        val dataReceived = StringBuffer()
        requireNotNull(jsonData) { "Json data can not be null" }
        return try
        {
            val url = URL(urlString)
            Util.trace("Url ", "Url : $url")
            var conn = url.openConnection() as HttpURLConnection?
            if(headers !== null)
            {
                conn = getHttpConnectionWitHeader(conn, headers)
            }

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "PUT"

            conn?.setRequestProperty("Content-Type", "application/json")

            conn?.doInput = true
            conn?.doOutput = true
            val writer    = conn?.outputStream
            writer?.write(jsonData.toByteArray())
            writer?.flush()
            writer?.close()
            conn?.connect()
            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage
            if (responseCode != 200) {
                return if (responseMsg?.length!! > 0) Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    responseMsg
                ) else Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    INTERNAL_NETWORK_ERROR
                )
            }
           // Util.trace("Response Code $responseCode")
            val bi = BufferedInputStream(conn?.inputStream)
            val buffer = ByteArray(1024)
            var ch: Int
            while (bi.read(buffer).also { ch = it } != -1) {
                dataReceived.append(String(buffer, 0, ch))
            }
            Util.trace("Received", "" + dataReceived.toString())
            bi.close()
            conn?.disconnect()
            Pair.create(
                NetworkConstants.NETWORK_SUCCESS,
                dataReceived.toString()
            )
        } catch (t: Throwable) {
            Util.trace("Error ", " = $t")
            t.printStackTrace()
            Pair.create(
                NetworkConstants.NETWORK_ERROR,
                t.toString()
            )
        }
    }

    /**
     * Method to call url From HTTP protocol with JSON request, with or without parameters.
     *
     * @param urlString  the url
     * @param jsonData   the JSON string as a request, can be null.
     * @return the pair,  the response in Pair format.
     * @see android.util.Pair
     */
    fun sendHttpPostJSON( urlString: String?, jsonData: String? , headers : HashMap<String, Any?>? ): Pair<String, Any>?
    {
        val dataReceived = StringBuffer()
        requireNotNull(jsonData) { "Json data can not be null" }
        return try
        {
            val url = URL(urlString)
            Util.trace("Url ", "Url : $url")
            var conn = url.openConnection() as HttpURLConnection?
            if(headers !== null)
            {
                conn = getHttpConnectionWitHeader(conn, headers)
            }

            conn?.readTimeout = READ_TIME_OUT
            conn?.connectTimeout = CONNECTION_TIME_OUT
            conn?.requestMethod = "POST"

            conn?.setRequestProperty("Content-Type", "application/json")

            conn?.doInput = true
            conn?.doOutput = true
            val writer    = conn?.outputStream
            writer?.write(jsonData.toByteArray())
            writer?.flush()
            writer?.close()
            conn?.connect()
            val responseCode = conn?.responseCode
            val responseMsg = conn?.responseMessage
            if (responseCode != 200) {
                return if (responseMsg?.length!! > 0) Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    responseMsg
                ) else Pair.create(
                    NetworkConstants.NETWORK_ERROR,
                    INTERNAL_NETWORK_ERROR
                )
            }
            // Util.trace("Response Code $responseCode")
            val bi = BufferedInputStream(conn?.inputStream)
            val buffer = ByteArray(1024)
            var ch: Int
            while (bi.read(buffer).also { ch = it } != -1) {
                dataReceived.append(String(buffer, 0, ch))
            }
            Util.trace("Received", "" + dataReceived.toString())
            bi.close()
            conn?.disconnect()
            Pair.create(
                NetworkConstants.NETWORK_SUCCESS,
                dataReceived.toString()
            )
        } catch (t: Throwable) {
            Util.trace("Error ", " = $t")
            t.printStackTrace()
            Pair.create(
                NetworkConstants.NETWORK_ERROR,
                t.toString()
            )
        }
    }


    /**
     * Method returns a single string consist of Post parameters from given HashMap.
     */
    @Throws(UnsupportedEncodingException::class)
    private fun getPostString(parameters: HashMap<String, Any>?): String {
        val result = StringBuilder()

        if (parameters != null) {
            var i = 0

            for ((key, value) in parameters) {

                if (i == 0) {
                    i++

                } else {
                    result.append("&")
                }

                result.append(URLEncoder.encode(key, "UTF-8"))
                result.append("=")
                result.append(URLEncoder.encode(value.toString(), "UTF-8"))

                /*result.append(key);
                result.append("=");
                result.append(value);*/
            }
        }


        return result.toString()
    }

    /**
     * Method returns a single string consist of GET parameters from given HashMap.
     */
    @Throws(UnsupportedEncodingException::class)
    fun getQueryString(parameters: HashMap<String, Any>?): String {
        val result = StringBuilder()

        if (parameters != null) {

            //Set keys = this.parameters.keySet();

            var i = 0

            for ((key, value) in parameters) {

                if (i == 0) {
                    result.append("?$key=$value")
                    i++
                } else {
                    result.append("&$key=$value")
                }
            }
        }
        return result.toString()
    }

    /**
     * function returns object of HttpsUrlConnection by adding headers int it.
     *
     * @param conn : HtttpsUrlConnection which needs to add custom headers
     * @param headers : Headers which are going to append with connection example : 'CapellaAuthToken' etc.
     * @return : HttpsUrlConnection with added headers on it
     */
    private fun getHttpsConnectionWitHeader(conn: HttpsURLConnection? , headers: HashMap<String, Any?>?): HttpsURLConnection? {


        if (headers != null)
        {
            for ((key, value) in headers) {

                conn?.setRequestProperty(key , value.toString())

            }
        }
        return conn
    }

    /**
     * function returns object of HttpUrlConnection by adding headers int it.
     *
     * @param conn : HtttpUrlConnection which needs to add custom headers
     * @param headers : Headers which are going to append with connection example : 'CapellaAuthToken' etc.
     * @return : HttpUrlConnection with added headers on it
     */
    private fun getHttpConnectionWitHeader(conn: HttpURLConnection? , headers: HashMap<String, Any?>?): HttpURLConnection? {


        if (headers != null)
        {
            for ((key, value) in headers) {

                conn?.setRequestProperty(key , value.toString())

            }
        }
        return conn
    }

    /**
     * Method creates random file name from current date and time, file extension is extracted from
     * content type.
     *
     *
     * @param contentType : content type received from server whether its jpeg, png or others.
     * @return : file name with file extension.
     */
    private fun getFileNameFromContentType(contentType :String) : String
    {
        val dat =  Date()
        try
        {
            val fName = dat.toString().replace("\\s".toRegex(), "")

            return ""+fName+ "."+contentType.split(";")[0].split("/")[1]
        }catch (t: Throwable)
        {
            Util.trace("File Name error : " + t.toString())
        }


        return "$dat"
    }



}
