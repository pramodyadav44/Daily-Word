package com.pramod.dailyword.ui.word_details

import com.pramod.dailyword.BR
import android.os.Bundle
import android.transition.ArcMotion
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.pramod.dailyword.databinding.ActivityWordDetailedBinding
import com.pramod.dailyword.R
import com.pramod.dailyword.db.model.WordOfTheDay
import com.pramod.dailyword.helper.AdsManager
import com.pramod.dailyword.helper.WindowPreferencesManager
import com.pramod.dailyword.helper.openWebsite
import com.pramod.dailyword.helper.shareApp
import com.pramod.dailyword.ui.BaseActivity
import com.pramod.dailyword.util.CommonUtils
import kotlinx.android.synthetic.main.activity_word_detailed.*

class WordDetailedActivity : BaseActivity<ActivityWordDetailedBinding, WordDetailedViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_word_detailed

    override fun getViewModel(): WordDetailedViewModel {
        val word = intent.extras!!.getSerializable("WORD") as WordOfTheDay

        return ViewModelProviders.of(this, WordDetailedViewModel.Factory(application, word))
            .get(WordDetailedViewModel::class.java)
    }

    override fun getBindingVariable(): Int = BR.wordDetailedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        initEnterAndReturnTransition()
        super.onCreate(savedInstanceState)
        setUpToolbar()
        setUpExampleRecyclerView()
        setUpDefinationRecyclerView()
        setNestedScrollListener()
        setNavigateMW()
        arrangeViewsAccordingToEdgeToEdge()
    }

    private fun setUpToolbar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.let {
            it.title = null
        }
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_round_back_arrow)
        mBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setUpExampleRecyclerView() {
        val adapter = ExampleAdapter(
            mViewModel.wordOfTheDay.examples,
            mViewModel.wordOfTheDay.wordColor,
            mViewModel.wordOfTheDay.wordDesaturatedColor
        )
        mBinding.wordDetailedExamplesRecyclerview.adapter = adapter
    }

    private fun setUpDefinationRecyclerView() {
        val adapter = DefinationAdapter(
            mViewModel.wordOfTheDay.meanings,
            mViewModel.wordOfTheDay.wordColor,
            mViewModel.wordOfTheDay.wordDesaturatedColor
        )
        mBinding.wordDetailedDefinationsRecyclerview.adapter = adapter
    }

    private fun setNavigateMW() {
        mViewModel.navigateToMerriamWebster().observe(this, Observer {
            it.getContentIfNotHandled()?.let { url ->
                openWebsite(url)
            }
        })
    }

    private fun arrangeViewsAccordingToEdgeToEdge() {
        if (WindowPreferencesManager.newInstance(this).isEdgeToEdgeEnabled()) {
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

                val fabMarginBottom = mBinding.fabGotToMw.marginBottom + paddingBottom
                val layoutParam: CoordinatorLayout.LayoutParams =
                    mBinding.fabGotToMw.layoutParams as CoordinatorLayout.LayoutParams
                layoutParam.setMargins(
                    mBinding.fabGotToMw.marginLeft,
                    mBinding.fabGotToMw.marginTop,
                    mBinding.fabGotToMw.marginRight,
                    fabMarginBottom
                )
                mBinding.fabGotToMw.layoutParams = layoutParam

                insets
            }
        }
    }

    private fun initEnterAndReturnTransition() {

        val enterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            duration = 300
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            pathMotion = ArcMotion()
            interpolator = FastOutSlowInInterpolator()
            containerColor =
                CommonUtils.resolveAttrToColor(
                    this@WordDetailedActivity,
                    android.R.attr.windowBackground
                )
        }

        val returnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            duration = 250
            pathMotion = ArcMotion()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            interpolator = FastOutSlowInInterpolator()
            containerColor =
                CommonUtils.resolveAttrToColor(
                    this@WordDetailedActivity,
                    android.R.attr.windowBackground
                )
        }


        findViewById<View>(android.R.id.content).transitionName = "CONTAINER"
        window.sharedElementEnterTransition = enterTransition
        window.sharedElementReturnTransition = returnTransition
        window.sharedElementsUseOverlay = false
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    }

    private fun setNestedScrollListener() {
        mBinding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val distanceToCover =
                mBinding.txtViewWordOfTheDay.height + mBinding.txtViewWordOfTheDayDate.height
            mViewModel.setTitleVisibility(distanceToCover < oldScrollY)
            /*  if (oldScrollY < scrollY) {
                  mBinding.fabGotToMw.shrink()
              } else {
                  mBinding.fabGotToMw.extend()
              }*/
        }
    }

    private val adsManager: AdsManager = AdsManager.newInstance(this)

    private fun showInterstitialAd() {

    }

    private fun showBannerAd() {
        val showing = adsManager.showBannerAdInCoordinateLayout(coordinatorLayout)
        if (showing) {
            //if ads are shown than increase the padding of the recyclerview by banner ad size
            ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout) { view: View, windowInsetsCompat: WindowInsetsCompat ->
                windowInsetsCompat
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share_word -> shareApp()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        adsManager.destroyAdsIfActive()
        super.onDestroy()
    }

}
