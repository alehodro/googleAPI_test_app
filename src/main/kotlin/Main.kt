fun main(args: Array<String>) {
    val testDataBuilder = DataBuilder()
    val apiTestMachine = GoogleApiTestMachine()

    println("1. Press enter to create initial spreadsheet with columns and no data. This emulates creating of export")

    readLine()

    val spreadsheetId = apiTestMachine.createTable(testDataBuilder.initialColumnList)

    println(
        "2. Press enter to append data to your spreadsheet." + "\n" +
                "App reads columns row of a spreadsheet," +
                " matches them to data export maps keys and appends data to spreadsheet"
    )

    readLine()

    apiTestMachine.appendData(spreadsheetId, testDataBuilder.fakeDataWithOrderId1)
    apiTestMachine.appendData(spreadsheetId, testDataBuilder.fakeDataWithOrderId2)
    apiTestMachine.appendData(spreadsheetId, testDataBuilder.fakeDataWithOrderId3)

    println("3. Press enter to append data with new column")

    readLine()

    apiTestMachine.appendData(spreadsheetId, testDataBuilder.fakeDataWithOrderId4)

    println("4. Press enter to append data with all new columns")

    readLine()

    apiTestMachine.appendData(spreadsheetId, testDataBuilder.fakeDataWithOrderId5)
}
