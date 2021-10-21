package nesty.anzhy.test.models

import com.google.gson.annotations.SerializedName

data class TokenResponse(
	@field:SerializedName("response")
	val response: List<ResponseItem>
)

