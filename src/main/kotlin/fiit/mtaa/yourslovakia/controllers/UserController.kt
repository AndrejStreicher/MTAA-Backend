package fiit.mtaa.yourslovakia.controllers

import fiit.mtaa.yourslovakia.models.AuthenticationRequest
import fiit.mtaa.yourslovakia.services.TokenService
import fiit.mtaa.yourslovakia.services.UserAuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val tokenService: TokenService,
    private val userAuthenticationService: UserAuthenticationService
) {

    @PostMapping("/users/create_user")
    fun createUser(
        @RequestBody authRequest: AuthenticationRequest
    ): ResponseEntity<Boolean> {
        userAuthenticationService.createUser(authRequest.email, authRequest.password)
        if (userAuthenticationService.authenticateUser(authRequest.email, authRequest.password)) {
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.badRequest().build()
    }

    @PutMapping("/users/update_email")
    fun updateUserEmail(
        @RequestParam newEmail: String,
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<Boolean> {
        val token = authorization.substring("Bearer ".length)
        val oldEmail = tokenService.extractEmail(token) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        userAuthenticationService.updateUserEmail(oldEmail, newEmail)
        return if (userAuthenticationService.loadUserByUsername(newEmail) != null) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/users/update_password")
    fun updateUserPassword(
        @RequestParam newPlainTextPassword: String,
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<Boolean> {
        val token = authorization.substring("Bearer ".length)
        val email = tokenService.extractEmail(token) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        userAuthenticationService.updateUserPassword(email, newPlainTextPassword)
        return if (userAuthenticationService.authenticateUser(email, newPlainTextPassword)) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/users/delete_user")
    fun deleteUser(
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<Boolean> {
        val token = authorization.substring("Bearer ".length)
        val email = tokenService.extractEmail(token) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        userAuthenticationService.deleteUser(email)
        return if (userAuthenticationService.loadUserByUsername(email) == null) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.badRequest().build()
        }
    }
}
