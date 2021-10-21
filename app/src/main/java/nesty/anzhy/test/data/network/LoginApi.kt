package nesty.anzhy.test.data.network

import nesty.anzhy.test.models.LoginResponse
import nesty.anzhy.test.models.TokenResponse
import nesty.anzhy.test.util.Constants.Companion.APP_KEY_HEADER
import nesty.anzhy.test.util.Constants.Companion.VERSION_HEADER
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.POST


interface LoginApi {
    @FormUrlEncoded
    @Headers(APP_KEY_HEADER, VERSION_HEADER)
    @POST("login")
    fun login(@Field("login") login:String,
              @Field ("password") password:String): Call<LoginResponse>

    @GET("payments")
    suspend fun getPayments(
        @QueryMap queries: Map<String, String>
    ): Response<TokenResponse>
}