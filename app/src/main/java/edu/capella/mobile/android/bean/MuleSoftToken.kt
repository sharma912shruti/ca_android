package edu.capella.mobile.android.bean

/**
 * MuleSoftToken.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */

data class MuleSoftToken(
    var access_token: String? = null,
    var expires_in: Int? = null,
    var scope: String? = null,
    var token_type: String? = null


)
{
    override fun toString(): String {
        return "MuleSoftToken(access_token=$access_token, expires_in=$expires_in, scope=$scope, token_type=$token_type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MuleSoftToken

        if (access_token != other.access_token) return false
        if (expires_in != other.expires_in) return false
        if (scope != other.scope) return false
        if (token_type != other.token_type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = access_token?.hashCode() ?: 0
        result = 31 * result + (expires_in ?: 0)
        result = 31 * result + (scope?.hashCode() ?: 0)
        result = 31 * result + (token_type?.hashCode() ?: 0)
        return result
    }


}