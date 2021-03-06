package com.pramod.dailyword.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pramod.dailyword.R
import com.pramod.dailyword.databinding.ActivityAppSettingBinding
import com.pramod.dailyword.ui.BaseActivity

import com.pramod.dailyword.BR
import com.pramod.dailyword.helper.*
import com.pramod.dailyword.helper.WindowPrefManager
import com.pramod.dailyword.ui.about_app.AboutAppActivity

class AppSettingActivity : BaseActivity<ActivityAppSettingBinding, AppSettingViewModel>() {


    companion object {
        @JvmStatic
        fun openActivity(context: Context) {
            val intent = Intent(context, AppSettingActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_app_setting

    override fun getViewModel(): AppSettingViewModel =
        ViewModelProviders.of(this).get(AppSettingViewModel::class.java)

    override fun getBindingVariable(): Int = BR.appSettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        initThemeSelector()
        navigateToAbout()
        edgeToEdgeSettingChanged()
        arrangeViewsAccordingToEdgeToEdge()
    }


    private fun arrangeViewsAccordingToEdgeToEdge() {
        if (WindowPrefManager.newInstance(this).isEdgeToEdgeEnabled()) {
            ViewCompat.setOnApplyWindowInsetsListener(
                mBinding.root
            ) { v, insets ->
                mBinding.appBar.setPadding(
                    0, insets.systemWindowInsetTop, 0, 0
                )

                val paddingTop = insets.systemWindowInsetTop + mBinding.nestedScrollView.paddingTop
                val paddingBottom = insets.systemWindowInsetBottom

                mBinding.nestedScrollView.setPadding(
                    0,
                    paddingTop,
                    0,
                    paddingBottom
                )

                insets
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.let {
            it.title = null
        }
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_round_back_arrow)
        mBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initThemeSelector() {
        mViewModel.getShowThemeSelector().observe(this, Observer {
            it.getContentIfNotHandled()?.let { option ->
                DailogHelper.showRadioDialog(
                    this@AppSettingActivity,
                    "Choose App Theme",
                    R.array.theme_options,
                    option.name,
                    "Apply",
                    "Cancel"
                ) { selectedThemeText ->
                    mViewModel.changeThemePref(ThemeManager.Options.valueOf(selectedThemeText))
                }
            }
        })
        mViewModel.themeManager.liveData().observe(this, Observer {
            mViewModel.applyTheme(ThemeManager.Options.values()[it])
        })
    }

    private fun navigateToAbout() {
        mViewModel.navigateToAbout().observe(this, Observer {
            it.getContentIfNotHandled()?.let { navigate ->
                if (navigate) {
                    AboutAppActivity.openActivity(this)
                }
            }
        })
    }

    private fun edgeToEdgeSettingChanged() {
        mViewModel.recreateActivity().observe(this, Observer {
            it?.let { recreate ->
                restartActivity()
            }
        })
    }

}
