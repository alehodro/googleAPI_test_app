import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import java.util.*
import kotlin.collections.ArrayList


class GoogleApiTestMachine() {

    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val sheetsService = Sheets.Builder(
        HTTP_TRANSPORT,
        AuthProvider.JSON_FACTORY,
        AuthProvider.getCredentials(HTTP_TRANSPORT)
    )
        .setApplicationName(AuthProvider.APPLICATION_NAME)
        .build()

    fun readValues(spreadsheetId: String, range: String) {
        val response: ValueRange = sheetsService.spreadsheets().values()[spreadsheetId, range].execute()
        val values: List<List<Any>> = response.getValues().orEmpty()
        if (values.isEmpty()) {
            println("No data found.")
        } else {
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                println(row.joinToString(" "))
            }
        }
    }

    fun appendValues(spreadsheetId: String, range: String, valueRange: ValueRange) {
        sheetsService
            .spreadsheets()
            .values()
            .append(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
            .execute()
        val response: ValueRange = sheetsService.spreadsheets().values()[spreadsheetId, range]
            .execute()
        val values: List<List<Any>> = response.getValues().orEmpty()
        if (values.isEmpty()) {
            println("No data found.")
        } else {
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                println(row.joinToString(" "))
            }
        }
    }

    fun createTable(columnsList: List<String>) {

        fun createCellDataLists(columnsList: List<String>): ArrayList<List<CellData>> {
            val cellDataLists = ArrayList<List<CellData>>()
            val size = columnsList.size
            for (i in 0 until size) {
                cellDataLists.add(
                    listOf(CellData().setUserEnteredValue(ExtendedValue().setStringValue(columnsList.get(i))))
                )
            }
            return cellDataLists
        }

        val cellDataLists = createCellDataLists(columnsList)

        fun createRawDataLists(cellDataLists:ArrayList<List<CellData>>):ArrayList<List<RowData>>{
            val RawDataLists = ArrayList<List<RowData>>()
            for (i in cellDataLists){
                RawDataLists.add(listOf(RowData().setValues(i)))
            }
            return RawDataLists
        }

        val rawDataLists = createRawDataLists(cellDataLists)
/*
        val rowData1 = listOf(
            (RowData()
                .setValues(
                    listOf(
                        CellData()
                            .setUserEnteredValue(
                                ExtendedValue()
                                    .setStringValue("1")
                            )
                    )
                )
                    )
        )

        val rowData2 = listOf(
            (RowData()
                .setValues(
                    listOf(
                        CellData()
                            .setUserEnteredValue(
                                ExtendedValue()
                                    .setStringValue("2")
                            )
                    )
                )
                    )
        )

*/
        /*
         val gridData = GridData()
             .setStartColumn(0)
             .setStartRow(0)
             .setRowData(
                 listOf(
                     RowData()
                         .setValues(
                             listOf(
                                 CellData()
                                     .setUserEnteredValue(
                                         ExtendedValue()
                                             .setStringValue("1")
                                     )
                             )
                         )
                 )
             )
         */

        fun createDataGridLists(rawDataLists:ArrayList<List<RowData>>):List<GridData>{
            val gridDataList = mutableListOf<GridData>()
            val size = rawDataLists.size
            for (i in 0 until size) {
                gridDataList.add(i,(GridData()
                    .setStartColumn(i)
                    .setStartRow(0)
                    .setRowData(rawDataLists.get(i))))
            }
            return gridDataList.toList()
        }

        val gridDataList=createDataGridLists(rawDataLists)
/*
        val gridDataList = listOf(
            (GridData()
                .setStartColumn(0)
                .setStartRow(0)
                .setRowData(rowData1)),

            (GridData()
                .setStartColumn(1)
                .setStartRow(0)
                .setRowData(rowData2))
        )
*/

        val newSheet = Sheet()
            .setData(gridDataList)
            .setProperties(
                SheetProperties()
                    .setTitle("Data")
                    .setSheetId(0)
                    .setSheetType("GRID")
            )


        val newSheets = listOf<Sheet>(newSheet)
        val spreadSheetProps = SpreadsheetProperties()
            .setTitle("testMelon")
            .setLocale("ru_RU")
        val newSpreadsheet = Spreadsheet()
            .setSheets(newSheets)
            .setProperties(spreadSheetProps)
        val response = sheetsService
            .spreadsheets()
            .create(newSpreadsheet)
            .execute()
        val spreadsheetId = response.spreadsheetId
        println(spreadsheetId)
        //   val response = sheetsService.Spreadsheets().get(spreadsheetId)
        //  println(response.spreadsheetId.toString())

    }
}
