package com.senjapagi.sawitjaya.modelAndAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.staff.StaffOrderDetail
import kotlinx.android.synthetic.main.list_order.view.*
import java.text.SimpleDateFormat
import java.util.*

class adapterAllOrderStaff (private val orderData:MutableList<modelReqOrder>, val context:Context) :
    RecyclerView.Adapter<AllOrderStaffHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderStaffHolder {
        return AllOrderStaffHolder(LayoutInflater.from(context).inflate(R.layout.list_order,parent,false))
    }

    override fun getItemCount(): Int {
        return orderData.size
    }

    override fun onBindViewHolder(holder: AllOrderStaffHolder, position: Int) {
        holder.addr.text=orderData[position].addr
        holder.created_at.text=getDateTime(orderData[position].created_at)
        holder.est_weight.text=orderData[position].est_weight
        holder.id.text=orderData[position].id
        holder.itemView.setOnClickListener {
            makeToast("User ID : ${orderData[position].user_id}")
        }

        //waiting,processed,successed,failed
        var stat = "Error"
        when(orderData[position].status){
            "waiting" ->{
                stat="Menunggu Diproses"

            }
            "processed"->{
                stat="Sedang Diproses"
            }
            "successed"-> {
                stat = "Selesai"
            }
            "failed"->{
                stat="Dibatalkan"
            }

        }
        holder.status.text=stat

        holder.btnOrderDetail.setOnClickListener {
            try {
                val e = Intent(context, StaffOrderDetail::class.java)
                e.putExtra("id",orderData[position].id)
                e.putExtra("address",orderData[position].addr)
                e.putExtra("est",orderData[position].est_weight)
                e.putExtra("created_at",holder.created_at.text.toString())
                e.putExtra("status",orderData[position].status)
                context.startActivity(e)
            }
            catch (e : java.lang.Exception){
                Log.e("Error",e.toString())
            }

        }

    }

    private fun makeToast(message:String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
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
}


class AllOrderStaffHolder(view: View) : RecyclerView.ViewHolder(view) {
    val id = view.orderID
    val est_weight = view.orderEstimate
    val addr = view.orderAddress
    val status = view.orderStatus
    val statusContainer = view.orderStatusColor
    val created_at = view.orderDueDate
    val btnOrderDetail = view.btnSeeOrderDetail


    //val user_id

    //  val cord_lat:String,
    //  val cord_lon:String,
    //  val alt_contact

    //  val updated_at:String,
    //  val deleted_at:String,

}