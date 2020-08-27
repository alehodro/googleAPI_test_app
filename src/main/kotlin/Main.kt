fun main(args: Array<String>) {
    val testDataBuilder = DataBuilder()
    val apiTestMachine = GoogleApiTestMachine()

    println("1. Press enter to create initial spreadsheet and append data")

    readLine()

  apiTestMachine.appendData(testDataBuilder.spreadSheetIsNotExist,testDataBuilder.fakeDataReceived1)

    println(
        "2. Create a spreadsheet and name list \"Data\" copy and paste it's id to command line and" + "\n" +"press " +
                "enter or copy id of the spreadsheet created at first step"
    )

    val spreadsheetId = SpreadSheet(readLine(),null)

    apiTestMachine.appendData(spreadsheetId,testDataBuilder.fakeDataReceived2)

    println("3. Now press enter to append data with new columns to your second table you created")

    readLine()

    apiTestMachine.appendData(SpreadSheet(spreadsheetId.spreadSheetId,null),testDataBuilder.fakeDataReceived3)
    apiTestMachine.appendData(SpreadSheet(spreadsheetId.spreadSheetId,null),testDataBuilder.fakeDataReceived4)
    apiTestMachine.appendData(SpreadSheet(spreadsheetId.spreadSheetId,null),testDataBuilder.fakeDataReceived5)
}
