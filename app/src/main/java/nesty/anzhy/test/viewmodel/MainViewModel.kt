package nesty.anzhy.test.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import nesty.anzhy.test.R
import nesty.anzhy.test.data.Repository
import nesty.anzhy.test.models.LoginResponse
import nesty.anzhy.test.models.TokenResponse
import nesty.anzhy.test.util.NetworkResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    var networkStatus = false
    var backOnline = false

    private val eventsChannel = Channel<AllEvents>()
    val allEventsFlow = eventsChannel.receiveAsFlow()

    val paymentsResponseToken: MutableLiveData<NetworkResult<TokenResponse>> = MutableLiveData()

    fun getPayments(queries: Map<String, String>) = viewModelScope.launch {
        getPaymentsSafeCall(queries)
    }

    private suspend fun getPaymentsSafeCall(queries: Map<String, String>) {
        paymentsResponseToken.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val data = repository.remote.getPayments(queries)
                paymentsResponseToken.value = handlePaymentsResponse(data)
            } catch (e: Exception) {
                paymentsResponseToken.value = NetworkResult.Error("Payments not found.")
            }
        } else {
            paymentsResponseToken.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handlePaymentsResponse(response: Response<TokenResponse>): NetworkResult<TokenResponse>? {
        return when {
            response.isSuccessful -> {
                val data = response.body()
                NetworkResult.Success(data!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signInUser(login: String, password: String, fragment: Fragment) = viewModelScope.launch {
        when {
            login.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(1))
            }
            password.isEmpty() -> {
                eventsChannel.send(AllEvents.ErrorCode(2))
            }
        }

        repository.remote.login(login, password).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("response", t.message.toString())
            }

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                Log.d("responseSuccess", response.toString())
                val token = response.body()?.response?.token.toString()
                Log.d("responseSuccess2", token)
                val bundle = Bundle()
                bundle.putString("token", token)
                if(response.body()?.success.equals("true")) {
                    findNavController(fragment).navigate(
                        R.id.action_signInFragment_to_paymentsFragment,
                        bundle
                    )
                }
                else Toast.makeText(getApplication(), "wrong login or password",
                Toast.LENGTH_SHORT).show()
            }
        })


    }


    sealed class AllEvents {
        data class Message(val message: String) : AllEvents()
        data class ErrorCode(val code: Int) : AllEvents()
        data class Error(val error: String) : AllEvents()
    }
}



