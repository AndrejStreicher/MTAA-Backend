package fiit.mtaa.yourslovakia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableScheduling
@EnableWebSecurity
class YourSlovakiaApplication

fun main(args: Array<String>) {
    runApplication<YourSlovakiaApplication>(*args)
}
