package nesty.anzhy.test.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nesty.anzhy.test.databinding.PaymentsItemLayoutBinding
import nesty.anzhy.test.models.ResponseItem
import nesty.anzhy.test.util.convertTimestampToTime

class PaymentsAdapter : RecyclerView.Adapter<PaymentsAdapter.VH>() {

    private var data = emptyList<ResponseItem>()

    class VH(val binding: PaymentsItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: PaymentsItemLayoutBinding =
            PaymentsItemLayoutBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = data[position]
        var amount = data.amount.toString()
        var currency = data.currency
        if(currency.isNullOrEmpty()){
            currency = ""
        }
        holder.binding.txtDate.text = convertTimestampToTime(data.created.toLong())
        holder.binding.txtAmount.text = amount+" "+ currency

    }

    override fun getItemCount(): Int = data.size

    fun setData(newData: List<ResponseItem>) {
        this.data = newData
        notifyDataSetChanged()
    }
}


