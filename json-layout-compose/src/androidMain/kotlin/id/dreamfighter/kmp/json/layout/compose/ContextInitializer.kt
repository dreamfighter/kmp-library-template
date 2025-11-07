package id.dreamfighter.kmp.json.layout.compose

import android.content.Context
import androidx.startup.Initializer

lateinit var applicationContext: Context
    private set

public object AppContext

class ContextInitializer : Initializer<AppContext> {
    override fun create(context: Context): AppContext {
        applicationContext = context.applicationContext
        return AppContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}