package com.senjapagi.sawitjaya.modelAndAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.*
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.staff.StaffInputInvoice

class CustomListAdapter(
    private val cont: Context,
    private val weightCount: MutableList<Double>
) :
    ArrayAdapter<Double>(cont, R.layout.list_weight, weightCount) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val rowView = LayoutInflater.from(cont).inflate(R.layout.list_weight, null, true)
        val tvWeightCount = rowView.findViewById(R.id.tv_weight_count) as TextView
        val tvWeightOrder = rowView.findViewById(R.id.tv_weight_order) as TextView
        val btnDelete = rowView.findViewById(R.id.btnDeleteWeight) as ImageButton
        val btnEdit = rowView.findViewById(R.id.btnEditWeight) as ImageButton

        tvWeightCount.text = weightCount[position].toString()
        tvWeightOrder.text = "Timbangan ke-${position+1}"



        btnDelete.setOnClickListener {
            makeToast("adadad $position")
        }

        btnEdit.setOnClickListener {

        }

        return rowView
    }

    private fun makeToast(message:String){
        Toast.makeText(cont,message,Toast.LENGTH_LONG).show()
    }

}