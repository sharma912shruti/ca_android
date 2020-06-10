package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CourseSyllabusBean
import edu.capella.mobile.android.bean.EmailCountBean
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  20-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class EmailCountBeanTest {

    var emailCountBean = EmailCountBean()

    @Before
    fun setFakeData()
    {
        emailCountBean = EmailCountBean()

    }
    @Test
    fun check_for_getter_and_setter() {
        emailCountBean.loginUrl = null
        emailCountBean.serverName = null
        emailCountBean.trackingId = null
        emailCountBean.unreadCount = null
        Assert.assertNull(emailCountBean.loginUrl)
        Assert.assertNull(emailCountBean.serverName)
        Assert.assertNull(emailCountBean.trackingId)
        Assert.assertNull(emailCountBean.unreadCount)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(emailCountBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(emailCountBean.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val emailCount = EmailCountBean()

        val emailCountInfo = EmailCountBean()
        emailCountInfo.loginUrl =   "https://google.com"

        emailCount.loginUrl = emailCountInfo.loginUrl

        emailCountBean   = emailCount
        var gson = Gson( )
        var json = gson.toJson(emailCountBean)

        var newBean = gson.fromJson(json , EmailCountBean::class.java )

        Assert.assertEquals(emailCountBean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val emailCount = EmailCountBean()

        val emailCountInfo = EmailCountBean()
        emailCountInfo.loginUrl =   "https://google.com"

        emailCount.loginUrl = emailCountInfo.loginUrl

        emailCountBean   = emailCount
        var gson = Gson( )
        var json = gson.toJson(emailCountBean)
        var newBean = gson.fromJson(json , EmailCountBean::class.java )

        val emailInfo = EmailCountBean()
        val emailInfoOne = EmailCountBean()
        emailInfoOne.loginUrl =   "https://youtube.com"
        emailInfo.loginUrl = emailInfoOne.loginUrl
        newBean   =  emailInfo

        Assert.assertNotEquals(emailCountBean, newBean)
    }
}