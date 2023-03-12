package com.example.assistedselfietest

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.LocaleListCompat
import it.infocert.assistedselfiesdk.exceptions.AssistedSelfieException
import it.infocert.assistedselfiesdk.init.AssistedSelfie
import it.infocert.assistedselfiesdk.models.input.AssistedSelfieCheck
import it.infocert.sdk.android.idtools.models.input.IdToolsLanguage
import java.util.*

open class BaseActivitySelfie : AppCompatActivity() {

    private var requestIntent: Intent? = null
    private val languageCode = getSupportedLanguageIso()

    protected fun startActivitySelfie(
        assistedSelfieLanguage: IdToolsLanguage = IdToolsLanguage(languageCode),
        timeOutForCheckStep: Long = DEFAULT_TIME_OUT_FOR_CHECK_STEP,
        backgroundTransparency: Int = DEFAULT_BACKGROUND_TRANSPARENCY,
        turnRightControl: Boolean = true,
        turnLeftControl: Boolean = true,
        closeEyesControl: Boolean = true,
        smilingControl: Boolean = true,
        tiltRightControl: Boolean = true,
        tiltLeftControl: Boolean = true
    ) {
        val list = buildListControls(
            turnRightControl, turnLeftControl, closeEyesControl,
            smilingControl, tiltRightControl, tiltLeftControl
        )
        val intent = try {
            AssistedSelfie.initializer()
                .setCallingActivity(this) //required
                //The list can be empty. In this case only the Aligned Selfie will be performed. The checks will be
                //required to the user with the same order that they have in this list.
                .setChecksToPerform(list) //required
                //useful for activating the Continuous Face Tracking.
                //Can be set to true if and only if assistedSelfieCheckArrayList is not empty.
                .enableContinuousFaceTracking() //optional
                .setLanguage(assistedSelfieLanguage) //optional
                //Timeout value in ms for each Liveness check chosen, default 5000
                .setTimeOutForCheckStep(timeOutForCheckStep) //optional
                //make background of the Camera Activity transparent by that percentage. default 4
                .setBackgroundTransparency(backgroundTransparency) //optional
                //Parameter useful to show the start button for the liveness process. If this parameter is set to false the
                //process starts automatically.
                .enableStartButton() //optional
                //Default is French(string.xml main) language
                .init()
        } catch (e: AssistedSelfieException) {
            e.printStackTrace()
            null
        }
        intent?.let {
            Log.d("Started language", languageCode)
            requestIntent = intent
        }
    }

    private fun buildListControls(
        turnRightControl: Boolean,
        turnLeftControl: Boolean,
        closeEyesControl: Boolean,
        smilingControl: Boolean,
        tiltRightControl: Boolean,
        tiltLeftControl: Boolean
    ): ArrayList<AssistedSelfieCheck> = ArrayList<AssistedSelfieCheck>().apply {
        if (turnRightControl) add(AssistedSelfieCheck.ROTATE_RIGHT)
        if (turnLeftControl) add(AssistedSelfieCheck.ROTATE_LEFT)
        if (closeEyesControl) add(AssistedSelfieCheck.BLINK)
        if (smilingControl) add(AssistedSelfieCheck.SMILE)
        if (tiltRightControl) add(AssistedSelfieCheck.TILT_RIGHT)
        if (tiltLeftControl) add(AssistedSelfieCheck.TILT_LEFT)
    }

    fun startActivityWithThreeRandomActions(
        assistedSelfieLanguage: IdToolsLanguage = IdToolsLanguage(languageCode),
        timeOutForCheckStep: Long = DEFAULT_TIME_OUT_FOR_CHECK_STEP,
        backgroundTransparency: Int = DEFAULT_BACKGROUND_TRANSPARENCY
    ) {
        //Choose randomly only three from the six selfie control options
        val tabOptionsCheck = listOf(true, true, true, false, false, false).shuffled()
        startActivitySelfie(
            assistedSelfieLanguage = assistedSelfieLanguage,
            timeOutForCheckStep = timeOutForCheckStep,
            backgroundTransparency = backgroundTransparency,
            turnRightControl = tabOptionsCheck[0],
            turnLeftControl = tabOptionsCheck[1],
            closeEyesControl = tabOptionsCheck[2],
            smilingControl = tabOptionsCheck[3],
            tiltRightControl = tabOptionsCheck[4],
            tiltLeftControl = tabOptionsCheck[5]
        )
    }

    private fun getSupportedLanguageIso(): String {
        val supportedLanguageIsoList = listOf(
            Locale.FRENCH.language,
            Locale.GERMAN.language
        )
        val current = LocaleListCompat.getDefault()[0]?.language ?: Locale.getDefault().language
        return supportedLanguageIsoList.find { current.startsWith(it) } ?: supportedLanguageIsoList[0]
    }

    companion object {
        //Spec: change the default timeout for selfie check to 10s
        private const val DEFAULT_TIME_OUT_FOR_CHECK_STEP = 10000L
        private const val DEFAULT_BACKGROUND_TRANSPARENCY = 4
    }
}
