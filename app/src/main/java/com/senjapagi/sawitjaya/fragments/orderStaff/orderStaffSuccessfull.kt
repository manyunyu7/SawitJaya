package com.senjapagi.sawitjaya.fragments.orderStaff

import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.fragment_order_staff_successfull.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [orderStaffSuccessfull.newInstance] factory method to
 * create an instance of this fragment.
 */
class orderStaffSuccessfull : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var urlGet = ""
    var source = ""
    var data = ArrayList<modelReqOrder>()
    lateinit var adapterOrder: adapterAllOrderStaff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        source= arguments?.getString("source").toString()

    }

    override fun onPause() {
        super.onPause()
        AndroidNetworking.forceCancelAll()
        AndroidNetworking.cancelAll()
    }

    override fun onResume() {
        super.onResume()
        getOrder()
        staffSrlOrderSuccess.setOnRefreshListener {
            staffSrlOrderSuccess.isRefreshing=false
            getOrder()
        }
    }

    private fun getOrder() {
        try {
            recyclerViewStaffSuccessOrder.setHasFixedSize(true)
            recyclerViewStaffSuccessOrder.layoutManager = LinearLayoutManager(
                context, RecyclerView.VERTICAL, false)

            staffSuccessOrderErrorPlaceHolder.visibility=View.GONE
            anim_loading.visibility=View.VISIBLE
            data.clear()

            urlGet = if(source=="admin"){
                api.ADMIN_ORDER_SUCCESSED
            }else{
                api.STAFF_ORDER_SUCCESSED
            }

            AndroidNetworking.get(urlGet)
                .addHeaders("token", Preference(requireContext()).getPrefString(const.TOKEN))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.i("Respons Get Ord","$response")
                        anim_loading?.visibility=View.GONE
                        staffSuccessOrderErrorPlaceHolder.text=response.toString()
                        val raz = response.getJSONArray("data")
                        if(raz.length()<1){
                            staffSuccessOrderErrorPlaceHolder?.visibility=View.VISIBLE
                            staffSuccessOrderErrorPlaceHolder?.text="Belum Ada Request Jemput Berhasil"
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

                                data?.add(
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
                            adapterOrder = adapterAllOrderStaff(data,requireContext(),source)
                            recyclerViewStaffSuccessOrder?.adapter = adapterOrder
                            recyclerViewStaffSuccessOrder?.visibility = View.VISIBLE
                        } else {
                            staffSuccessOrderErrorPlaceHolder?.text = "Terjadi Error yang tidak diketahui"
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anim_loading?.visibility=View.GONE
                        staffSuccessOrderErrorPlaceHolder?.text = "Gagal Terhubung Dengan Server \n ${anError.toString()}"
                    }

                })
        }catch (err:Exception){

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_staff_successfull, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment orderStaffAll.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            orderStaffAll().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}