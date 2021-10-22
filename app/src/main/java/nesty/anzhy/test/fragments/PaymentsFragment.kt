package nesty.anzhy.test.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import nesty.anzhy.test.viewmodel.MainViewModel
import nesty.anzhy.test.R
import nesty.anzhy.test.adapters.PaymentsAdapter
import nesty.anzhy.test.databinding.FragmentPaymentsBinding
import nesty.anzhy.test.models.ResponseItem
import nesty.anzhy.test.util.Constants
import nesty.anzhy.test.util.NetworkListener
import nesty.anzhy.test.util.NetworkResult
import java.util.*
import kotlin.collections.HashMap

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

        setHasOptionsMenu(true)


        val token:String = arguments?.get("token").toString()
        Log.d("TOKEN", token)

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
                        Log.e("ApiRequestSuccess", response.data.response.toString())
                        val list:List<ResponseItem> = response.data.response
                        Collections.sort(list)
                        mAdapter.setData(list) }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showAlertDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Log out")
        builder.setMessage("You will be returned to the login screen.")

        builder.setPositiveButton("Log out") { dialog, which ->
            findNavController().navigate(R.id.action_paymentsFragment_to_signInFragment)
        }

        builder.setNegativeButton("Cancel"){dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("onDestroy", "DESTROYYYYY")
    }
}
