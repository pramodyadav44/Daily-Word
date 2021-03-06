package com.pramod.dailyword.ui.word_details

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.pramod.dailyword.SnackbarMessage
import com.pramod.dailyword.db.Resource
import com.pramod.dailyword.db.model.WordOfTheDay
import com.pramod.dailyword.db.repository.BookmarkRepo
import com.pramod.dailyword.db.repository.WOTDRepository
import com.pramod.dailyword.ui.BaseViewModel
import com.pramod.dailyword.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WordDetailedViewModel(application: Application, private val wordOfTheDay: WordOfTheDay) :
    BaseViewModel(application) {

    companion object {
        val TAG = WordDetailedViewModel::class.simpleName
    }

    private val bookmarkRepo = BookmarkRepo(application)
    private val wordOfTheDayRepo = WOTDRepository(application)

    private val retryEventLiveData = MutableLiveData<Event<Boolean>>()

    val loadingLiveData = MutableLiveData<Boolean>()
    val showWord = MutableLiveData<Boolean>()

    fun retry() {
        retryEventLiveData.value = Event.init(true)
    }

    val wordOfTheDayLiveData: MediatorLiveData<WordOfTheDay> = MediatorLiveData()

    init {
        wordOfTheDayLiveData.value = wordOfTheDay
        //fetch word and listen to changes
        var resourceLiveData = wordOfTheDayRepo.getWord(wordOfTheDay.date!!)

        wordOfTheDayLiveData.addSource(retryEventLiveData) {
            if (wordOfTheDay.word == null) {
                wordOfTheDayLiveData.removeSource(resourceLiveData)
                resourceLiveData = wordOfTheDayRepo.getWord(wordOfTheDay.date!!)
                wordOfTheDayLiveData.addSource(resourceLiveData) {
                    loadingLiveData.value = it.status == Resource.Status.LOADING
                    if (it.status == Resource.Status.ERROR) {
                        it.message?.let { message ->
                            setMessage(SnackbarMessage.init(message))
                        }
                    }
                    showWord.value = it.data != null
                    wordOfTheDayLiveData.value = it.data
                }
            } else {
                showWord.value = true
                wordOfTheDayLiveData.addSource(wordOfTheDayRepo.getWord(wordOfTheDay.date!!)) {
                    loadingLiveData.value = it.status == Resource.Status.LOADING
                    wordOfTheDayLiveData.value = it.data
                }
            }
        }
        retry()

    }

    private val showTitle = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val navigateToMerriamWebster = MutableLiveData<Event<String>>()


    fun navigateToWordMW(url: String) {
        navigateToMerriamWebster.value = Event.init(url)
    }

    fun setTitleVisibility(show: Boolean) {
        showTitle.value = show
    }

    fun showTitle(): LiveData<Boolean> = showTitle

    fun pronounceWord(url: String) {
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            mediaPlayer.setOnPreparedListener {
                it.start()
            }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            Log.d("AUDIO URL", url)
            Log.d("AUDIO ERROR", e.toString())
        }
    }

    fun navigateToMerriamWebster(): LiveData<Event<String>> = navigateToMerriamWebster

    fun bookmark() {
        GlobalScope.launch(Dispatchers.Main) {
            val wordOfTheDay = wordOfTheDayLiveData.value
            if (wordOfTheDay != null) {
                bookmarkRepo.bookmarkToggle(wordOfTheDay.word!!)
                Log.i(TAG, "bookmark: not null")
            } else {
                setMessage(SnackbarMessage.init("Word details is not loaded yet! Please wait..."))
                Log.i(TAG, "bookmark: Word details is not loaded yet! Please wait...")
            }
        }
    }

    class Factory(
        private val application: Application,
        private val word: WordOfTheDay
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WordDetailedViewModel(application, word) as T
        }

    }

    override fun onCleared() {
        super.onCleared()
    }

}