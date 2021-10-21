package nesty.anzhy.test.models

import com.google.gson.annotations.SerializedName

data class ResponseItem(

    @field:SerializedName("amount")
    val amount: Double,

    @field:SerializedName("created")
    val created: Int,

    @field:SerializedName("currency")
    val currency: String,

    @field:SerializedName("desc")
    val desc: String


):Comparable<ResponseItem> {
    override fun compareTo(other: ResponseItem): Int {
      return  other.created - this.created
    }
}