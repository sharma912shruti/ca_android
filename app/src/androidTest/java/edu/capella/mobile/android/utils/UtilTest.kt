package edu.capella.mobile.android.utils

import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.activity.LoginActivity
import org.junit.Assert
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import java.util.*

/**
 * Class Name.kt : class description goes here
 *
 * @param param1
 *
 *Constructor Param1 Description Goes here
 * @param param2
 *
 *Constructor Param2 Description Goes here
 * @author :  jayesh.lahare
 * @version :
 * @since :  4/25/2020
 */
class UtilTest {


    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    lateinit var baseActivity: Activity

    var dummyDate  = "2020-04-22T23:59:00-05:00"

    @Before
    fun init() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        baseActivity = activityRule.activity

        Preferences.getInstance(context)
    }



    @Test
    fun getDate()
    {
        Assert.assertNotNull(Util.getDate(Date().time,Constants.DATE_FORMAT_SEC))
    }

    @Test
    fun stringToArrayList()
    {
        Assert.assertTrue(Util.stringToArrayList("a,b,c").isNotEmpty())
    }

    @Test
    fun getDateNotPMAM()
    {
        Assert.assertNotNull(Util.getDateMMDDYYYY(dummyDate))
    }

    @Test
    fun getDateOject()
    {
        Assert.assertNotNull(Util.getDateOject(dummyDate))
    }




    @Test
    fun formatDateTimeNew() {
        Assert.assertNotNull(Util.formatDateTimeNew(dummyDate , false, false))
    }

    @Test
    fun formatDateNew() {
        Assert.assertNotNull(Util.formatDateNew(dummyDate ))
    }

    @Test
    fun getTwoDigitNumber() {

        Assert.assertEquals(Util.getTwoDigitNumber("10.0" ) , "10.00")
    }

    @Test
    fun getDateAgo() {
        Assert.assertNotNull(Util.getDateAgo(dummyDate))
    }

    @Test
    fun getDateToLong() {
        Assert.assertNotNull(Util.getDateToLong(dummyDate))
    }

    @Test
    fun getDateAgoMins() {
        Assert.assertNotNull(Util.getDateAgoMins(dummyDate))
    }

    @Test
    fun getCurrentTime() {
        Assert.assertNotNull(Util.getCurrentTime())
    }

    @Test
    fun getDifferentInMinutes() {
        Assert.assertNotNull(Util.getDifferentInMinutes(Date().time))
    }

    @Test
    fun isUrl()
    {
        Assert.assertTrue(Util.isUrl("http://www.capella.edu"))

    }

    @Test
    fun isEmail() {
        Assert.assertTrue(Util.isEmail("jayesh.lahare@capella.edu"))
    }

    @Test
    fun getDateMMDDYYYY() {
        Assert.assertNotNull(Util.getDateMMDDYYYY(dummyDate))
    }

    @Test
    fun findURLinHREF() {
        Assert.assertNotNull(Util.findURLinHREF("href=www.capella.edu"))
    }






    @Test
    fun removeHostName() {

        Assert.assertNotNull(Util.removeHostName("https://www.capella.edu/home"))
    }

    @Test
    fun getTrucatedString() {
        Assert.assertNotNull(Util.removeHostName("https://www.capella.edu/home"))
    }

    @Test
    fun getHostName() {
        Assert.assertNotNull(Util.getHostName("https://www.capella.edu/home"))
    }

    @Test
    fun findDomainByMessageLink() {

        Assert.assertNotNull(Util.findDomainByMessageLink("https://www.capella.edu/webapps"))
    }

    @Test
    fun getWordsForMoreFeature()
    {
        Assert.assertTrue(Util.getWordsForMoreFeature("lorem ipusm" , 5).length > 5 )
    }

    @Test
    fun getNonDecimal() {
        Assert.assertTrue(Util.getNonDecimal("10.00")!!.length == 2 )
    }
}