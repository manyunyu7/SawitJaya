package com.senjapagi.sawitjaya.activity.staff

import android.media.Image
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.modelAndAdapter.CustomListAdapter
import kotlinx.android.synthetic.main.activity_staff_input_invoice.*
import kotlinx.android.synthetic.main.activity_staff_input_invoice.etInputWeight
import kotlinx.android.synthetic.main.layout_edit_weight.*
import kotlinx.android.synthetic.main.layout_order_invoice.*
import kotlinx.android.synthetic.main.layout_question_dialog.*
import kotlinx.android.synthetic.main.list_weight.view.*

class StaffInputInvoice : AppCompatActivity() {
    var listBerat = mutableListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_input_invoice)


//        e.putExtra("order_id",orderDetID.toString())
//        e.putExtra("order_date",orderDetDueDate.toString())
//        e.putExtra("nama",orderDetAccName.toString())
//        e.putExtra("alt_con",orderDetAltContact.toString())
//        e.putExtra("main_con",orderDetAccPhone.toString())
//        e.putExtra("address",orderDetAddress.toString())
//        e.putExtra("est_weight",orderDetEstimate.toString())

        orderID.text = intent.getStringExtra("order_id")
        orderDueDate.text = intent.getStringExtra("order_date")
        orderEstimate.text = intent.getStringExtra("est_weight")
        orderAddress.text = intent.getStringExtra("address")
        orderInvDetAccName.text = intent.getStringExtra("name")
        btnInputWeight.setOnClickListener {
            if (!etInputWeight.text.toString().isBlank()) {
                val weight = etInputWeight.text.toString().toDouble()
                listBerat.add(weight)
                listViewWeight.adapter = CustomListAdapter(this, listBerat)
                totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
                makeToast("Berhasil Menambah Hasil Timbangan")
                etInputWeight.setText("")
            }
        }

        btnResetWeight.setOnClickListener {
            listBerat.clear()
            makeToast("Berhasil Menghapus Semua Hasil Timbangan")
            listViewWeight.adapter = CustomListAdapter(this, listBerat)
            totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
        }

        listViewWeight.adapter = CustomListAdapter(this, listBerat)
        val e = listViewWeight.rootView
        e.findViewById<ImageButton>(R.id.btnDeleteWeight)
        e.setOnClickListener {

        }




        listViewWeight.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                xEditWeight.visibility = View.VISIBLE
                xEditWeight.animation =
                    AnimationUtils.loadAnimation(this, R.anim.item_animation_falldown)
                xEditWeightTitle.text = view.tv_weight_order.text.toString()
                xEditInputWeight.setText(listBerat[position].toString())
                xEditWeightPButton.setOnClickListener {
                    if(xEditInputWeight.text.toString().isNotBlank()){
                        listBerat[position] =xEditInputWeight.text.toString().toDouble()
                        totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
                        xEditWeight.visibility = View.GONE
                        xEditWeight.animation =
                            AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
                        listViewWeight.invalidateViews()
                    }else{
                        etInputWeight.error="Isi Bidang ini untuk mengupdate"
                    }

                }
                xEditWeightNeutralButton.setOnClickListener {
                    xEditWeight.visibility = View.GONE
                    xEditWeight.animation =
                        AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
                }
                xEditWeightNButton.setOnClickListener {
                    totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
                    xEditWeight.visibility = View.GONE
                    xEditWeight.animation =
                        AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
                    listBerat.removeAt(position)
                    listViewWeight.invalidateViews()
                }
            }


//        listViewWeight.setOnItemClickListener { parent, view, position, id ->
//            view.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    //use getItem(position) to get the item
//                    v.
//                }
//            })
//        }


    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    public fun removeItem(index: Int) {
        listBerat.removeAt(index)
    }
}