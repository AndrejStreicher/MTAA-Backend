package fiit.mtaa.yourslovakia.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserAuthenticationServiceTest {
    @Autowired
    private lateinit var userAuthenticationService: UserAuthenticationService

}