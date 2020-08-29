package com.senjapagi.sawitjaya.fragments.orderStaff

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.modelAndAdapter.adapterAllOrderStaff
import com.senjapagi.sawitjaya.modelAndAdapter.modelReqOrder
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.fragment_order_staff_processed.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [orderStaffProcessed.newInstance] factory method to
 * create an instance of this fragment.
 */
class orderStaffProcessed : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var data = ArrayList<modelReqOrder>()
    lateinit var adapterOrder: adapterAllOrderStaff

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_staff_processed, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        data.clear()
        getOrder()
        staffSrlOrderProcessed.setOnRefreshListener {
            staffSrlOrderProcessed.isRefreshing=false
            getOrder()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    private fun getOrder() {
        recyclerViewStaffProcessedOrder.setHasFixedSize(true)
        recyclerViewStaffProcessedOrder.layoutManager = LinearLayoutManager(
            context, RecyclerView.VERTICAL, false)

        staffAllOrderErrorPlaceHolder.visibility=View.GONE
        anim_loading.visibility=View.VISIBLE
        data.clear()
        AndroidNetworking.get(api.STAFF_ORDER_PROCESSED)
            .addHeaders("token", Preference(context!!).getPrefString(const.TOKEN))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    anim_loading.visibility=View.GONE
                    staffAllOrderErrorPlaceHolder.text=response.toString()
                    val raz = response.getJSONArray("data")
                    if(raz.length()<1){
                        staffAllOrderErrorPlaceHolder.visibility=View.VISIBLE
                        staffAllOrderErrorPlaceHolder.text="Anda Belum Memiliki Order Aktif"
                    }
                    if (response.getBoolean("success_status")) {
                        for (i in 0 until raz.length()) {
                            val id = raz.getJSONObject(i).getString("id")
                            val user_id = raz.getJSONObject(i).getString("user_id")
                            val est_weight = raz.getJSONObject(i).getString("est_weight")
                            val address = raz.getJSONObject(i).getString("addr")
                            val cord_lat = raz.getJSONObject(i).getString("cord_lat")
                            val cord_lon = raz.getJSONObject(i).getString("cord_lon")
                            val alt_contact = raz.getJSONObject(i).getString("alt_contact")
                            val created_at = raz.getJSONObject(i).getString("created_at")
                            val updated_at = raz.getJSONObject(i).getString("updated_at")
                            val deleted_at = raz.getJSONObject(i).getString("deleted_at")
                            val status = raz.getJSONObject(i).getString("status")

                            data.add(
                                modelReqOrder(
                                    id,
                                    user_id,
                                    est_weight,
                                    address,
                                    cord_lat,
                                    cord_lon,
                                    alt_contact,
                                    created_at,
                                    updated_at,
                                    deleted_at,
                                    status
                                )
                            )

                        }
                        adapterOrder = adapterAllOrderStaff(data,context!!)
                        recyclerViewStaffProcessedOrder?.adapter = adapterOrder
                        recyclerViewStaffProcessedOrder?.visibility = View.VISIBLE
                    } else {
                        staffAllOrderErrorPlaceHolder.text = "Terjadi Error yang tidak diketahui"
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility=View.GONE
                    staffAllOrderErrorPlaceHolder.text = "Gagal Terhubung Dengan Server \n ${anError.toString()}"
                }

            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment orderStaffProcessed.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            orderStaffProcessed().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}