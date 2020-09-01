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
    ): List<List<String>> {
        return listOf(columnsExistList.map { orderData.getOrElse(it) { "" } })
    }

    private fun readValues(spreadsheetId: String): List<List<Any>> {
        val response: ValueRange = sheetsService.spreadsheets().values()[spreadsheetId, "Data!1:1"].execute()
        val values = response.getValues().orEmpty()
        if (values.isEmpty()) println("No data found.")
        return values
    }

    private fun createTitle(): String {
        return "Google Api Test Spreadsheet"
    }

    fun appendData(spreadsheetId: String?, orderData: Map<String, String>) {
        val valuesRead = if (!spreadsheetId.isNullOrEmpty()) readValues(spreadsheetId) else null
        val columnsInOrderData = orderData.keys.toList()
        if (spreadsheetId.isNullOrEmpty() || valuesRead.isNullOrEmpty()) {
            val spreadsheetIdToAppend = if (spreadsheetId.isNullOrEmpty()) createTable(createTitle()) else spreadsheetId
            val rowsValuesColumns = listOf(columnsInOrderData)
            val rowsValuesData = listOf(orderData.values.toList())
            appendValues(spreadsheetIdToAppend, rowsValuesColumns)
            appendValues(spreadsheetIdToAppend, rowsValuesData)
        } else {
            val readResponseRowValues = valuesRead[0].map {
                it.toString()
            }
            val columnsToAdd = columnsInOrderData.filterNot { readResponseRowValues.contains(it) }
            val responseRowValues = if (columnsToAdd.isNotEmpty()) addColumns(
                columnsToAdd,
                readResponseRowValues,
                spreadsheetId
            ) else readResponseRowValues
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
            return currentColumns+columnsToAdd
    }

    fun appendValues(spreadsheetId: String, rowsValues: List<List<String>>) {
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
        // private fun createTable(columnsList: List<String>, spreadsheetTitle: String): String {

        /*  fun createCellDataLists(columnsList: List<String>): List<List<CellData>> {
              return columnsList.map { listOf(CellData().setUserEnteredValue(ExtendedValue().setStringValue(it))) }
          }

          fun createRawDataLists(cellDataLists: List<List<CellData>>): List<List<RowData>> {
              return cellDataLists.map { listOf(RowData().setValues(it)) }
          }

          fun createDataGridList(rawDataLists: List<List<RowData>>): List<GridData> {
              return rawDataLists.mapIndexed { i, rowData ->
                  GridData()
                      .setStartColumn(i)
                      .setStartRow(0)
                      .setRowData(rowData)
              }
          }

          val cellDataLists = createCellDataLists(columnsList)
          val rawDataLists = createRawDataLists(cellDataLists)
          val gridDataList = createDataGridList(rawDataLists)*/
        val newSheet = Sheet()
            //  .setData(gridDataList)
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
