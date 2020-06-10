package edu.capella.mobile.android.bean

/**
 * MuleTokenVerify.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */
data class MuleTokenVerify(
    var capellaLearnerVerify: String? = null,
    var exp: Int? = null,
    var expires_in: Int? = null,
    var iat: Int? = null,
    var iss: String? = null,
    var ssn: Any? = null,
    var sub: String? = null


)
{
    override fun toString(): String {
        return "MuleTokenVerify(capellaLearnerVerify=$capellaLearnerVerify, exp=$exp, expires_in=$expires_in, iat=$iat, iss=$iss, ssn=$ssn, sub=$sub)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MuleTokenVerify

        if (capellaLearnerVerify != other.capellaLearnerVerify) return false
        if (exp != other.exp) return false
        if (expires_in != other.expires_in) return false
        if (iat != other.iat) return false
        if (iss != other.iss) return false
        if (ssn != other.ssn) return false
        if (sub != other.sub) return false

        return true
    }

    override fun hashCode(): Int {
        var result = capellaLearnerVerify?.hashCode() ?: 0
        result = 31 * result + (exp ?: 0)
        result = 31 * result + (expires_in ?: 0)
        result = 31 * result + (iat ?: 0)
        result = 31 * result + (iss?.hashCode() ?: 0)
        result = 31 * result + (ssn?.hashCode() ?: 0)
        result = 31 * result + (sub?.hashCode() ?: 0)
        return result
    }


}