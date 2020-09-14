package com.senjapagi.sawitjaya.fragments.order

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
import com.senjapagi.sawitjaya.modelAndAdapter.adapterAllOrder
import com.senjapagi.sawitjaya.modelAndAdapter.modelReqOrder
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.fragment_active_order.*
import kotlinx.android.synthetic.main.fragment_finished_order.*
import kotlinx.android.synthetic.main.fragment_user_order_all.*
import kotlinx.android.synthetic.main.fragment_user_order_all.srlOrderAll
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [activeOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class activeOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var data = ArrayList<modelReqOrder>()
    lateinit var adapterOrder: adapterAllOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onPause() {
        super.onPause()
        AndroidNetworking.forceCancelAll()
        AndroidNetworking.cancelAll()
    }
    override fun onResume() {
        super.onResume()
        getOrder()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewActiveOrder?.visibility = View.VISIBLE
        recyclerViewActiveOrder.setHasFixedSize(true)
        recyclerViewActiveOrder.layoutManager = LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        srlOrderActive.setOnRefreshListener {
            srlOrderActive.isRefreshing = false
            getOrder()
        }

    }

    private fun getOrder() {
        try {
            activeOrderErrorPlaceHolder.visibility = View.GONE
            anim_loading.visibility = View.VISIBLE
            data.clear()
            AndroidNetworking.get(api.USER_ORDER_ACTIVE)
                .addHeaders("token", Preference(requireContext()).getPrefString(const.TOKEN))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        anim_loading.visibility = View.GONE
                        val raz = response.getJSONArray("data")
                        if (raz.length() < 1) {
                            activeOrderErrorPlaceHolder.visibility = View.VISIBLE
                            activeOrderErrorPlaceHolder.text = "Anda Belum Memiliki Order Aktif"
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
                            adapterOrder = adapterAllOrder(data, context!!)
                            recyclerViewActiveOrder?.adapter = adapterOrder
                            recyclerViewActiveOrder?.visibility = View.VISIBLE
                        } else {
                            activeOrderErrorPlaceHolder.text = "Terjadi Error yang tidak diketahui"
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anim_loading.visibility = View.GONE
                        activeOrderErrorPlaceHolder.text =
                            "Gagal Terhubung Dengan Server \n ${anError.toString()}"
                    }

                })
        } catch (err: Exception) {

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment activeOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            activeOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}