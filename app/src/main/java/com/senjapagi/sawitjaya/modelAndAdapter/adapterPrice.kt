package com.senjapagi.sawitjaya.modelAndAdapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.admin.AdminManageHarga
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.list_order.view.*
import kotlinx.android.synthetic.main.list_prices.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class adapterPrice(private val priceData: MutableList<modelPrice>, val context: Context) :
    RecyclerView.Adapter<PriceHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceHolder {
        return PriceHolder(
            LayoutInflater.from(context).inflate(R.layout.list_prices, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return priceData.size
    }

    override fun onBindViewHolder(holder: PriceHolder, position: Int) {
        holder.tvMargin.text = priceData[position].price_grade + " %"
        holder.tvPriceAmt.text = "Rp." + priceData[position].price + ",-"
        val netDate = getDateTime(priceData[position].created_at)
        holder.tvDate.text = netDate.toString()

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Konfirmasi")
            builder.setMessage("Anda Yakin ingin menghapus harga yang dipilih ??")
            builder.setPositiveButton("Ya") { dialog, which ->
                deletePrice(priceData[position].id.toString().toInt(),position)
                Toast.makeText(context, "Menghapus Harga", Toast.LENGTH_SHORT).show()
            }
            // Display a negative button on alert dialog
            builder.setNegativeButton("Batal") { dialog, which ->
                dialog.dismiss()
            }
//            builder.setNeutralButton("Cancel"){_,_ ->
//            }
            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            // Display the alert dialog on app interface
            dialog.show()
        }
    }


    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
//    api/admin/price/{id}/delete
//    (post)
//    (header) token

    private fun deletePrice(id: Int, position: Int) {
        AndroidNetworking.post(api.BASE_URL + "admin/price/$id/delete")
            .addHeaders("token", Preference(context).getPrefString(const.TOKEN))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (context is AdminManageHarga) {
                        if (response.getBoolean("success_status")) {
                            context.successDialog("Berhasil", "Berhasil Menghapus Harga", 0)
                            priceData.removeAt(position);
                            this@adapterPrice.notifyDataSetChanged()
                            this@adapterPrice.notifyItemRemoved(position);
                            this@adapterPrice.notifyItemRangeChanged(position, priceData.size);
                        } else {
                            context.errorDialog(
                                "Gagal",
                                "Gagal Menambahkan Harga karena " + response.getString("message"),
                                3
                            )
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    if (context is AdminManageHarga) {
                        context.errorDialog(
                            "Gagal",
                            "Gagal Terhubung Dengan Server, Silakan Coba Lagi Nanti",
                            3
                        )
                    }

                }

            })
    }
}

class PriceHolder(view: View) : RecyclerView.ViewHolder(view) {
    val id = view.orderID
    val tvMargin = view.price_margin
    val tvDate = view.price_date
    val tvPriceAmt = view.price_amt

    //val user_id

    //  val cord_lat:String,
    //  val cord_lon:String,
    //  val alt_contact

    //  val updated_at:String,
    //  val deleted_at:String,

}