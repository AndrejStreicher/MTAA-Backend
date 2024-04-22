package fiit.mtaa.yourslovakia

import org.ktorm.database.Database
import java.util.*

object DatabaseManager {
    val database: Database

    init {
        val properties = Properties()
        val classLoader = Thread.currentThread().contextClassLoader
        classLoader.getResourceAsStream("application.properties").use {
            properties.load(it)

            val url = System.getenv("DB_URL")
            val driver = "org.postgresql.Driver"
            val user = System.getenv("DB_USER")
            val password = System.getenv("DB_PASSWORD")

            database = Database.connect(url, driver, user, password)
        }
    }
}
