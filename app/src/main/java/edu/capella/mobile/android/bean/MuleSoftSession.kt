package edu.capella.mobile.android.bean

/**
 * MuleSoftSession.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */
data class MuleSoftSession(
    var token: String? = null
)
{
    override fun toString(): String {
        return "MuleSoftSession(token=$token)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MuleSoftSession

        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        return token?.hashCode() ?: 0
    }


}