package nesty.anzhy.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.flow.collect
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import nesty.anzhy.test.databinding.FragmentSignInBinding

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        listenToChannels()

        binding.apply {
            btnSignIn.setOnClickListener {
                progressBarSignin.isVisible = true
                val email = userLoginEtv.text.toString()
                val password = userPasswordEtv.text.toString()
                mainViewModel.signInUser(email, password, this@SignInFragment)
            }
        }


        return binding.root
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.allEventsFlow.collect { event ->
                when(event){
                    is MainViewModel.AllEvents.Error -> {
                        binding.apply {
                            errorTxt.text =  event.error
                            progressBarSignin.isInvisible = true
                        }
                    }
                    is MainViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is MainViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding.apply {
                                userLoginEtvL.error = "login should not be empty"
                                progressBarSignin.isInvisible = true
                            }


                        if(event.code == 2)
                            binding.apply {
                                userPasswordEtvL.error = "password should not be empty"
                                progressBarSignin.isInvisible = true
                            }
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}