package stores

import DatabaseFactory
import io.ktor.http.Parameters
import models.User
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.joda.time.DateTime
import java.util.*

data class UsersObj(
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val id: String,
        val createdAt: String)

object UserStore {
    suspend fun registerUser(params: Parameters): String? {
        var userId = ""
        DatabaseFactory.dbQuery {
            userId = (User.insert {
                it[password] = params["password"]!!
                it[username] = params["username"]!!
                it[firstName] = params["firstName"]!!
                it[lastName] = params["lastName"]!!
                it[email] = params["email"]!!
                it[id] = UUID.randomUUID().toString()
                it[createdAt] = DateTime.now()
            } get User.id)
        }

        return userId
    }

    suspend fun isNewUser(params: Parameters): Boolean = getUser(params["email"]) == null

    private suspend fun getUser(email: String?): Query? {
        return DatabaseFactory.dbQuery {
            User.select { User.email.eq(email!!) }.takeIf { !it.empty() }
        }
    }

    suspend fun getAllUsers(): List<UsersObj> {
        return DatabaseFactory.dbQuery {
            User.selectAll().map {
                return@map UsersObj(
                        it[User.username],
                        it[User.firstName],
                        it[User.lastName],
                        it[User.email],
                        it[User.id],
                        it[User.createdAt].toString()
                )
            }
        }
    }
}
