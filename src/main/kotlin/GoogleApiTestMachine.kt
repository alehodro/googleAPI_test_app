import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange


class GoogleApiTestMachine(

) {


    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val sheetsService = Sheets.Builder(
        HTTP_TRANSPORT,
        AuthProvider.JSON_FACTORY,
        AuthProvider.getCredentials(HTTP_TRANSPORT)
    )
        .setApplicationName(AuthProvider.APPLICATION_NAME)
        .build()


    fun readValues(spreadsheetId: String, range: String) {
        val response: ValueRange = sheetsService.spreadsheets().values()[spreadsheetId, range]
            .execute()
        val values: List<List<Any>> = response.getValues()
        if (values == null || values.isEmpty()) {
            println("No data found.")
        } else {
            println("Name, Major")
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                println(row.joinToString(" "))

            }
        }
    }

    fun appendValues() {


    }

    fun createTable() {

    }
}
