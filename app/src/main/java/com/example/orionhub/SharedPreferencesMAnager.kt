import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesMAnager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    var username: String?
        get() = prefs.getString("USERNAME_KEY", null)
        set(value) = prefs.edit().putString("USERNAME_KEY", value).apply()
}