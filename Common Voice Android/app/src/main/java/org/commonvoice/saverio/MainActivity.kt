package org.commonvoice.saverio

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_settings.*
import org.commonvoice.saverio.ui.home.HomeFragment
import org.commonvoice.saverio.ui.settings.SettingsFragment
import org.commonvoice.saverio.ui.settings.SettingsViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var firstRun = true
    private val RECORD_REQUEST_CODE = 101
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "FIRST_RUN"
    private val LANGUAGE_NAME = "LANGUAGE"
    private val LOGGED_IN_NAME = "LOGGED" //false->no logged-in || true -> logged-in
    private val USER_CONNECT_ID = "USER_CONNECT_ID"
    private val USER_NAME = "USERNAME"

    var languages_list_short =
        arrayOf("en") // don't change manually -> it's imported from strings.xml
    var languages_list =
        arrayOf("English") // don't change manually -> it's imported from strings.xml
    var selected_language = ""
    var logged: Boolean = false
    var user_id: String = ""
    var user_name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        this.firstRun = sharedPref.getBoolean(PREF_NAME, true)

        // import languages from array
        this.languages_list = resources.getStringArray(R.array.languages)
        this.languages_list_short = resources.getStringArray(R.array.languages_short)

        val sharedPref2: SharedPreferences = getSharedPreferences(LANGUAGE_NAME, PRIVATE_MODE)
        this.selected_language = sharedPref2.getString(LANGUAGE_NAME, "en")

        val sharedPref3: SharedPreferences = getSharedPreferences(LOGGED_IN_NAME, PRIVATE_MODE)
        this.logged = sharedPref3.getBoolean(LOGGED_IN_NAME, false)

        if (logged) {
            val sharedPref4: SharedPreferences = getSharedPreferences(USER_CONNECT_ID, PRIVATE_MODE)
            this.user_id = sharedPref4.getString(USER_CONNECT_ID, "")

            val sharedPref5: SharedPreferences = getSharedPreferences(USER_NAME, PRIVATE_MODE)
            this.user_name = sharedPref5.getString(USER_NAME, "")

            loginSuccessful()
        }

        if (this.firstRun) {
            // close main and open tutorial -- first run
            open_tutorial()
        } else {
            checkRecordVoicePermission()
        }

        //get_language()
    }

    fun get_language() {
        Toast.makeText(
            this,
            "Language: " + this.selected_language + " index: " + languages_list_short.indexOf(this.selected_language),
            Toast.LENGTH_LONG
        ).show()
    }

    fun loginSuccessful() {
        //login successful -> show username and log-out button
        /*Toast.makeText(
            this,
            "Login successful!",
            Toast.LENGTH_LONG
        ).show()*/
    }

    fun setLanguageSettings(lang: String) {
        val sharedPref: SharedPreferences = getSharedPreferences(LANGUAGE_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString(LANGUAGE_NAME, lang)
        editor.apply()

        this.selected_language = lang
    }

    fun getLanguageList(): ArrayAdapter<String> {
        return ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languages_list)
    }

    fun getSelectedLanguage(): String {
        return this.selected_language
    }

    fun open_tutorial() {
        val intent = Intent(this, TutorialActivity::class.java).also {
            startActivity(it)
        }
        finish()
    }

    fun open_speak_section() {
        val intent = Intent(this, SpeakActivity::class.java).also {
            startActivity(it)
        }
    }

    fun open_listen_section() {
        val intent = Intent(this, ListenActivity::class.java).also {
            startActivity(it)
        }
    }

    fun open_login_section() {
        val intent = Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            //close the MainActivity
            finish()
        }
    }

    fun open_logout_section() {
        // logout -> delete USERNAME, USERID e LOGGEDIN variables (shared)
        val intent = Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            //close the MainActivity
            finish()
        }
    }

    fun checkRecordVoicePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    checkRecordVoicePermission()
                }
            }
        }
    }
}
