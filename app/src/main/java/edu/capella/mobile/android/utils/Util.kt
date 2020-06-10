package edu.capella.mobile.android.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import edu.capella.mobile.android.R
import edu.capella.mobile.android.adapters.GradeAssignmentListAdapter
import edu.capella.mobile.android.interfaces.EventListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Util.kt : A Util class to provide certain common utility functions.
 *
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 */
object Util {
    /**
     * Shows LOG with tag and message.
     *
     * @param tag     the tag
     * @param message the message
     */
    fun trace(tag: String?, message: String?) {
      // Log.e(tag, message.toString())
    }

    private const val CAPELLA_DIRECTORY_NAME = "CapellaCache"
    /**
     * Shows LOG without tag
     *
     * @param message the message
     */
    fun trace(message: String?) {
     // Log.e("General", message.toString())
    }

    /**
     * Checks whether internet is available on phone or not.
     *
     * @param context the context
     * @return the boolean
     */
//    fun isInternetAvailable(context: Context): Boolean {
//        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (connectivity != null) {
//            val info = connectivity.allNetworkInfo
//            if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
//                trace(
//                    "Network",
//                    "NETWORKnAME: " + info[i].typeName
//                )
//                return true
//            }
//        }
//        return false
//    }

    fun showSnakeBar(message: String?, view: View, duration: Int) {
        val snakeBar = Snackbar.make(view, message!!, duration)

        val view = snakeBar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
/*        params.leftMargin = view.resources.getDimension(R.dimen._8dp).toInt()
        params.rightMargin = view.resources.getDimension(R.dimen._8dp).toInt()
        params.bottomMargin = view.resources.getDimension(R.dimen._19dp).toInt()*/

        snakeBar.show()

        announceAda(message, view.context )

    }

    fun showCustomSnakeBar(context:Context,message: String?, view: View, duration: Int) {
        val snackbar =
            Snackbar.make(view, message!!,duration)
        snackbar.view.layoutParams.width =     ViewGroup.LayoutParams.MATCH_PARENT
        try {
            val textView = snackbar.view.findViewById(R.id.snackbar_text) as TextView
            textView.visibility = View.INVISIBLE

            var mInflater = LayoutInflater.from(context)
            var snackView = mInflater.inflate(R.layout.custom_snackbar, null)

            var txtView = snackView.findViewById<TextView>(R.id.custom_snackbar_text)
            txtView.text = message

            var layout = snackbar.getView() as Snackbar.SnackbarLayout
            layout.addView(snackView)
        }catch (t:Throwable){}

        snackbar.show()
        announceAda(message, view.context )
    }

