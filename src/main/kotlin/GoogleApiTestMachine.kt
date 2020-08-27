import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import com.google.common.primitives.Chars


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

    fun appendData(spreadsheet: SpreadSheet, orderData: Map<String, String>) {
        if (spreadsheet.spreadSheetId.isNullOrEmpty()&&!spreadsheet.spreadSheetTitle.isNullOrEmpty()){
            val spreadsheetId = createTable(orderData.keys.toList(),spreadsheet.spreadSheetTitle)
            val range = "Data"
            val rowsValues = listOf(listOf(orderData.values))
            val valueRange = ValueRange().setValues(rowsValues)

            appendValues(spreadsheetId,range,valueRange)
        } else if (!spreadsheet.spreadSheetId.isNullOrEmpty()&& readValues(spreadsheet.spreadSheetId)[0].isEmpty()){
            val columnsRowToAdd = listOf(listOf(orderData.keys))
            val range = "Data"
            val valueRange = ValueRange().setValues(columnsRowToAdd)
            appendValues(spreadsheet.spreadSheetId,range,valueRange)
        } else {
            val readResponseRowValues = readValues(spreadsheet.spreadSheetId!!)[0].map {
                it.toString()
            }
            val columnsInOrderData = orderData.keys.toList()
            val columnsToAdd = columnsInOrderData.filterNot { readResponseRowValues.contains(it) }
            if (columnsToAdd.firstOrNull() != null) {
                val startIndex = Chars.checkedCast(readResponseRowValues.size.toLong() + 97)
                val endIndex = if (columnsToAdd.size == 1) {
                    startIndex
                } else {
                    Chars.checkedCast(readResponseRowValues.size.toLong() + columnsToAdd.size.toLong() + 97)
                }
                val rangeColumns = "$startIndex:$endIndex"
                val content = ValueRange()
                    .setValues(listOf(columnsToAdd))
                sheetsService
                    .spreadsheets()
                    .Values()
                    .append(spreadsheet.spreadSheetId, rangeColumns, content)
                    .setValueInputOption("RAW")
                    .execute()

                val readResponseRowAddedValues = readValues(spreadsheet.spreadSheetId)[0].map { it.toString() }
                val rowsToAdd = createRowsToAdd(orderData, readResponseRowAddedValues)
                val valueRange = ValueRange().setValues(rowsToAdd)
                val range = "Data"
                appendValues(spreadsheet.spreadSheetId, range, valueRange)
            } else {
                val rowsToAdd = createRowsToAdd(orderData, readResponseRowValues)
                val valueRange = ValueRange().setValues(rowsToAdd)
                val range = "Data"
                appendValues(spreadsheet.spreadSheetId, range, valueRange)
            }
        }
    }

    private fun appendValues(spreadsheetId: String, range:String , valueRange:ValueRange) {
        sheetsService
            .spreadsheets()
            .values()
            .append(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
            .execute()
        println("Data successfully appended\n")
    }

    private fun createTable(columnsList: List<String>, spreadsheetTitle: String): String {

        fun createCellDataLists(columnsList: List<String>): List<List<CellData>> {
            return columnsList.map {
                listOf(
                    CellData().setUserEnteredValue(ExtendedValue().setStringValue(it))
                        .setUserEnteredFormat(
                            CellFormat()
                                .setWrapStrategy("WRAP")
                                .setHorizontalAlignment("LEFT")
                                .setTextFormat(TextFormat().setBold(true))
                        )
                )
            }
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
        val gridDataList = createDataGridList(rawDataLists)
        val newSheet = Sheet()
            .setData(gridDataList)
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
