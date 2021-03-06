package com.pramod.dailyword.ui.home

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import androidx.lifecycle.*
import com.pramod.dailyword.BuildConfig
import com.pramod.dailyword.SnackbarMessage
import com.pramod.dailyword.db.Resource
import com.pramod.dailyword.db.repository.WOTDRepository
import com.pramod.dailyword.db.model.WordOfTheDay
import com.pramod.dailyword.helper.PrefManager
import com.pramod.dailyword.helper.PronounceHelper
import com.pramod.dailyword.ui.BaseViewModel
import com.pramod.dailyword.util.CommonUtils
import com.pramod.dailyword.util.Event
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val wordOfTheDayRepo: WOTDRepository =
        WOTDRepository(application)

    private val title = MutableLiveData<String>().apply {
        value = CommonUtils.getGreetMessage()
    }

    fun title(): LiveData<String> = title

    private val wordOfTheDayLoading: MutableLiveData<Event<Boolean>> = MutableLiveData()


    private val refreshDataSourceLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    fun refreshDataSourceLiveData(): LiveData<Event<Boolean>> = refreshDataSourceLiveData

    private var wordOfTheDayLiveData: LiveData<WordOfTheDay?>
    private var wordsExceptTodayLiveData: LiveData<List<WordOfTheDay>?>

    var wordResourceLiveData: LiveData<Resource<List<WordOfTheDay>?>>

    var firstNotificationShown = false

    init {
        wordOfTheDayLoading.value = Event.init(true)
        wordResourceLiveData = Transformations.switchMap(refreshDataSourceLiveData) {
            return@switchMap it.getContentIfNotHandled()?.let {
                wordOfTheDayRepo.getWords()
            }
        }
        wordOfTheDayLiveData = Transformations.map(wordResourceLiveData) {
            wordOfTheDayLoading.value = Event.init(it.status == Resource.Status.LOADING)
            if (it.status != Resource.Status.LOADING) {
                Handler().postDelayed({
                    title.postValue(
                        "Here's your Daily Word"
                    )
                }, 2000)
            }
            if (it.status == Resource.Status.ERROR) {
                it.message?.let { message ->
                    setMessage(SnackbarMessage.init(message))
                }
            }
            return@map if (it.data != null && it.data.isNotEmpty()) it.data[0] else null
        }

        wordsExceptTodayLiveData = Transformations.map(wordResourceLiveData) {
            val list = it.data?.toMutableList()
            if (list != null && list.isNotEmpty()) {
                list.removeAt(0)
            }
            return@map list
        }

        /*wordOfTheDayRepo.initGetWordOfTheDayWorker()
        wordOfTheDayWorkerLiveData = wordOfTheDayRepo.getDailyWordOfTheDayWorker()

        wordOfTheDayRepo.initRemindWordOfTheDay()
        newWordReminderWorkerLiveData = wordOfTheDayRepo.getRemindWordOfTheDay()*/
    }

    fun refreshDataSource() {
        refreshDataSourceLiveData.value = Event.init(true)
    }

    fun getTodaysWordOfTheDay(): LiveData<WordOfTheDay?> {
        return wordOfTheDayLiveData
    }


    fun getWordsExceptToday(): LiveData<List<WordOfTheDay>?> {
        return wordsExceptTodayLiveData
    }

    fun pronounceWord(url: String) {
        Log.d("AUDIO URL", url)
        PronounceHelper.playAudio(url)
    }

    fun updateWordSeenStatus(word: WordOfTheDay) {
        viewModelScope.launch {
            wordOfTheDayRepo.updateWord(word)
        }
    }

    val showChangelogActivity: LiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>().apply {
        val prefManager = PrefManager(application)
        value = if (prefManager.getLastAppVersion() < BuildConfig.VERSION_CODE) {
            prefManager.updateAppVersion()
            Event.init(true)
        } else {
            Event.init(false)
        }
    }
}