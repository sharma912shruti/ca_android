package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.AnnouncementsBean
import edu.capella.mobile.android.bean.AssesstmentFacultyBean
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class AssesstmentFacultyBeanTest {

   var assesstmentFacultyBean  =  AssesstmentFacultyBean()

    @Before
    fun setFakeData()
    {
        assesstmentFacultyBean  =  AssesstmentFacultyBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        assesstmentFacultyBean.courseAssignment = null
        assesstmentFacultyBean.serverName = null
        assesstmentFacultyBean.trackingId   = null

        assertNull ( assesstmentFacultyBean.courseAssignment)
        assertNull (assesstmentFacultyBean.serverName)
        assertNull ( assesstmentFacultyBean.trackingId)

    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(assesstmentFacultyBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(assesstmentFacultyBean.hashCode())
    }



//    @Test
//    fun check_whether_bean_is_gson_supported()
//    {
//        var gson = Gson( )
//        var json = gson.toJson(assesstmentFacultyBean)
//
//        var newBean = gson.fromJson(json , AssesstmentFacultyBean::class.java )
//
//        assertEquals(assesstmentFacultyBean,newBean)
//
//    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(assesstmentFacultyBean)

        var newBean = gson.fromJson(json , AssesstmentFacultyBean::class.java )
        newBean.serverName = "Assessment faculty Bean"

        assertNotEquals(assesstmentFacultyBean,newBean)

    }




}
