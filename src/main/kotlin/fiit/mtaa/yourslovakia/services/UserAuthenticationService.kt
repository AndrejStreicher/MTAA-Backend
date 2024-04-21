package fiit.mtaa.yourslovakia.services

import fiit.mtaa.yourslovakia.models.User
import fiit.mtaa.yourslovakia.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserAuthenticationService(private val userRepository: UserRepository) : UserDetailsService {
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

    fun hashPassword(plainPassword: String): String {
        return argon2PasswordEncoder.encode(plainPassword)
    }

    fun authenticateUser(email: String, submittedPassword: String): Boolean {
        val user = userRepository.getUserByEmail(email)
        if (user != null) {
            return argon2PasswordEncoder.matches(submittedPassword, user.password)
        }
        return false
    }

    fun createUser(email: String, plainTextPassword: String) {
        val newUser = User(10, email, hashPassword(plainTextPassword))
        userRepository.insertUser(newUser)
    }

    fun updateUserEmail(oldEmail: String, newEmail: String) {
        val currentUser = userRepository.getUserByEmail(oldEmail)
        val newUser = currentUser?.let { User(it.id, newEmail, it.password) }
        if (newUser != null) {
            userRepository.updateUser(newUser)
        }
    }

    fun updateUserPassword(email: String, newPlainTextPassword: String) {
        val currentUser = userRepository.getUserByEmail(email)
        val newUser = currentUser?.let { User(it.id, email, hashPassword(newPlainTextPassword)) }
        if (newUser != null) {
            userRepository.updateUser(newUser)
        }
    }

    override fun loadUserByUsername(email: String): UserDetails {
        val user =
            userRepository.getUserByEmail(email) ?: throw UsernameNotFoundException("User not found with email: $email")
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.email)
            .password(user.password)
            .roles("USER")
            .build()
    }

    fun deleteUser(email: String) {
        val user = userRepository.getUserByEmail(email)
        if (user != null) {
            userRepository.deleteUser(user)
        }
    }
}