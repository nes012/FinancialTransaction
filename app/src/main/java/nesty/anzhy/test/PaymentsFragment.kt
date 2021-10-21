package nesty.anzhy.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import nesty.anzhy.test.adapters.PaymentsAdapter
import nesty.anzhy.test.databinding.FragmentPaymentsBinding
import nesty.anzhy.test.models.ResponseItem
import nesty.anzhy.test.util.Constants
import nesty.anzhy.test.util.NetworkListener
import nesty.anzhy.test.util.NetworkResult

@AndroidEntryPoint
class PaymentsFragment : Fragment() {

    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkListener: NetworkListener

    private val mAdapter: PaymentsAdapter by lazy { PaymentsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentsBinding.inflate(inflater, container, false)

        val token:String = arguments?.get("token").toString()
        Log.e("TOKEN", token)

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    mainViewModel.networkStatus = status
                    mainViewModel.showNetworkStatus()
                }
        }

        setupRecyclerView()

        requestApiData(token)

        return binding.root
    }

    private fun requestApiData(token: String) {
        mainViewModel.getPayments(applyQuery(token))
        mainViewModel.paymentsResponseToken.observe(viewLifecycleOwner, { response ->
            when(response){
                is NetworkResult.Success -> {
                    response.data?.let {
                        Log.e("apirequest", response.data.response.toString())
                        mAdapter.setData(response.data.response) }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    fun applyQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[Constants.TOKEN] = searchQuery

        return queries
    }

    private fun setupRecyclerView() {
        binding.recycelerViewPayments.adapter = mAdapter
        binding.recycelerViewPayments.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
