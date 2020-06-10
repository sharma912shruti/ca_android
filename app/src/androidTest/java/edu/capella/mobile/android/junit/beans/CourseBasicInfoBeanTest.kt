package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CourseBasicInfoBean
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * <H1>Class Name</H1>
 *
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
class CourseBasicInfoBeanTest {

    var courseBasicInfoBean  =  CourseBasicInfoBean()

    @Before
    fun setFakeData()
    {
        courseBasicInfoBean  =  CourseBasicInfoBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {
        courseBasicInfoBean.basicCourseInfo = null
        courseBasicInfoBean.serverName = null
        courseBasicInfoBean.trackingId = null

        Assert.assertNull(courseBasicInfoBean.basicCourseInfo)
        Assert.assertNull(courseBasicInfoBean.serverName)
        Assert.assertNull(courseBasicInfoBean.trackingId)

        var basicInformation = CourseBasicInfoBean.BasicCourseInfo()
        basicInformation.courseIdentifier = null
        basicInformation.courseId = null
        basicInformation.announcementLink = null
        basicInformation.courseLink = null
        basicInformation.courseMessageCount = null
        basicInformation.discussionLink = null
        basicInformation.messageLink = null
        basicInformation.messaging = null
        basicInformation.studyInstructions = null
        basicInformation.userRole = null

        Assert.assertNull(basicInformation.courseIdentifier)
        Assert.assertNull(basicInformation.courseId)
        Assert.assertNull(basicInformation.announcementLink)
        Assert.assertNull(basicInformation.courseLink)
        Assert.assertNull(basicInformation.courseMessageCount)
        Assert.assertNull(basicInformation.discussionLink)
        Assert.assertNull(basicInformation.messageLink)
        Assert.assertNull(basicInformation.messaging)
        Assert.assertNull(basicInformation.studyInstructions)
        Assert.assertNull(basicInformation.userRole)

        val courseIdentifier = CourseBasicInfoBean.CourseIdentifier()
        courseIdentifier.catalogNumber = null
        courseIdentifier.catalogSubject = null
        courseIdentifier.section = null
        courseIdentifier.sessionCode = null
        courseIdentifier.termCode = null

        Assert.assertNull(courseIdentifier.catalogNumber)
        Assert.assertNull(courseIdentifier.catalogSubject)
        Assert.assertNull(courseIdentifier.section)
        Assert.assertNull(courseIdentifier.sessionCode)
        Assert.assertNull(courseIdentifier.termCode)

        var studyInstruction = CourseBasicInfoBean.StudyInstruction()
        studyInstruction.content = null
        studyInstruction.title = null

        Assert.assertNull(studyInstruction.content)
        Assert.assertNull(studyInstruction.title)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(courseBasicInfoBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(courseBasicInfoBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        val basicCourseInfo = CourseBasicInfoBean.BasicCourseInfo()

        val courseInfo = CourseBasicInfoBean.BasicCourseInfo()
        courseInfo.courseId =   "2400217"

        basicCourseInfo.courseId = courseInfo.courseId

        var basicInfoList = ArrayList<CourseBasicInfoBean.BasicCourseInfo> ()
        basicInfoList.add(basicCourseInfo)
        courseBasicInfoBean.basicCourseInfo   = basicInfoList

        var gson = Gson( )
        var json = gson.toJson(courseBasicInfoBean)

        var newBean = gson.fromJson(json , CourseBasicInfoBean::class.java )

        Assert.assertEquals(courseBasicInfoBean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {

        val basicCourseInfo = CourseBasicInfoBean.BasicCourseInfo()

        val courseInfo = CourseBasicInfoBean.BasicCourseInfo()
        courseInfo.courseId =   "2400217"

        basicCourseInfo.courseId = courseInfo.courseId

        var basicInfoList = ArrayList<CourseBasicInfoBean.BasicCourseInfo> ()
        basicInfoList.add(basicCourseInfo)
        courseBasicInfoBean.basicCourseInfo   = basicInfoList

        var gson = Gson( )
        var json = gson.toJson(courseBasicInfoBean)
        var newBean = gson.fromJson(json , CourseBasicInfoBean.BasicCourseInfo::class.java )

        val ncourseInfo = CourseBasicInfoBean.BasicCourseInfo()
        val ncourseId = CourseBasicInfoBean.BasicCourseInfo()
        ncourseId.courseId =   "3400217"
        ncourseInfo.courseId = ncourseId.courseId
        newBean.courseId   = ncourseInfo.courseId

        Assert.assertNotEquals(courseBasicInfoBean.basicCourseInfo?.get(0)!!, newBean)
    }
}
