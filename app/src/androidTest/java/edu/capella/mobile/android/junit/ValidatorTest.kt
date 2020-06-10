package edu.capella.mobile.android.junit

import edu.capella.mobile.android.utils.Validator
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test


class ValidatorTest {



    @Test
    fun check_for_empty_string()
    {

        assertThat(Validator.checkEmptyString("capella") ,  equalTo(true) )
    }

    @Test
    fun check_for_non_empty_string()
    {

        assertThat(Validator.checkEmptyString("") ,  equalTo(false) )
    }




}
