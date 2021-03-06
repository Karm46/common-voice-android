package org.commonvoice.saverio.ui.settings

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.commonvoice.saverio.BuildConfig
import org.commonvoice.saverio.MainActivity
import org.commonvoice.saverio.R


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    var languagesListShort =
        arrayOf("en") // don't change it manually -> it will import automatically
    var languagesList =
        arrayOf("English") // don't change it manually -> it will import automatically

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        var main = (activity as MainActivity)
        main.dashboard_selected = false

        val textLanguage: TextView = root.findViewById(R.id.text_settingsLanguage)
        textLanguage.text = getText(R.string.settingsLanguage)
        //val model = ViewModelProviders.of(activity!!).get(SettingsViewModel::class.java)

        var releaseNumber: TextView = root.findViewById(R.id.textRelease)
        releaseNumber.text = BuildConfig.VERSION_NAME

        // import the languages list (short and "standard" from mainactivity)
        this.languagesListShort = main.languagesListShortArray
        this.languagesList = main.languagesListArray

        var language: Spinner = root.findViewById(R.id.languageList)
        language.adapter = main.getLanguageList()

        var selectedLanguage: String = main.getSelectedLanguage()

        language.setSelection(languagesListShort.indexOf(selectedLanguage))

        language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                main.setLanguageSettings(languagesListShort.get(position))
            }
        }

        var textProjectGithub: TextView = root.findViewById(R.id.textProjectGitHub)
        textProjectGithub.setOnClickListener {
            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Sav22999/common-voice-android")
                )
            startActivity(browserIntent)
        }
        textProjectGithub.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        var textDonatePaypal: TextView = root.findViewById(R.id.textDonatePayPal)
        textDonatePaypal.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/saveriomorelli"))
            startActivity(browserIntent)
        }
        textDonatePaypal.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        var btnOpenTutorial: Button = root.findViewById(R.id.buttonOpenTutorial)
        btnOpenTutorial.setOnClickListener {
            main.openTutorial()
        }

        var btnWebBrowserForTest: Button = root.findViewById(R.id.buttonOpenWBTests)
        btnWebBrowserForTest.setOnClickListener {
            main.openWebBrowserForTest()
        }

        if (main.logged && (main.userName == "Sav22999" || main.userName == "Common Voice Android")) {
            btnWebBrowserForTest.isGone = false
        }

        var txtContributors: TextView = root.findViewById(R.id.textContributors)
        txtContributors.text = getString(R.string.txt_contributors)

        var txtDevelopedBy: TextView = root.findViewById(R.id.textDevelopedBy)
        txtDevelopedBy.text = getString(R.string.txt_developed_by)

        main.checkConnection()

        return root
    }
}