import com.github.jdw.seaofshadows.Terminal
import com.github.jdw.seaofshadows.core.Settings
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@JsName("SeaOfShadowsMain")
suspend fun main() {
    println("Starting JS main...")
    var s: Settings? = null
    Terminal()
        .also { t ->
            s = t.fetchSettings("${window.location.protocol}/seaofshadows/settings")
        }
        .also {
            val format = Json {
                prettyPrint = true
                encodeDefaults = true
            }
            println(format.encodeToString(s))
        }
        .also { t ->
            t.run(s!!)
        }

}