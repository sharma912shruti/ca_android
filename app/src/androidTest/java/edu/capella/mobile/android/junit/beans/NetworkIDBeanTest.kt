package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.GradeFacultyBean
import edu.capella.mobile.android.bean.NetworkID
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class NetworkIDBeanTest {

   var networkID  =  NetworkID()

    @Before
    fun setFakeData()
    {
        networkID  =  NetworkID()

    }

    @Test
    fun check_for_getter_and_setter()
    {



        networkID.id = null
        networkID.network= null
       

       

        assertNull (networkID.id )
        assertNull (networkID.network  )


    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(networkID.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(networkID.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(networkID)

        var newBean = gson.fromJson(json , NetworkID::class.java )

        assertEquals(networkID,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(networkID)

        var newBean = gson.fromJson(json , NetworkID::class.java )
        newBean.id = "New Id"

        assertNotEquals(networkID,newBean)

    }




}
