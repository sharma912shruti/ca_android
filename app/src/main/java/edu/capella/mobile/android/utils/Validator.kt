package edu.capella.mobile.android.utils




/**
 * Validator.kt : Basic validation used in app.
 *
 * @author  :  KPANDYA1
 * @version :  1.0
 * @since   :  2/17/2020
 *
 */object Validator {


    /**
     * if string contains any value except null then it'll return true
     */
    fun checkEmptyString(string: String?): Boolean {
        return if (string != null && !string.trim { it <= ' ' }.equals(
                "null",
                ignoreCase = true
            ) && string.trim { it <= ' ' } != ""
        ) {
            true
        } else false
    }

    fun isBlank(string: String?): Boolean
    {
        if(string == null)
        {
            return true
        }
        if(string.trim().isEmpty())
        {
            return true
        }
        return  false

    }


}
