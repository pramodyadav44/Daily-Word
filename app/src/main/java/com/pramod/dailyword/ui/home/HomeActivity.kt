package com.pramod.dailyword.ui.home

import android.app.Activity
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.gson.Gson
import com.judemanutd.autostarter.AutoStartPermissionHelper
import com.pramod.dailyword.BR
import com.pramod.dailyword.BuildConfig
import com.pramod.dailyword.R
import com.pramod.dailyword.SnackbarMessage
import com.pramod.dailyword.databinding.ActivityMainBinding
import com.pramod.dailyword.db.model.WordOfTheDay
import com.pramod.dailyword.firebase.FBMessageService
import com.pramod.dailyword.helper.*
import com.pramod.dailyword.helper.WindowPrefManager
import com.pramod.dailyword.ui.BaseActivity
import com.pramod.dailyword.ui.bookmarked_words.FavoriteWordsActivity
import com.pramod.dailyword.ui.change_logs.ChangelogActivity
import com.pramod.dailyword.ui.settings.AppSettingActivity
import com.pramod.dailyword.ui.word_details.WordDetailedActivity
import com.pramod.dailyword.ui.words.WordListActivity
import com.pramod.dailyword.util.CalenderUtil
import kotlinx.android.synthetic.main.activity_word_list.*
import java.util.*


class HomeActivity : BaseActivity<ActivityMainBinding, HomeViewModel>() {


    companion object {

        val TAG = HomeActivity::class.simpleName

        @JvmStatic
        fun openActivity(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun openActivityWithFade(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            if (context is Activity) {
                context.overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            }
            context.startActivity(intent)
        }

        @JvmStatic
        fun openActivityWithTransition(context: Context, option: ActivityOptions) {
            val intent = Intent(context, HomeActivity::class.java)
            ActivityCompat.startActivity(context, intent, option.toBundle())
        }
    }

    private lateinit var pastWordAdapter: PastWordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        initEnterTransition()
        initExitTransition()
        super.onCreate(savedInstanceState)
        setUpViewCallbacks()
        deepLinkNotification()
        showChangelogActivity()
        initAppUpdate()
        initToolbar()
        initBottomMenu()
        setUpRecyclerViewAdapter()
        shouldShowRatingDialog()
        //edgeToEdgeSettingChanged()
        arrangeViewsAccordingToEdgeToEdge()
        promptAutoStart()
        //showDummyLotttieDialog()
        showNativeAdDialogWithDelay()
        shouldShowCreditDialog()
    }


    override fun onResume() {
        super.onResume()
        pastWordAdapter.setCanStartActivity(true)
    }

    private fun setUpViewCallbacks() {
        mBinding.setVariable(BR.viewCallbacks, object : ViewCallbacks {
            override fun readMore(v: View?, word: WordOfTheDay?) {
                word?.let {
                    if (pastWordAdapter.canStart()) {
                        pastWordAdapter.setCanStartActivity(false)
                        val view = mBinding.mainLinearLayoutWotd
                        intentToWordDetail(this@HomeActivity, view, word)
                    }
                }
            }

            override fun learnAll(v: View?) {
                WordListActivity.openActivity(this@HomeActivity)
            }

        })
    }

