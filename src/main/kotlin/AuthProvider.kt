import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.*
import java.util.*

object AuthProvider {
    const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
    val JSON_FACTORY = JacksonFactory.getDefaultInstance()
  //  const val TOKENS_DIRECTORY_PATH = "tokens"

    private val SCOPES = listOf(
        SheetsScopes.SPREADSHEETS,
        SheetsScopes.DRIVE,
        SheetsScopes.DRIVE_FILE
    )

    @Throws(IOException::class)
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val googleRepository = GoogleRepository()
        val prop = Properties()
       AuthProvider::class.java.classLoader.getResourceAsStream("config.properties").use{
           if (it==null) println("No configs found")
           prop.load(it)
       }
        val input =prop.getProperty("serviceCredentials") ?: throw FileNotFoundException("Resource not found")
        val clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(ByteArrayInputStream(input.toByteArray())))
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(GoogleDataStoreFactory(googleRepository))
          //  .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
          //  .setCredentialCreatedListener(CredentilaCreatedListenerImpl())
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}