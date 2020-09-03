import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*


class GoogleApiTestMachine() {
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val sheetsService = Sheets.Builder(
        HTTP_TRANSPORT,
        AuthProvider.JSON_FACTORY,
        AuthProvider.getCredentials(HTTP_TRANSPORT)
    )
        .setApplicationName(AuthProvider.APPLICATION_NAME)
        .build()

    private fun createRowsToAdd(
        orderData: Map<String, String>,
        columnsExistList: List<String>
    ) = listOf(columnsExistList.map { orderData.getOrElse(it) { "" } })

    private fun readValues(spreadsheetId: String): List<List<Any>> {
        val response = sheetsService
            .spreadsheets()
            .values()
            .get(spreadsheetId, "Data!1:1")
            .execute()
        val values = response.getValues().orEmpty()
        if (values.isEmpty()) println("No data found.")
        return values
    }

    private fun createTitle(): String {
        return "Google Api Test Spreadsheet"
    }

    private fun fillEmptyColumns(spreadsheetId: String, columnsExistList: List<String>) {
        val rowListFilled = columnsExistList.map {
            if (it.isBlank()) {
                "empty"
            } else {
                it
            }
        }
        val valueRange = ValueRange().setValues(listOf(rowListFilled))
        sheetsService
            .spreadsheets()
            .values()
            .update(spreadsheetId, "Data!1:1", valueRange)
            .setValueInputOption("RAW")
            .execute()
    }


    fun appendData(spreadsheetId: String?, orderData: Map<String, String>) {
        val valuesRead = if (spreadsheetId != null) {
            readValues(spreadsheetId).first().map { it.toString() }
        } else {
            emptyList()
        }
        spreadsheetId?.let { if (valuesRead.contains("")) fillEmptyColumns(it, valuesRead) }
        val columnsInOrderData = orderData.keys.toList()
        if (spreadsheetId == null || valuesRead.isEmpty()) {
            val spreadsheetIdToAppend = spreadsheetId ?: createTable(createTitle())
            val rowsValuesColumns = listOf(columnsInOrderData)
            val rowsValuesData = listOf(orderData.values.toList())
            appendValues(spreadsheetIdToAppend, rowsValuesColumns)
            appendValues(spreadsheetIdToAppend, rowsValuesData)
        } else {
            val columnsToAdd = columnsInOrderData.filterNot { valuesRead.contains(it) }
            val responseRowValues = if (columnsToAdd.isNotEmpty()) {
                addColumns(columnsToAdd, valuesRead, spreadsheetId)
            } else {
                valuesRead
            }
            val rowsToAdd = createRowsToAdd(orderData, responseRowValues)
            appendValues(spreadsheetId, rowsToAdd)
        }
    }

    private fun addColumns(
        columnsToAdd: List<String>,
        currentColumns: List<String>,
        spreadSheetId: String
    ): List<String> {
        val startIndex = currentColumns.size + 97
        val endIndex = columnsToAdd.size + startIndex - 1
        val rangeColumns = "${startIndex.toChar()}:${endIndex.toChar()}"
        val content = ValueRange()
            .setValues(listOf(columnsToAdd))
        sheetsService
            .spreadsheets()
            .Values()
            .append(spreadSheetId, rangeColumns, content)
            .setValueInputOption("RAW")
            .execute()
        return currentColumns + columnsToAdd
    }

    private fun appendValues(spreadsheetId: String, rowsValues: List<List<String>>) {
        val valueRange = ValueRange().setValues(rowsValues)
        sheetsService
            .spreadsheets()
            .values()
            .append(spreadsheetId, "Data", valueRange)
            .setValueInputOption("RAW")
            .execute()
        println("Data successfully appended\n")
    }

    private fun createTable(spreadsheetTitle: String): String {
        val newSheet = Sheet()
            .setProperties(
                SheetProperties()
                    .setTitle("Data")
                    .setSheetId(0)
                    .setSheetType("GRID")
            )
        val newSheets = listOf(newSheet)
        val spreadSheetProps = SpreadsheetProperties()
            .setTitle(spreadsheetTitle)
            .setLocale("ru_RU")
        val newSpreadsheet = Spreadsheet()
            .setSheets(newSheets)
            .setProperties(spreadSheetProps)
        val response = sheetsService
            .spreadsheets()
            .create(newSpreadsheet)
            .execute()
        val spreadsheetId = response.spreadsheetId
        println("Spreadsheet is created with Id $spreadsheetId\n")
        return spreadsheetId
    }
}