    private fun showChangelogActivity() {
        mViewModel.showChangelogActivity.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                if (it) {
                    ChangelogActivity.openActivity(this, true)
                }
            }
        })
    }

    private fun shouldShowCreditDialog() {
        val prefManager = PrefManager.getInstance(this)
        if (prefManager.getShowInitailCreditDialogStatus()) {
            prefManager.changeShowInitialCreditDialogStatus(false)
            showBasicDialog(
                "App Content Credit",
                resources.getString(R.string.merriam_webster_credit_text),
                positiveText = "Go to Merriam-Webster",
                positiveClickCallback = {
                    openWebsite(resources.getString(R.string.app_merriam_webster_icon_url))
                },
                negativeText = "Close"
            )
        }
    }

    private fun arrangeViewsAccordingToEdgeToEdge() {
        if (WindowPrefManager.newInstance(this).isEdgeToEdgeEnabled()) {
            ViewCompat.setOnApplyWindowInsetsListener(
                mBinding.root
            ) { v, insets ->
                mBinding.homeCardToolbar.setContentPadding(
                    0, insets.systemWindowInsetTop, 0, mBinding.homeCardToolbar.contentPaddingBottom
                )


                val paddingBottom = insets.systemWindowInsetBottom

                mBinding.homeImageViewBuildings.setPadding(
                    0,
                    0,
                    0,
                    paddingBottom
                )


                insets
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.let { title = null }
    }

    private fun initEnterTransition() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        window.enterTransition = fade
    }

    private fun initExitTransition() {
        /*val fade = Fade()
        fade.duration = 150
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        window.exitTransition = fade*/
        window.sharedElementsUseOverlay = false
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    }

    private fun showNotification(word: WordOfTheDay) {
        val notificationHelper = NotificationHelper(this@HomeActivity)
        val notification = notificationHelper.createNotification(
            title = "Welcome to Daily Word!",
            body = "Your first word of the day is '${word.word}'",
            cancelable = true,
            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                NotificationHelper.generateUniqueNotificationId(),
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(
                        FBMessageService.EXTRA_NOTIFICATION_PAYLOAD,
                        Gson().toJson(
                            FBMessageService.MessagePayload(
                                date = word.date!!,
                                deepLink = FBMessageService.DEEP_LINK_TO_WORD_DETAILED
                            )
                        )
                    )
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        notificationHelper.makeNotification(
            notification = notification
        )

    }

    private fun setUpRecyclerViewAdapter() {
        pastWordAdapter = PastWordAdapter { i: Int, wordOfTheDay: WordOfTheDay ->
            val view = mBinding.mainRecyclerviewPastWords.layoutManager!!.findViewByPosition(i)
            view?.let { nonNullView ->
                intentToWordDetail(this, nonNullView, wordOfTheDay)
            } ?: intentToWordDetail(activity = this, word = wordOfTheDay)

        }
        mBinding.pastWordAdapter = pastWordAdapter
        mViewModel.getTodaysWordOfTheDay().observe(this, Observer {
            it?.let {
                if (!it.isSeen) {
                    it.isSeen = true
                    it.seenAtTimeInMillis = Calendar.getInstance().timeInMillis
                    mViewModel.updateWordSeenStatus(it)
                    if (PrefManager.getInstance(this).getAppLaunchCount() == 1
                        && !mViewModel.firstNotificationShown
                    ) {
                        mViewModel.firstNotificationShown = true
                        showNotification(it)
                    }
                }
            }
        })
        mViewModel.getWordsExceptToday().observe(this, Observer {
            it?.let { words ->
                pastWordAdapter.submitList(words)
                mBinding.mainRecyclerviewPastWords.scrollToPosition(0)
            }
        })
        mViewModel.refreshDataSource()
    }

    private fun intentToWordDetail(activity: Activity, view: View? = null, word: WordOfTheDay) {
        val option = view?.let {
            ActivityOptions.makeSceneTransitionAnimation(
                activity,
                view,
                "CONTAINER"
            )
        }
        WordDetailedActivity.openActivity(this, word, option)
    }

    private fun edgeToEdgeSettingChanged() {
        WindowPrefManager.newInstance(this).getLiveData().observe(this, Observer<Boolean> {
            if (it) {

            }
        })
    }

    override fun getViewModel(): HomeViewModel {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.mainViewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> AppSettingActivity.openActivity(this)
            R.id.menu_bookmarked_words -> FavoriteWordsActivity.openActivity(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private fun initBottomMenu() {
        val navigationView = NavigationView(this)
        navigationView.inflateMenu(R.menu.home_more_menu)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_rate -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                    startActivity(intent)
                }
            }
            bottomSheetDialog.dismiss()
            false
        }
        bottomSheetDialog = BottomSheetDialog(this@HomeActivity)
        bottomSheetDialog.setContentView(navigationView)
    }

    private fun shouldShowRatingDialog() {
        if (PrefManager.getInstance(this)
                .shouldShowRateNowDialog()
        ) {
            showStaticPageDialog(
                R.layout.dialog_rate_app,
                positiveText = "Rate Now",
                positiveClickCallback = {
                    PrefManager.getInstance(this)
                        .setUserClickedRateNow()
                    openGoogleReviewPage()
                },
                negativeText = "Later",
                neutralText = "Never",
                neutralClickCallback = {
                    PrefManager.getInstance(this)
                        .setUserClickedNever()
                }
            )
        }
    }

    var appUpdateHelper: AppUpdateHelper? = null
    private fun initAppUpdate() {
        appUpdateHelper = AppUpdateHelper(this)
        appUpdateHelper?.checkForUpdate(object : AppUpdateHelper.AppUpdateAvailabilityListener {
            override fun onUpdateAvailable(appUpdateInfo: AppUpdateInfo) {
                Log.i("HomeActivity", "Update available")
                appUpdateHelper?.startImmediateUpdate(appUpdateInfo) {
                    mViewModel.setMessage(SnackbarMessage.init(it))
                }
            }

            override fun onUpdateNotAvailable() {
                Log.i("HomeActivity", "You're up to date!")
            }
        })
    }

    private fun promptAutoStart() {
        val autoStartPermissionHelper = AutoStartPermissionHelper.getInstance()
        val autoStartPrefManager = AutoStartPrefManager.newInstance(this)
        if (autoStartPermissionHelper.isAutoStartPermissionAvailable(this)) {
            if (!autoStartPrefManager.isAutoStartAlreadyEnabled()) {
                showBasicDialog(
                    title = "Auto Start",
                    message =
                    if (!autoStartPrefManager.isClickedOnSetting())
                        "Please enable auto start else notification feature won't work properly!"
                    else
                        "It's look like you have went to setting, if you have enabled AutoStart clicked on 'Already Enabled'",
                    positiveText = "Setting",
                    positiveClickCallback = {
                        autoStartPermissionHelper.getAutoStartPermission(this)
                        autoStartPrefManager.clickedOnSetting()
                    },
                    negativeText = "Cancel",
                    negativeClickCallback = {

                    },
                    neutralText = "Already Enabled",
                    neutralClickCallback = {
                        autoStartPrefManager.clickedOnAlreadyEnabled()
                    }
                )
            }
        }
    }

    private fun showNativeAdDialogWithDelay() {
        Handler().postDelayed({
            AdsManager.incrementCountAndShowNativeAdDialog(this)
        }, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdateHelper?.onActivityResult(requestCode, resultCode, data)
    }

    private fun deepLinkNotification() {
        val messagePayload: FBMessageService.MessagePayload? =
            Gson().fromJson(
                intent.extras?.getString(FBMessageService.EXTRA_NOTIFICATION_PAYLOAD),
                FBMessageService.MessagePayload::class.java
            )
        Log.i(
            TAG,
            "deepLinkNotification: ${intent.extras?.getString(FBMessageService.EXTRA_NOTIFICATION_PAYLOAD)}"
        )
        when (messagePayload?.deepLink) {
            FBMessageService.DEEP_LINK_TO_WORD_DETAILED -> {
                WordDetailedActivity.openActivity(this, messagePayload.date)
            }
            FBMessageService.DEEP_LINK_TO_WORD_LIST -> {
                WordListActivity.openActivity(this)
            }
            else -> {

            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

}

interface ViewCallbacks {
    fun readMore(v: View?, word: WordOfTheDay?)
    fun learnAll(v: View?)
}