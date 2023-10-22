package com.cube.cubeacademy.lib.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Nomination(
    @SerializedName("nomination_id") val nominationId: String,
    @SerializedName("nominee_id") val nomineeId: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("process") val process: String,
    @SerializedName("date_submitted") val dateSubmitted: String,
    @SerializedName("closing_date") val closingDate: String
) : Serializable

fun Nomination.withNominee(nominee: Nominee?): NominationWithNominee {
    return NominationWithNominee(this, nominee)
}

data class NominationWithNominee(val nomination: Nomination, val nominee: Nominee?)
