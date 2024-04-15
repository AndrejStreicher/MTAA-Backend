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

            val url = properties.getProperty("spring.datasource.url")
            val driver = properties.getProperty("spring.datasource.driver-class-name")
            val user = properties.getProperty("spring.datasource.username")
            val password = properties.getProperty("spring.datasource.password")

            database = Database.connect(url, driver, user, password)
        }
    }
}
