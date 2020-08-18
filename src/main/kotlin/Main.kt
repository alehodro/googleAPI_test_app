import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange


/*object SheetsQuickstart {
    private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private const val TOKENS_DIRECTORY_PATH = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES: List<String> =
        Collections.singletonList(SheetsScopes.SPREADSHEETS)
    private const val CREDENTIALS_FILE_PATH = "/credentials.json"

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
     fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val `in` =
            SheetsQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    @Throws(IOException::class, GeneralSecurityException::class)
   @JvmStatic */
fun main(args: Array<String>) {
    // Build a new authorized API client service.
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val spreadsheetId = "1R2KE5LZjkIbK36NaFfOunKBmTOAU9w13U41v67wQTeI"
    val range = "Data"
    val service = Sheets.Builder(
        HTTP_TRANSPORT,
        SheetsQuickstart.JSON_FACTORY,
        SheetsQuickstart.getCredentials(HTTP_TRANSPORT)
    )
        .setApplicationName(SheetsQuickstart.APPLICATION_NAME)
        .build()

    // edit start
    // получить первую строку из файла, если она пустая - записать keys если не пустая - сравнить keys
    // с первой строкой и разницу записать в конец строки
    // запись в конкретный столбец - берем ключ из data ищем его позицию в строке которую записали, создаем список
    /* неведома хуйня

fun a() {
    val dataS = mutableMapOf<String, String>()
     val firstColumns = listOf("test1", "val", "foo", "bar", "test2")
    val values = mutableListOf<String>()
    val columns = dataS.forEach {
        val index = firstColumns.indexOfFirst { coolumn -> coolumn == it.key }
        values.add(index, it.value)
    }
}

}*/
    val fakeData = sortedMapOf<String, String>()
    //val keys = fakeData.map { it.key }
    val firstColumns = listOf("test1", "val", "foo", "bar", "test2")
    // переменные выше firstColumns
    // mutableMapOf make like sorted map
    for (i in 0..30) {
        fakeData["test$i"] = "col$i"
    }
    val rows = mutableListOf<List<String?>>()
    val columns = arrayOfNulls<String?>(firstColumns.size)
    fakeData.forEach {
        val index = firstColumns.indexOfFirst { coolumn -> coolumn == it.key }
      //  columns.add(index, it.value)
        if (index>=0)
        columns[index]=it.value
    }
  //  val columns: List<String> = fakeData.map { it.value }
    for (i in 0..10) {
        rows.add(columns.map {
            if (it==null) {
                " "
            } else it
        })
    }
    // val values2 = listOf(fakeData.map { it.value }
    /* listOf(
         "kol1","kol2","kol3"
     )*/


    //  )
    //  val valueRange = ValueRange().setValues(values2)
    val valueRange = ValueRange().setValues(rows.toList())
    service.spreadsheets().values().append(spreadsheetId, range, valueRange).setValueInputOption("RAW")
        .execute()
    // end
    // read start
    val response: ValueRange = service.spreadsheets().values()[spreadsheetId, range]
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
    // read end
}
//}