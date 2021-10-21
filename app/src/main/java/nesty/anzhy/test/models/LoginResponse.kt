package nesty.anzhy.test.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("success")
	val success: String,

	@field:SerializedName("response")
	val response: TokenResult
)
