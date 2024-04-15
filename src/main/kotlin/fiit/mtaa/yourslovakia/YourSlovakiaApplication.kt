package fiit.mtaa.yourslovakia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class YourSlovakiaApplication

fun main(args: Array<String>) {
    runApplication<YourSlovakiaApplication>(*args)
}
