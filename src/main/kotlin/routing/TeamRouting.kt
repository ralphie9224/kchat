package routing

import Locations
import Stores.TeamObj
import Stores.TeamStore
import com.sun.media.jfxmedia.logging.Logger
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route

data class ListResponse<T>(val status: String, val reason: String, val data: List<T>?)
data class CreateResponse(val status: String, val reason: String, val id: String?)

object TeamRouting{
    fun Route.createTeam() {
        post<Locations.Teams> {
            val params = call.receive<Parameters>()
            Logger.logMsg(Logger.INFO, "Create a Team ")
            val requiredParams = listOf("name")
            val missingFields: List<String> =
                    requiredParams.filter {
                        params[it].isNullOrBlank()
                    }
            if (missingFields.isNotEmpty()) {
                val response = missingFields.joinToString(separator = ", ")
                call.respond(CreateResponse("failed", "Missing Fields: $response", null))
            } else {
                val teamId: String? = TeamStore.createTeam(params)
                call.respond(CreateResponse("success", "Successfully created a Team", teamId))
            }
        }
        get<Locations.Teams>{
            val teams= TeamStore.getAll();
            call.respond(ListResponse<TeamObj>("success", "Successfully retrieved all Teams", teams))
        }
    }
    fun Route.getTeam(){
        get<Locations.Team>{
            val uuid = call.parameters["uuid"]
            val team = TeamStore.get(uuid)
            call.respond(CreateResponse("success", "Successfully retrieved a channel", team))
        }
    }
}