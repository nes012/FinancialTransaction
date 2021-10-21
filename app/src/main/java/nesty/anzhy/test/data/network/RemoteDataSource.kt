package nesty.anzhy.test.data.network

import nesty.anzhy.test.models.LoginResponse
import nesty.anzhy.test.models.ResponseItem
import nesty.anzhy.test.models.TokenResponse
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val loginApi: LoginApi
) {

    suspend fun getPayments(queries: Map<String, String>): Response<TokenResponse> {
        return loginApi.getPayments(queries)
    }

    fun login(login: String, password: String): Call<LoginResponse> {
        return loginApi.login(login, password)
    }

}