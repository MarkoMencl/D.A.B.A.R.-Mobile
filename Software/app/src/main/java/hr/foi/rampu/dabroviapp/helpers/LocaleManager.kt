import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import hr.foi.rampu.dabroviapp.R
import java.util.*

object LocaleManager {

    private const val PREFS_NAME = "app_preferences"
    private const val KEY_LANGUAGE = "language_code"

    fun updateLocale(context: Context, languageCode: String?): Context {
        val locale = if (languageCode.isNullOrEmpty()) {
            Locale.ENGLISH
        } else {
            Locale(languageCode)
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()

        return context.createConfigurationContext(config)
    }

    fun applyLocale(context: Context, languageCode: String?) {
        val updatedContext = updateLocale(context, languageCode)

        context.resources.updateConfiguration(
            updatedContext.resources.configuration,
            updatedContext.resources.displayMetrics
        )
    }

    fun setLocaleAndRecreateActivity(
        activity: androidx.appcompat.app.AppCompatActivity,
        languageCode: String?
    ) {
        applyLocale(activity, languageCode)

        activity.recreate()
    }
}
