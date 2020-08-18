import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import java.util.*


fun main(args: Array<String>) {
    // Build a new authorized API client service.

    val apiTestMachine = GoogleApiTestMachine()

    println("1 to create, 2 to append")
    var inputCommand: Int? = null
    val scanner = Scanner(System.`in`)
    inputCommand = scanner.nextLine().trim().toInt()
    if (inputCommand==1) apiTestMachine.

    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val spreadsheetId = "1R2KE5LZjkIbK36NaFfOunKBmTOAU9w13U41v67wQTeI"
    val range = "Data"
    val service = Sheets.Builder(
        HTTP_TRANSPORT,
        AuthProvider.JSON_FACTORY,
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
        if (index >= 0)
            columns[index] = it.value
    }
    //  val columns: List<String> = fakeData.map { it.value }
    for (i in 0..10) {
        rows.add(columns.map {
            if (it == null) {
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
// создаю новую таблицу

    val title = "testCreate"
    var spreadsheet = Spreadsheet()
        .setProperties(
            SpreadsheetProperties()
                .setTitle(title)
        )

    spreadsheet = service.spreadsheets().create(spreadsheet)
        .setFields("8R2KE5LZjkIbK36NaTfOunKBmTOAU9w13U41v67wQTeI")
        .execute()
    // service.spreadsheets().create()


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
