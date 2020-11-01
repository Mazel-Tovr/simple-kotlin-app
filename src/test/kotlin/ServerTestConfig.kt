import api.db.ConnectionToTestDB
import com.epam.kotlinapp.chat.server.Server
import com.epam.kotlinapp.chat.server.webSocket
import com.epam.kotlinapp.crud.business.ProductGroupService
import com.epam.kotlinapp.crud.business.ProductService
import com.epam.kotlinapp.crud.business.UserService
import com.epam.kotlinapp.crud.controllers.productController
import com.epam.kotlinapp.crud.controllers.productGroupController
import com.epam.kotlinapp.crud.controllers.userController
import com.epam.kotlinapp.crud.dao.ProductGroupOperations
import com.epam.kotlinapp.crud.dao.ProductOperations
import com.epam.kotlinapp.crud.dao.UserOperations
import de.nielsfalk.ktor.swagger.SwaggerSupport
import de.nielsfalk.ktor.swagger.version.v2.Swagger
import de.nielsfalk.ktor.swagger.version.v3.OpenApi
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


//Server for testing
fun Application.main() {

    val server = Server()

    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(SwaggerSupport) {
        swagger = Swagger().apply {}
        openApi = OpenApi().apply {}
    }
    install(WebSockets)
    //setting con to test bd
    setConn(ProductOperations)
    setConn(ProductGroupOperations)
    setConn(UserOperations)


    routing {
        this.userController(UserService)
        this.productController(ProductService)
        this.productGroupController(ProductGroupService)
        this.webSocket(server)
    }
}

private fun setConn(someImpl: Any) {
    val kClass = Class.forName(someImpl.javaClass.name).kotlin
    val member = kClass.memberProperties.filterIsInstance<KMutableProperty<*>>()
        .firstOrNull { it.name == "conn" }
    if (member != null) {
        member.isAccessible = true
        member.setter.call(ConnectionToTestDB.conn)
    }
}