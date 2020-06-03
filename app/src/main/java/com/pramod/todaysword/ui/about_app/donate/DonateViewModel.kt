package com.pramod.todaysword.ui.about_app.donate

import android.app.Application
import com.pramod.todaysword.R
import com.pramod.todaysword.ui.BaseViewModel

class DonateViewModel(application: Application) : BaseViewModel(application) {

    val donateItemList = arrayListOf(
        DonateItem(R.drawable.ic_cookie, "Cookie", 10),
        DonateItem(R.drawable.ic_coffee_outline, "Coffee", 50),
        DonateItem(R.drawable.ic_baseline_card_giftcard_24, "Gift", 150)
    )
}


data class DonateItem(val drawableId: Int, val title: String, val amount: Int)