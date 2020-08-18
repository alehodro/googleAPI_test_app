import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange


fun main(args: Array<String>) {
    // Build a new authorized API client service.

    val spreadsheetId = "1Qw8fZ142SIKHWi6kIPWDo68h8b8pr2lCc1dOUZgo0dU"
    val range = "Data"

//    val apiTestMachine = GoogleApiTestMachine()

//    println("1 to create, 2 to append")
//    val inputCommand = readLine()!!.toInt()
//    if (inputCommand == 1) apiTestMachine.readValues(spreadsheetId, range)

    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val service = Sheets.Builder(
        HTTP_TRANSPORT,
        AuthProvider.JSON_FACTORY,
        AuthProvider.getCredentials(HTTP_TRANSPORT)
    )
        .setApplicationName(AuthProvider.APPLICATION_NAME)
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
//    price -> 200
//    product -> potato
//    weight -> 100
//
//    1 price quality product weight color
//            2
//    3
//    4

    //[price => 200, product => potato, weight => 100]
    val fakeData = mapOf(
        "price" to "200",
        "product" to "potato",
        "weight" to "100"
    )

    //List(10) { i -> "col$i" to "value$i" }.toMap()  //[(test1, col1), (test2, col2) ...]
    val columnNamesExists = listOf("price", "quality", "product", "weight", "color", "hello")
    // переменные выше firstColumns

    val newRow = columnNamesExists.map { fakeData.getOrElse(it) { "" } }

    //["price", "quality", "product", "weight", "color"] => ["200", " ", "potato", "100", " "]
    val valueRange = ValueRange().setValues(listOf(newRow))
    service
        .spreadsheets()
        .values()
        .append(spreadsheetId, range, valueRange)
        .setValueInputOption("RAW")
        .execute()


    // read start
    val response: ValueRange = service.spreadsheets().values()[spreadsheetId, range]
        .execute()
    val values = response.getValues()
    if (values.isEmpty()) {
        println("No data found.")
    } else {
        for (row in values) {
            // Print columns A and E, which correspond to indices 0 and 4.
            println(row.joinToString(" "))
        }
    }
    // read end
}
