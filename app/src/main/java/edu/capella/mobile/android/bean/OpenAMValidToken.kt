package edu.capella.mobile.android.bean

/**
 * OpenAMValidToken.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */
data class OpenAMValidToken(
    var realm: String? = null,
    var uid: String? = null,
    var valid: Boolean? = null
)
{
    override fun toString(): String {
        return "OpenAMValidToken(realm=$realm, uid=$uid, valid=$valid)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenAMValidToken

        if (realm != other.realm) return false
        if (uid != other.uid) return false
        if (valid != other.valid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = realm?.hashCode() ?: 0
        result = 31 * result + (uid?.hashCode() ?: 0)
        result = 31 * result + (valid?.hashCode() ?: 0)
        return result
    }


}