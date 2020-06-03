package com.pramod.todaysword.ui.word_details

import com.pramod.todaysword.BR
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.pramod.todaysword.databinding.ActivityWordDetailedBinding
import com.pramod.todaysword.R
import com.pramod.todaysword.db.model.WordOfTheDay
import com.pramod.todaysword.ui.BaseActivity

class WordDetailedActivity : BaseActivity<ActivityWordDetailedBinding, WordDetailedViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initEnterAndReturnTransition()
        super.onCreate(savedInstanceState)

    }

    private fun initEnterAndReturnTransition() {

        val enterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content);
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            duration = 300
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            interpolator = FastOutSlowInInterpolator();
            /*        containerColor =
                        CommonUtils.resolveAttrToColor(
                            this@WordDetailedActivity,
                            android.R.attr.windowBackground
                        )*/
        }

        val returnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content);
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            duration = 300;
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            interpolator = FastOutSlowInInterpolator();
            /*containerColor =
                CommonUtils.resolveAttrToColor(
                    this@WordDetailedActivity,
                    android.R.attr.windowBackground
                )*/
        }


        findViewById<View>(android.R.id.content).transitionName = "CONTAINER";
        window.sharedElementEnterTransition = enterTransition;
        window.sharedElementReturnTransition = returnTransition;
        window.sharedElementsUseOverlay = false;
        val fade = Fade()
        fade.duration = 150
        window.returnTransition = fade
        window.enterTransition = fade
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_word_detailed
    }

    override fun getViewModel(): WordDetailedViewModel {
        val word = intent.extras!!.getSerializable("WORD") as WordOfTheDay

        return ViewModelProviders.of(this, WordDetailedViewModel.Factory(application, word))
            .get(WordDetailedViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.wordDetailedViewModel
    }

}
