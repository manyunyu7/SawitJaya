package com.senjapagi.sawitjaya

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.senjapagi.sawitjaya.fragments.order.activeOrderFragment
import com.senjapagi.sawitjaya.fragments.order.allOrderFragment
import com.senjapagi.sawitjaya.fragments.order.canceledOrderFragment
import com.senjapagi.sawitjaya.fragments.order.finishedOrderFragment

class UserOrderPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        //  TODO("Not yet implemented")
        return when (position) {
            0 -> {
                allOrderFragment()

            }
            1 -> {
                activeOrderFragment()
            }
            2 -> {
                finishedOrderFragment()
            }
            3 -> {
                canceledOrderFragment()
            }
            else -> {
                return allOrderFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 4;
    }

    override fun getPageTitle(position: Int): CharSequence? {
//        return super.getPageTitle(position)
        return when(position){
            0->"Semua"
            1->"Aktif"
            2->"Selesai"
            3->"Gagal"


            else->{
                return "semua"
            }
        }
    }


}
