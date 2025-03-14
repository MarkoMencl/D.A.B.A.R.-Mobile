import android.app.Application
import android.content.Context
import hr.foi.rampu.dabroviapp.helpers.SessionManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        val languageCode = SessionManager.getUser()?.language
        val contextWithLocale = LocaleManager.updateLocale(base, languageCode)
        super.attachBaseContext(contextWithLocale)
    }
}
