package nesty.anzhy.test.models

import com.google.gson.annotations.SerializedName

data class TokenResult(
    @field:SerializedName("token")
    val token: String
)