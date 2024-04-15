package fiit.mtaa.yourslovakia.controllers

import fiit.mtaa.yourslovakia.services.UserAuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(private val userAuthenticationService: UserAuthenticationService) {
    @PostMapping("/users/create_user")
    fun createUser(
            @RequestParam email: String,
            @RequestParam plainTextPassword: String,
    ): ResponseEntity<Boolean> {
        userAuthenticationService.createUser(email, plainTextPassword)
        if (userAuthenticationService.authenticateUser(email, plainTextPassword)) {
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.badRequest().build()
    }

    @PutMapping("/users/update_email")
    fun updateUserEmail(
            @RequestParam oldEmail: String,
            @RequestParam newEmail: String,
    ): ResponseEntity<Boolean> {
        userAuthenticationService.updateUserEmail(oldEmail, newEmail)
        if (userAuthenticationService.findUserByEmail(newEmail) != null) {
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.badRequest().build()
    }

    @PutMapping("/users/update_password")
    fun updateUserPassword(
            @RequestParam email: String,
            @RequestParam newPlainTextPassword: String,
    ): ResponseEntity<Boolean> {
        userAuthenticationService.updateUserPassword(email, newPlainTextPassword)
        if (userAuthenticationService.authenticateUser(email, newPlainTextPassword) != null) {
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/users/delete_user")
    fun deleteUser(
            @RequestParam email: String,
    ): ResponseEntity<Boolean> {
        userAuthenticationService.deleteUser(email)
        if (userAuthenticationService.findUserByEmail(email) == null) {
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.badRequest().build()
    }
}
