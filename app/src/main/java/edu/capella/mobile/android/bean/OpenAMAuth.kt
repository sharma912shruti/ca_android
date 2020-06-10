package edu.capella.mobile.android.bean

/**
 * OpenAMAuth.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */

data class OpenAMAuth(
    var realm: String? = null,
    var successUrl: String? = null,
    var tokenId: String? = null


)
{
    override fun toString(): String {
        return "OpenAMAuth(realm=$realm, successUrl=$successUrl, tokenId=$tokenId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenAMAuth

        if (realm != other.realm) return false
        if (successUrl != other.successUrl) return false
        if (tokenId != other.tokenId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = realm?.hashCode() ?: 0
        result = 31 * result + (successUrl?.hashCode() ?: 0)
        result = 31 * result + (tokenId?.hashCode() ?: 0)
        return result
    }


}