package nesty.anzhy.test.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nesty.anzhy.test.databinding.PaymentsItemLayoutBinding
import nesty.anzhy.test.models.ResponseItem

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
        holder.binding.txtAmount.text = data.amount.toString()+" "+ data.currency

    }

    override fun getItemCount(): Int = data.size

    fun setData(newData: List<ResponseItem>) {
        this.data = newData
        notifyDataSetChanged()
    }
}