    fun isAlphaNumeric(chars: String): Boolean {
//        return chars.matches("\\d+".toRegex())
        return chars.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    public fun openPlayStore(context: Context) {
        val appPackageName =
           context.packageName // getPackageName() from Context or Activity object

        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=edu.capella.mobile.android&hl=en")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=edu.capella.mobile.android&hl=en")
                )
            )
        }
    }

    public fun openPlayStoreLink(context: Context) {
        val appPackageName =
            context.packageName // getPackageName() from Context or Activity object
        Uri.parse("https://play.google.com/store/apps/details?id=edu.capella.mobile.android&hl=en")
    }

    fun str(string: String?): String {
        return if (string == null || string == "null") {
            ""
        } else {
            string.toString()
        }

    }

    fun stringToWords(mnemonic: String): List<String> {
        val words = ArrayList<String>()
        for (w in mnemonic.trim().split(",")) {
            if (w.isNotEmpty()) {
                words.add(w)
            }
        }
        return words
    }

    fun getDateForCST(
        milliSeconds: Long,
        dateFormat: String?
    ): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun convertDateLocalToCST(strDate: String?): String? {
        var cstDate=""
        try {

            val sSdf =
                SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.ENGLISH)

            sSdf.timeZone = TimeZone.getDefault()
            val dt = sSdf.parse(strDate)

            // This prints: Date with default formatter: 2013-03-14 22:00:12 PDT
            // As my machine is in PDT time zone
            // This prints: Date with default formatter: 2013-03-14 22:00:12 PDT
            // As my machine is in PDT time zone
            println(
                "Date with default formatter: " + SimpleDateFormat("MM/dd/yyyy hh:mm a").format(
                    dt
                )
            )

            // This prints: Date with IST time zone formatter: 2013-03-15 10:30:12 GMT+05:30
            // This prints: Date with IST time zone formatter: 2013-03-15 10:30:12 GMT+05:30
            val sdf =
                SimpleDateFormat("MM/dd/yyyy hh:mm a")
            var tz = TimeZone.getTimeZone("GMT+0530")
            sdf.timeZone = tz
            val dateIST = sdf.format(dt)
            println("Date with IST time zone formatter: $dateIST")

            // This prints: Date CST time zone formatter: 2013-03-15 00:00:12 CDT
            // This prints: Date CST time zone formatter: 2013-03-15 00:00:12 CDT
            tz = TimeZone.getTimeZone("America/Chicago")
            sdf.timeZone = tz
            println("Date CST time zone formatter: " + sdf.format(dt))

            cstDate= sdf.format(dt).replace("AM", "a.m.").replace("PM", "p.m.")


        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        return cstDate
    }


    /**
     * Checks whether internet is available on phone or not.
     *
     * @param context the context
     * @return the boolean
     */

    fun isInternetOn(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
                }
            } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                /**
                 * WE are not supporting Below MARSHMELLOW but still we have given support to
                 * old Android version, not removing deprecated code from here : JAYESH
                 * */
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * A static method shows confirmation popup for opening given url in
     * external browser.
     *
     * @param context : context of any activity
     * @param url : String url which needs to open in external browser.
     */
    fun openBrowserWithConfirmationPopup(context: Context, url: String) {
        DialogUtils.showDialogOnGoOutSide(context, object : EventListener {
            override fun confirm() {
                super.confirm()

                try {

                        openExternalBrowser(context, url)

                }catch (t:Throwable)
                {

                }
            }

            override fun cancel() {
                super.cancel()
            }

        })
    }

    fun openExternalBrowser(context: Context, url: String) {
        Preferences.addBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE, true)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }


    fun openBrowserWithConfirmationPopupNew(context: Context, listener: EventListener) {
        DialogUtils.showDialogOnGoOutSide(context, listener)
    }

    fun openBrowserWithConfirmationPopupNew(context: Context, listener: EventListener, customMessage:String) {
        DialogUtils.showDialogOnGoOutSide(context, listener,customMessage)
    }



    /**
     * Method responsible for opening Browser apps availabe over device with
     * given url.
     *
     * @param context
     * @param url
     */
    fun openUrlBrowser(context: Context, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    /**
     * Method responsible for opening native phone dialer with given number.
     * @param context : Context of an activity.
     * @param numTODial : number which needs to dial.
     */
    fun dialNumber(context: Context, numTODial: String) {
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(numTODial)))
            context.startActivity(dialIntent)

        } catch (th: Throwable) {
            // TO-DO
        }
    }

    /**
     * Default directory of app to create and read files.
     *
     * @return the biz now dir
     */
    fun getCapellaDir(): File? {
        val mediaStorageDir =
            File(Environment.getExternalStorageDirectory(), CAPELLA_DIRECTORY_NAME)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        return mediaStorageDir
    }


    fun dayAgo(createTimeStr: String, context: Context): Int {
        var remainingDay: Int = 0
        var createTimeStr = createTimeStr
        try {
            val f = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val tz = TimeZone.getDefault()
            simpleDateFormat.timeZone = tz
            val d = simpleDateFormat.parse(createTimeStr)
            val currentDateandTime = f.format(Date())
            val d1 = f.parse(currentDateandTime)
            val milliseconds = d.time
            val millisecondsCurrent = d1.time
            val diff_Milli = millisecondsCurrent - milliseconds
            var minutes = Math.abs((millisecondsCurrent - milliseconds) / 60000)
            val seconds = Math.abs(diff_Milli / 1000)
            var hours = Math.abs(minutes / 60)
            val days = Math.abs(hours / 24)
            val weeks = Math.abs(days / 7)
            val months = Math.abs(days / 30)
            val years = Math.abs(months / 12)

            val calculateday = TimeUnit.SECONDS.toDays(seconds);
            remainingDay = calculateday.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            createTimeStr = ""
        }

        return remainingDay

    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    fun getDate(
        milliSeconds: Long,
        dateFormat: String?
    ): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time).replace("AM", "a.m.").replace("PM", "p.m.")
    }

    fun stringToArrayList(s: String) = s.trim().splitToSequence(',')
        .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
        .toList()

    fun getDateNotPMAM(
        milliSeconds: Long,
        dateFormat: String?
    ): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }


    fun getDateOject(dtStart: String): Date {
        var date = Date()
        val format =
            SimpleDateFormat("MM/dd/yyyy hh:mm a")
        try {
            date = format.parse(dtStart)
            return date
            System.out.println(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }


  /*  fun getDateForGrade(dateInput: String, dateOutput: String, _date: String): String {
        var date = Date()
        val format = SimpleDateFormat("MM/dd/yyyy'T'hh:mm a", Locale.ENGLISH)
        try {
            date = format.parse(_date)

            val outputFormat = SimpleDateFormat(dateOutput, Locale.ENGLISH)
            outputFormat.timeZone = TimeZone.getDefault()
            // val date = inputFormat.parse(_date)
            return outputFormat.format(date!!)

            //  return  date.toString()

        } catch (e: Exception) {
            Util.trace("Error dt : $e")
            e.printStackTrace()
        }
        return date.toString()
    }*/

   /* fun convertDateStringFormat(
        strDate: String?,
        fromFormat: String?,
        toFormat: String
    ): String? {

        Util.trace("Grade Date : " + strDate)

        return try {
            *//*val sdf = SimpleDateFormat(fromFormat)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormat2 =
                SimpleDateFormat(toFormat.trim { it <= ' ' })
            dateFormat2.format(sdf.parse(strDate))*//*

            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            return format.parse(strDate.toString()).toString()

        } catch (e: java.lang.Exception) {
            Util.trace("Error dt : $e")
            e.printStackTrace()
            ""
        }
    }

    fun ISO8601FormatDate(sDate: String): String {

        var result = ""

        try {

            var date = sDate.split("T")[0];
            var time = sDate.split("T")[1];

            var dateSplit = date.split("-");
            var year = dateSplit[0];
            var month = dateSplit[1];
            var day = dateSplit[2];

            if (day.length != 2) day = '0' + day;
            if (month.length != 2) month = '0' + month;

            result = year + '-' + month + '-' + day + 'T' + time;

        } catch (e: Exception) {
            Util.trace("Iso error $e")
            result = "";
        }

        return result;
    }*/

    fun formatDateTimeNew(sDate: String, weekday: Boolean, grade: Boolean): String {
        try {


            var c = Calendar.getInstance()


            var date = sDate.split("T")[0];
            var time = sDate.split("T")[1];
            time = time.substring(0, time.indexOf("-"))

            var dateSplit = date.split("-");
            var year = dateSplit[0];
            var month = dateSplit[1];
            var day = dateSplit[2];


            var hrmn = time.split(":")

            var hours = hrmn[0].toInt()
            var minutes = hrmn[1]

            c.set(year.toInt(), month.toInt(), day.toInt(), hours.toInt(), minutes.toInt())

            var ampm = ""
            ampm = if (hours >= 12) {
                "p.m."
            } else {
                "a.m."
            }

            hours = hours.rem(12)
            if (hours == 0)
                hours = 12
            //hours = hours ? hours : 12; // the hour '0' should be '12'
            /* if(minutes<10)
            minutes =  '0'+minutes
*/
            // minutes = minutes < 10 ? '0'+minutes : minutes;

            var weekdays = arrayOf<String>(
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
            )
            val dOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1
            var dayName = weekdays[dOfWeek];

            var monthNames = arrayOf<String>(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )


            var monthName = monthNames[c.get(Calendar.MONTH) - 1]

            var strTime = ""


            if (weekday) {
                strTime =
                    day + ' ' + (monthNames[Calendar.MONTH]) + "/" + c.get(Calendar.DATE) + "/" + c.get(
                        Calendar.YEAR
                    ) + " at " + hours + ':' + minutes + ' ' + ampm;
            } else if (grade) {
                strTime =
                    monthName + ' ' + c.get(Calendar.DATE) + ", " + c.get(Calendar.YEAR) + " at " + hours + ':' + minutes + ' ' + ampm;
            } else {
                strTime =
                    "" + (c.get(Calendar.MONTH)) + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR) + "  " + hours + ':' + minutes + ' ' + ampm;
            }
            return strTime;
        } catch (T: Throwable) {
            Util.trace("Formate Date Error $T for $sDate")
            T.printStackTrace()
        }
        return ""
    };


    fun formatDateNew(sDate: String): String {
        try {


            var c = Calendar.getInstance()


            var date = sDate.split("T")[0]
            var time = sDate.split("T")[1]
            time = time.substring(0, time.indexOf("-"))

            var dateSplit = date.split("-")
            var year = dateSplit[0]
            var month = dateSplit[1]
            var day = dateSplit[2]


            var hrmn = time.split(":")

            var hours = hrmn[0].toInt()
            var minutes = hrmn[1]

            c.set(year.toInt(), month.toInt(), day.toInt(), hours.toInt(), minutes.toInt())


            var strTime = ""


            strTime =
                "" + (c.get(Calendar.MONTH)) + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR)
            return strTime;
        } catch (T: Throwable) {
            Util.trace("Formate Date Error $T for $sDate")
            T.printStackTrace()
        }
        return ""
    }

    fun formatDateStringInDate(sDate: String): String {
        try {


            var c = Calendar.getInstance()


            var date = sDate.split("T")[0]
            var time = sDate.split("T")[1]
            time = time.substring(0, time.indexOf("-"))

            var dateSplit = date.split("-")
            var year = dateSplit[0]
            var month = dateSplit[1]
            var day = dateSplit[2]


            var hrmn = time.split(":")

            var hours = hrmn[0].toInt()
            var minutes = hrmn[1]

            c.set(year.toInt(), month.toInt(), day.toInt(), hours.toInt(), minutes.toInt())


            var strTime = ""


            strTime =
                "" + month + "/" + day + "/" + year
            return strTime;
        } catch (T: Throwable) {
            Util.trace("Formate Date Error $T for $sDate")
            T.printStackTrace()
        }
        return ""
    }

    fun getTwoDigitNumber(grad: String): String {
        try {
            if (grad.contains(".")) {
                var g = grad.split(".")
                if (g[1].length == 1) {
                    return grad + "0"
                } else if (g[1].length > 2) {
                    return g[0] + "." + g[1].substring(0, 2)
                } else {
                    return grad
                }
            } else {
                return "$grad.00"
            }

        } catch (t: Throwable) {
            return grad
        }
    }

    fun getDateAgo(createdAt: String): Long {

        var date = createdAt.split("T")[0];
        var time = createdAt.split("T")[1];
        time = time.substring(0, time.indexOf("-"))

        var dateSplit = date.split("-");
        var year = dateSplit[0];
        var month = dateSplit[1];
        var day = dateSplit[2];


        var hrmn = time.split(":")

        var hours = hrmn[0]
        var minutes = hrmn[1]

        var sDate = "$year-$month-$day" + "T" + "$hours:$minutes:00"

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date: Date = sdf.parse(sDate)
            val now = Date(System.currentTimeMillis())
            val days = getDateDiff(date, now, TimeUnit.DAYS)
            return days
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 100
    }

    fun getDateToLong(createdAt: String): Long? {

        var date = createdAt.split("T")[0];
        var time = createdAt.split("T")[1];
        time = time.substring(0, time.indexOf("-"))

        var dateSplit = date.split("-");
        var year = dateSplit[0];
        var month = dateSplit[1];
        var day = dateSplit[2];


        var hrmn = time.split(":")

        var hours = hrmn[0]
        var minutes = hrmn[1]
        var seconds = hrmn[2]

       // var sDate = "$year-$month-$day" + "T" + "$hours:$minutes:00"
        var sDate = "$year-$month-$day" + "T" + "$hours:$minutes:$seconds"

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date: Date = sdf.parse(sDate)

            return date.time
        } catch (e: java.lang.Exception) {
            Util.trace("Date error $e")
            e.printStackTrace()
        }
        return null
    }

    fun getRealDate(createdAt: String): Date? {

        var date = createdAt.split("T")[0];
        var time = createdAt.split("T")[1];
        time = time.substring(0, time.indexOf("-"))

        var dateSplit = date.split("-");
        var year = dateSplit[0];
        var month = dateSplit[1];
        var day = dateSplit[2];


        var hrmn = time.split(":")

        var hours = hrmn[0]
        var minutes = hrmn[1]
        var seconds = hrmn[2]

        // var sDate = "$year-$month-$day" + "T" + "$hours:$minutes:00"
        var sDate = "$year-$month-$day" + "T" + "$hours:$minutes:$seconds"

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date: Date = sdf.parse(sDate)

            return date
        } catch (e: java.lang.Exception) {
            Util.trace("Date error $e")
            e.printStackTrace()
        }
        return null
    }

    fun getDateAgoMins(createdAt: String): Long {

        var date = createdAt.split("T")[0];
        var time = createdAt.split("T")[1];
        time = time.substring(0, time.indexOf("-"))

        var dateSplit = date.split("-");
        var year = dateSplit[0];
        var month = dateSplit[1];
        var day = dateSplit[2];


        var hrmn = time.split(":")

        var hours = hrmn[0]
        var minutes = hrmn[1]

        var sDate = "$year-$month-$day" + "T" + "$hours:$minutes:00"

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date: Date = sdf.parse(sDate)
            val now = Date(System.currentTimeMillis())
            val mins = getDateDiff(date, now, TimeUnit.MINUTES)
            return mins
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Long.MAX_VALUE
    }

    fun getCurrentTime(): Long? {
        try {
            val now = Date(System.currentTimeMillis())
            return now.time
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getDifferentInMinutes(timeinmili: Long): Long? {
        try {
            val oldDate = Date(timeinmili)
            val now = Date(System.currentTimeMillis())
           /* val mins = getDateDiff(oldDate, now, TimeUnit.MINUTES)
            return mins*/
            val sec = getDateDiff(oldDate, now, TimeUnit.SECONDS)
            return sec
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun getDateDiff(
        date1: Date,
        date2: Date,
        timeUnit: TimeUnit
    ): Long {
        val diffInMillies = date2.time - date1.time
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    /**
     * check and return true if given string is url
     * @param stringValue text in String
     * @return boolean value
     */
    fun isUrl(stringValue: String): Boolean {
        return stringValue.contains("http") || stringValue.contains("https")
    }

    /**
     * check and return true if given string is email
     * @param stringValue text in String
     * @return boolean value
     */

    fun isEmail(stringValue: String): Boolean {
        return stringValue.contains("@")
    }

    fun getDateMMDDYYYY(date: String): String {
        try {
            var d = date.split("-")
            if (date.contains("T")) {
                d = date.substring(0, date.indexOf("T")).split("-")
            } else {
                d = date.split("-")
            }

            val formatedDate = "" + d[1] + "/" + d[2] + "/" + d[0]
            return formatedDate
        } catch (t: Throwable) {
            trace("Formated Date error : " + t.toString())
        }
        return date
    }

    fun showToastForUnderDevelopment(context: Context) {
        Toast.makeText(context, "Functionality under development ", Toast.LENGTH_SHORT).show()
    }

    fun getDifferenceBetweenDateIsMoreThan72Hours(dateInMilliSeconds: Long): Boolean {
        var shouldShow: Boolean = false

        val difference =
            System.currentTimeMillis() - dateInMilliSeconds

        val differenceInHours = TimeUnit.MILLISECONDS.toHours(difference)
        shouldShow = differenceInHours <= 72

        return shouldShow
    }

    fun findURLinHREF(string:String?) :Boolean
    {
        var str =""

        if(string==null)
            return false
        else
            str= string

       if(str.indexOf("href=") != -1)
           return true

        return false

    }
   fun isHTMLTagFound(discussionText:String?) :Boolean
   {
       if(discussionText==null)
           return false

        var isHTMLFound=false
        var tags = arrayListOf<String>("<html>", "<head>", "<body>","<B>","<I>","<U>","<strong>","<h1>", "<h2>", "<h3>","<h4>","<h5>","<h6>","<em>","<a","<img>", "<area>", "<map>","<ul>","<ol>","<li>","<dl>","<dd>", "<dt>","<table>","<tr>","<th>","<td>","<span>","</span>","<p>","</p>","<p style>","<p","<span","<span style","<sub>","</sub>","<sup>","</sup>","<hr","<hr width","<hr>","</hr>")
        for(item in tags)
        {
            if (discussionText?.indexOf(item) != -1)
            {
                isHTMLFound=true;
                break;
            }
        }
        return isHTMLFound;
    }

    /**
     *  For this particular method <P> , </P> <P  is removed
     */
    fun isHTMLFoundForFormattedView(discussionText:String?) :Boolean
    {
        if(discussionText==null)
            return false

        var isHTMLFound=false
        var tags = arrayListOf<String>("<html>", "<head>", "<body>","<B>","<I>","<U>","<strong>","<h1>", "<h2>", "<h3>","<h4>","<h5>","<h6>","<em>","<a","<img>", "<area>", "<map>","<ul>","<ol>","<li>","<dl>","<dd>", "<dt>","<table>","<tr>","<th>","<td>","<span>","</span>", "<p style>", "<span","<span style","<sub>","</sub>","<sup>","</sup>","<hr","<hr width","<hr>","</hr>")
        for(item in tags)
        {
            if (discussionText?.indexOf(item) != -1)
            {
                isHTMLFound=true;
                break;
            }
        }
        return isHTMLFound;
    }

    fun extractHref(string: String?): String? {
        if (string == null)
            return null
        try {
            var str = string
            var parts = str?.split(" ")

            if (parts != null) {
                for (p in parts) {
                    if (p.indexOf("href", 0, true) != -1) {

                        var discussionRoomLink =
                            Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
                        var urlWithoutDomainName =
                            discussionRoomLink?.substring(8, discussionRoomLink.length);
                        var schoolName =
                            urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))

                        var view_description_link = "https://" + schoolName + "/" + p.substring(
                            p.lastIndexOf("@") + 1,
                            p.length - 1
                        )


                        var linkToOpen = view_description_link

                        Util.trace("linkToOpen : " + linkToOpen)
                        return linkToOpen

                        break
                    }
                }
            }


        } catch (t: Throwable) {
            Util.trace(" linkToOpen error $t")
        }
        return null
    }
    fun extractLastHref(string: String?): String? {
        if (string == null)
            return null
        try {
            var str = string
            var parts = str?.split(" ")
            var linkToOpen : String? = null
            if (parts != null) {
                for (p in parts) {
                    if (p.indexOf("href", 0, true) != -1) {

                        var discussionRoomLink =
                            Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
                        var urlWithoutDomainName =
                            discussionRoomLink?.substring(8, discussionRoomLink.length);
                        var schoolName =
                            urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))

                        var view_description_link = "https://" + schoolName + "/" + p.substring(
                            p.lastIndexOf("@") + 1,
                            p.length - 1
                        )


                        linkToOpen = view_description_link

                        Util.trace("linkToOpen : " + linkToOpen)



                    }
                }
            }

            return linkToOpen

        } catch (t: Throwable) {
            Util.trace(" linkToOpen error $t")
        }
        return null
    }

    fun extractHref_recursive(string: String?): String? {
        if (string == null)
            return null

        var linkToOpen: String? = null
        try {
            var str = string
            var parts = str?.split(" ")

            if (parts != null) {
                for (p in parts) {
                    if (p.indexOf("href", 0, true) != -1) {


                        linkToOpen = p.substring(p.lastIndexOf("@") + 1, p.length - 1)
                        linkToOpen = linkToOpen.replace("href=\"", "")

                        return linkToOpen


                    }
                }

                return linkToOpen
            }


        } catch (t: Throwable) {
            Util.trace(" linkToOpen error $t")
        }
        return null
    }

    @SuppressLint("NewApi")
    fun getDateInMiliSeconds(date: String): Long {
        val sdf: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy");

        val mDate: Date = sdf.parse(date);
        val timeInMilliseconds = mDate.time;
        return timeInMilliseconds
    }

    fun openEmail(context: Context, email: String) {
        var emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$email")
        context.startActivity(Intent.createChooser(emailIntent, ""));
    }

    fun removeHostName(url: String): String {
        var isHttp = false;
        var domain = ""
        var finalDomain = ""

        domain = url.substring(0, url.indexOf(':'));

        if (domain == "http") {
            finalDomain = url.replace("http://", "");
        } else {
            finalDomain = url.replace("https://", "");
        }
        return finalDomain;
    }

    fun getTrucatedString(stringData:String, maxCount:Int):String{
        return stringData.subSequence(0,maxCount).toString() + "..."

    }


    fun getHostName(url: String): String {
        var isHttp = false;
        var domain = ""
        var finalDomain = ""

        domain = url.substring(0, url.indexOf(':'));

        if (domain == "http") {
            finalDomain = url.replace("http://", "");
            finalDomain = finalDomain.substring(0, finalDomain.indexOf("/"))
            finalDomain = "http://$finalDomain"
        } else {
            finalDomain = url.replace("https://", "");
            finalDomain = finalDomain.substring(0, finalDomain.indexOf("/"))
            finalDomain = "https://$finalDomain"
        }



        return finalDomain;
    }


    fun findDomainByMessageLink(blackboardUrl: String): String {
        var blackboardKey = "/webapps"
        var schoolDomain = ""

        var searchTokenIndex = blackboardUrl.indexOf(blackboardKey)

        schoolDomain = blackboardUrl.substring(0, searchTokenIndex);

        return schoolDomain;
    }

    fun getWordsForMoreFeature(msg:String , charLimit : Int ):String
    {
        val stringBuffer  = StringBuffer()
          if(msg.length > charLimit)
          {
              val parts = msg.split(" ")

              for(p in parts)
              {
                  if(stringBuffer.toString().length>= charLimit)
                  {
                      return stringBuffer.toString()
                  }

                  stringBuffer.append(p + " ")
              }
          }
        return stringBuffer.toString()
    }
    fun getNonDecimal(string: String?): String?
    {
        try
        {
            if(string!!.indexOf(".")!=-1)
            {
                return string.substring(0, string.indexOf("."))
            }else
                return string
        }catch (t: Throwable)
        {
            return string
        }
    }
    fun removeCorruptString(inputData:String?) :String
    {
        try
        {
            if(inputData == null)
            {
                return ""
            }
            var input = inputData.trim();
            var output = "";
            for ((index, char) in input.withIndex())
            {
                if (char.toInt() <= 127)
                {
                    output += char
                }
            }

            return output
        }catch (t:Throwable)
        {
            trace("Error currupt :" + t)
            t.printStackTrace()
        }
        return inputData!!
    }

    fun getNonParaBodyText( text : String):String
    {
            if(text==null)
                return text

          if(text.indexOf("<p>" ) != -1 )
          {
              var str = text.substring(text.indexOf("<p>" )  , text.lastIndexOf("</p>" ))
              return str
          }else{
              return text
          }
    }

    fun showSessionTimeout(context: Context,listener: EventListener)
    {
        DialogUtils.showSessionTimeout(context  , listener)
    }

    fun isDesiredStatus(status:String? ): Boolean
    {
        if(status == null)
            return false

        if (status.contains("GRADED", true))
        {
            return true
        }
        else if (status.contains("LATE", true))
        {
            return true
        }
        else if (status.contains("SUBMITTED", true))
        {
            return true
        }else if ( status.contains("is due this week", true))
        {
            return true
        }
        return false
    }

  fun openPlainUrl(globalContext: Context ,plainUrlToOpen:String)
  {
      Util.openBrowserWithConfirmationPopupNew(globalContext , object :EventListener{
          override fun confirm() {
              super.confirm()
              Util.openExternalBrowser(globalContext, plainUrlToOpen)

          }

          override fun cancel() {
              super.cancel()
          }

      } )
  }
    fun announceAda(contentDescription : String , context: Context)
    {
        try {
            val manager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            if (manager.isEnabled) {
                val e = AccessibilityEvent.obtain()
                e.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                e.className = javaClass.name
                e.packageName = context.packageName
                e.text.add(contentDescription)
                manager.sendAccessibilityEvent(e)
            }
        }catch (t:Throwable){
            Util.trace("Global Issue speaking x")
        }
    }

    fun isAdaEnabled(context: Context) : Boolean
    {
        try {
            val manager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
           return manager.isEnabled
        }catch (t:Throwable){
            Util.trace("Global Issue speaking x")
        }
        return false
    }

    fun findURL_list_inHREF(contents:String):ArrayList<String>?
    {
        try {
            var parts = contents.split(" ")
            var urls: ArrayList<String> = ArrayList<String>()
            for (items in parts) {
                if (items.contains("href=")) {
                    var url = items.replace("href=", "")
                    url = url.replace("\"", "")

                   urls.add(url)
                }
            }
            return urls
        }catch (t:Throwable)
        {}
        return null
    }


     fun extractHrefForViewDescription(string: String?) {
        Preferences.removeKey(PreferenceKeys.VIEW_DESCRIPTION_LINK_PATH.toString())
        try {
            var str = string
            var parts = str?.split(" ")

            if (parts != null) {
                for (p in parts) {
                    if (p.indexOf("href", 0, true) != -1) {
                        Util.trace("HREF : " + p)

                        var discussionRoomLink =
                            Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
                        var urlWithoutDomainName =
                            discussionRoomLink?.substring(8, discussionRoomLink.length);
                        var schoolName =
                            urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))

                        var pr = p
                        if(pr.indexOf(">") != -1)
                        {
                            pr = pr.substring(0, pr.indexOf(">"))
                        }

                        var view_description_link = "https://" + schoolName + "/" + pr.substring(
                            pr.lastIndexOf("@") + 1,
                            pr.length - 1
                        )

                        Util.trace("view_description_link : " + view_description_link)

                        Preferences.addValue(
                            PreferenceKeys.VIEW_DESCRIPTION_LINK_PATH,
                            view_description_link
                        )

                        break
                    }
                }
            }


        } catch (t: Throwable) {
            Util.trace(" setting VIEW_DESCRIPTION_LINK_PATH error $t")
        }
    }
    fun replaceFullStop(string:String):String
    {
        try{
            return string.replace("." , "")
        }catch (t:Throwable){}

        return string
    }


    fun findBlackboardDomain(blackboardUrl:String): String
    {
        try {
            var blackboardIdentifier = "/bbcswebdav"

            var schoolDomain = ""

            var searchTokenIndex = blackboardUrl.indexOf(blackboardIdentifier);

            schoolDomain = blackboardUrl.substring(0, searchTokenIndex)

            return schoolDomain
        }catch (t:Throwable){}
        return blackboardUrl
    }

    fun getDummyDate(): String
    {

        try
        {
            var c = Calendar.getInstance()

            var d =  ""+ (c.get(Calendar.MONTH) +1) + "-" + c.get(Calendar.DATE)

            return d

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun isDesiredStatusWithDate(status:String?  , row : GradeAssignmentListAdapter.GradeCollector): Boolean
    {
        if(status == null)
            return false

        if (status.contains("GRADED", true))
        {
            return row.gradedDate!=null
        }
        else if (status.contains("LATE", true))
        {
            return row.statusDate!=null
        }
        else if (status.contains("SUBMITTED", true))
        {
            return row.submittedDate!=null
        }else if ( status.contains("is due this week", true))
        {
            return row.statusDate!=null
        }
        return false
    }
}
