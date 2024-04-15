package fiit.mtaa.yourslovakia.repositories

import fiit.mtaa.yourslovakia.DatabaseManager
import fiit.mtaa.yourslovakia.models.User
import fiit.mtaa.yourslovakia.models.Users
import jakarta.transaction.Transactional
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository

@Repository
class UserRepository {

    fun getAllUsers(): List<User> {
        return DatabaseManager.database.from(Users).select().map { row ->
            User(
                id = row[Users.id]!!,
                email = row[Users.email]!!,
                password = row[Users.password]!!
            )
        }
    }

    @Transactional
    fun insertUser(user: User) {
        DatabaseManager.database.insert(Users) {
            set(it.id, user.id)
            set(it.email, user.email)
            set(it.password, user.password)
        }
    }

    @Transactional
    fun updateUser(user: User) {
        DatabaseManager.database.update(Users) {
            set(it.id, user.id)
            set(it.email, user.email)
            set(it.password, user.password)
            where { it.id eq user.id }
        }
    }

    @Transactional
    fun deleteUser(user: User) {
        DatabaseManager.database.delete(Users) {
            it.id eq user.id
        }
    }

}