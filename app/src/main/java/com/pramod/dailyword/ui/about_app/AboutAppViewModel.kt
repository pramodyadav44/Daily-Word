package com.pramod.dailyword.ui.about_app

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pramod.dailyword.ui.BaseViewModel
import com.pramod.dailyword.util.Event

class AboutAppViewModel(application: Application) : BaseViewModel(application)
    , DeveloperLinkNavigate, OtherLinkNavigate, CreditLinkNavigate, AppLinkNavigate {

    private val navigateToAppGithubLinkLiveData = MutableLiveData<Event<Boolean>>()
    private val navigateToGooglePlayReviewLiveData = MutableLiveData<Event<Boolean>>()
    private val navigateToDonatePageLiveData = MutableLiveData<Event<Boolean>>()
    private val shareAppLiveData = MutableLiveData<Event<Boolean>>()

    private val navigateToDevGithubLiveData = MutableLiveData<Event<Boolean>>()
    private val navigateToDevFacebookLiveData = MutableLiveData<Event<Boolean>>()
    private val navigateToDevGmailLiveData = MutableLiveData<Event<Boolean>>()
    private val navigateToDevInstagramLiveData = MutableLiveData<Event<Boolean>>()

    private val showTermAndServiceLiveData = MutableLiveData<Event<Boolean>>()
    private val showPrivacyPolicyLiveData = MutableLiveData<Event<Boolean>>()
    private val showOpenSourceLibLiveData = MutableLiveData<Event<Boolean>>()

    private val navigateToFreePikWebsiteLiveData = MutableLiveData<Event<Boolean>>()
    private val navigateToFreeMaterialIconLiveData = MutableLiveData<Event<Boolean>>()



    override fun navigateToGithub() {
        navigateToDevGithubLiveData.value = Event.init(true)
    }

    override fun navigateToFacebook() {
        navigateToDevFacebookLiveData.value = Event.init(true)
    }

    override fun navigateToGmail() {
        navigateToDevGmailLiveData.value = Event.init(true)
    }

    override fun navigateToInstagram() {
        navigateToDevInstagramLiveData.value = Event.init(true)
    }

    override fun showTermsAndService() {
        showTermAndServiceLiveData.value = Event.init(true)
    }

    override fun showPrivacyPolicy() {
        showPrivacyPolicyLiveData.value = Event.init(true)
    }

    override fun showOpenSourceLibs() {
        showOpenSourceLibLiveData.value = Event.init(true)
    }

    override fun navigateToFreePikWebsite() {
        navigateToFreePikWebsiteLiveData.value = Event.init(true)
    }

    override fun navigateToMaterialDesignIcon() {
        navigateToFreeMaterialIconLiveData.value = Event.init(true)
    }


    override fun navigateToForkProject() {
        navigateToAppGithubLinkLiveData.value = Event.init(true)
    }

    override fun openGooglePlayReview() {
        navigateToGooglePlayReviewLiveData.value = Event.init(true)
    }

    override fun openDonatePage() {
        navigateToDonatePageLiveData.value = Event.init(true)
    }

    override fun shareApp() {
        shareAppLiveData.value = Event.init(true)
    }

    fun navigateToAppGithubLinkLiveData(): LiveData<Event<Boolean>> =
        navigateToAppGithubLinkLiveData

    fun navigateToDonatePageLiveData(): LiveData<Event<Boolean>> =
        navigateToDonatePageLiveData

    fun navigateToGooglePlayReviewLiveData(): LiveData<Event<Boolean>> =
        navigateToGooglePlayReviewLiveData

    fun shareAppLiveData(): LiveData<Event<Boolean>> =
        shareAppLiveData

    fun navigateToDevGithubLiveData(): LiveData<Event<Boolean>> = navigateToDevGithubLiveData
    fun navigateToDevFacebookLiveData(): LiveData<Event<Boolean>> = navigateToDevFacebookLiveData
    fun navigateToDevGmailLiveData(): LiveData<Event<Boolean>> = navigateToDevGmailLiveData
    fun navigateToDevInstagramLiveData(): LiveData<Event<Boolean>> = navigateToDevInstagramLiveData


    fun navigateToFreepikLiveData(): LiveData<Event<Boolean>> = navigateToFreePikWebsiteLiveData
    fun navigateToMaterialIconLiveData(): LiveData<Event<Boolean>> =
        navigateToFreeMaterialIconLiveData

    fun showTermAndConditionLiveData(): LiveData<Event<Boolean>> = showTermAndServiceLiveData
    fun showPrivacyPolicyLiveData(): LiveData<Event<Boolean>> = showPrivacyPolicyLiveData
    fun showOpenSourceLibsLiveData(): LiveData<Event<Boolean>> = showOpenSourceLibLiveData

}

interface AppLinkNavigate {
    fun navigateToForkProject()
    fun openGooglePlayReview()
    fun openDonatePage()
    fun shareApp()
}

interface DeveloperLinkNavigate {
    fun navigateToGithub()
    fun navigateToFacebook()
    fun navigateToGmail()
    fun navigateToInstagram()
}

interface OtherLinkNavigate {
    fun showTermsAndService()
    fun showPrivacyPolicy()
    fun showOpenSourceLibs()
}

interface CreditLinkNavigate {
    fun navigateToFreePikWebsite()
    fun navigateToMaterialDesignIcon()
}