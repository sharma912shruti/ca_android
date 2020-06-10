package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CourseSyllabusBean
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
 */class CourseSyllabusBeanTest {

    var courseSyllabusBean  =  CourseSyllabusBean()

    @Before
    fun setFakeData()
    {
        courseSyllabusBean  =  CourseSyllabusBean()

    }

    @Test
    fun check_for_getter_and_setter() {
        courseSyllabusBean.courseSyllabus = null
        courseSyllabusBean.serverName = null
        courseSyllabusBean.trackingId = null
        Assert.assertNull(courseSyllabusBean.courseSyllabus)
        Assert.assertNull(courseSyllabusBean.serverName)
        Assert.assertNull(courseSyllabusBean.trackingId)

        var courseSyllabus = CourseSyllabusBean.CourseSyllabus()
        courseSyllabus.contentId = null
        courseSyllabus.courseSyllabusContents = null

        Assert.assertNull(courseSyllabus.courseSyllabusContents)
        Assert.assertNull(courseSyllabus.contentId)

        var courseSyllabusContent =
            CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent()
        courseSyllabusContent.content = null
        courseSyllabusContent.title = null

        Assert.assertNull(courseSyllabusContent.title)
        Assert.assertNull(courseSyllabusContent.content)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(courseSyllabusBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(courseSyllabusBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val courseSyllabus = CourseSyllabusBean.CourseSyllabus()

        val courseInfo = CourseSyllabusBean.CourseSyllabus()
        courseInfo.contentId =   "2400217"

        courseSyllabus.contentId = courseInfo.contentId

        courseSyllabusBean.courseSyllabus   = courseSyllabus
        var gson = Gson( )
        var json = gson.toJson(courseSyllabusBean)

        var newBean = gson.fromJson(json , CourseSyllabusBean::class.java )

        Assert.assertEquals(courseSyllabusBean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val courseSyllabus = CourseSyllabusBean.CourseSyllabus()

        val courseInfo = CourseSyllabusBean.CourseSyllabus()
        courseInfo.contentId =   "2400217"

        courseSyllabus.contentId = courseInfo.contentId

        courseSyllabusBean.courseSyllabus   = courseSyllabus
        var gson = Gson( )
        var json = gson.toJson(courseSyllabusBean)
        var newBean = gson.fromJson(json , CourseSyllabusBean::class.java )

        val nsyllabus = CourseSyllabusBean.CourseSyllabus()
        val ncourseSyllabus = CourseSyllabusBean.CourseSyllabus()
        ncourseSyllabus.contentId =   "3400217"
        nsyllabus.contentId = ncourseSyllabus.contentId
        newBean.courseSyllabus   = nsyllabus

        Assert.assertNotEquals(courseSyllabusBean, newBean)
    }
}