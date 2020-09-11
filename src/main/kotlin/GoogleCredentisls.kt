import org.jetbrains.exposed.dao.UUIDTable

object GoogleCredentisls : UUIDTable("google_credentilas") {
    val key = text("key")
    val accessToken = text("access_token")
    val refreshToken = text("refresh_token")
    val expirationTime = long("expires")
}