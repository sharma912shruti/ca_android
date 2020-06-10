package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.GP2Bean
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  02-05-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class Gp2BeanTest {

    var gp2Bean  =  GP2Bean()

    @Before
    fun setFakeData()
    {
        gp2Bean  =  GP2Bean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        gp2Bean.assessmentCount = null
        gp2Bean.courseBatchUid = null
        gp2Bean.courseId   = null
        gp2Bean.courseName = null
        gp2Bean.coursePk1 = null
        gp2Bean.courseUrl = null
        gp2Bean.leftNavigationItems = null
        gp2Bean.newAnnouncements = null

        Assert.assertNull(gp2Bean.newAnnouncements)
        Assert.assertNull(gp2Bean.leftNavigationItems)
        Assert.assertNull(gp2Bean.courseUrl)
        Assert.assertNull(gp2Bean.coursePk1)
        Assert.assertNull(gp2Bean.courseName)
        Assert.assertNull(gp2Bean.courseId)
        Assert.assertNull(gp2Bean.courseBatchUid)
        Assert.assertNull(gp2Bean.newAnnouncements)
        Assert.assertNull(gp2Bean.assessmentCount)

        var leftNavigationItem = GP2Bean.LeftNavigationItem()
        leftNavigationItem.courseContentsPk1 = null
        leftNavigationItem.id = null
        leftNavigationItem.isEntryPoint = null
        leftNavigationItem.name = null
        leftNavigationItem.type = null
        leftNavigationItem.url = null

        Assert.assertNull(leftNavigationItem.url)
        Assert.assertNull(leftNavigationItem.type)
        Assert.assertNull(leftNavigationItem.name)
        Assert.assertNull(leftNavigationItem.isEntryPoint)
        Assert.assertNull(leftNavigationItem.id)
        Assert.assertNull(leftNavigationItem.courseContentsPk1)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(gp2Bean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(gp2Bean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(gp2Bean)

        var newBean = gson.fromJson(json , GP2Bean::class.java )

        Assert.assertEquals(gp2Bean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(gp2Bean)

        var newBean = gson.fromJson(json , GP2Bean::class.java )
        newBean.courseBatchUid = "New feed name"

        Assert.assertNotEquals(gp2Bean, newBean)

    }

}