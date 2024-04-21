package fiit.mtaa.yourslovakia.configurations

import fiit.mtaa.yourslovakia.models.JwtProperties
import fiit.mtaa.yourslovakia.repositories.UserRepository
import fiit.mtaa.yourslovakia.services.UserAuthenticationService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfiguration {
    private var saltLength = 16

    private var hashLength = 32

    private var parallelism = 1

    private var memory = 4096
    private var iterations = 3
    private val argon2PasswordEncoder = Argon2PasswordEncoder(
        saltLength,
        hashLength,
        parallelism,
        memory,
        iterations
    )

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService =
        UserAuthenticationService(userRepository)

    @Bean
    fun encoder(): PasswordEncoder = argon2PasswordEncoder

    @Bean
    fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userDetailsService(userRepository))
                it.setPasswordEncoder(encoder())
            }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
}
