package edu.capella.mobile.android.junit

import edu.capella.mobile.android.utils.Util
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test


class UtilTest {



    @Test
    fun check_for_valid_url()
    {

        assertThat(
            Util.isUrl("http://capella.edu") ,  equalTo(true) )
    }

    @Test
    fun check_for_invalid_url()
    {

        assertThat(
            Util.isUrl("capella.edu") ,  equalTo(false) )
    }

    @Test
    fun check_for_valid_email()
    {

        assertThat(
            Util.isEmail("jayesh.lahare@capella.edu") ,  equalTo(true) )
    }

    @Test
    fun check_for_invalid_email()
    {

        assertThat(
            Util.isEmail("jayesh.lahare") ,  equalTo(false) )
    }

    @Test
    fun check_for_date_MM_DD_YYYY_FORMAT()
    {
        assertThat(
            Util.getDateMMDDYYYY("2020-03-30-15:29") ,  equalTo("03/30/2020") )
    }


}
