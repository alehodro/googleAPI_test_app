import java.util.*

class DataBuilder() {
    val spreadSheetTitle = "GoogleApi test spreadsheet"
    val initialColumnList = listOf("product", "weight", "price", "volume", "quantity")

    private val fakeDataReceived1 = mapOf(
        "price" to "200",
        "product" to "potato",
        "weight" to "100"
    )
    private val fakeDataReceived2 = mapOf(
        "price" to "400",
        "product" to "apple juice",
        "volume" to "5"
    )
    private val fakeDataReceived3 = mapOf(
        "price" to "30",
        "product" to "garlic",
        "quantity" to "10"
    )

    private val fakeDataReceived4 =
        mapOf(
            "price" to "50",
            "product" to "apple",
            "weight" to "7",
            "color" to "green"
        )

    private val fakeDataReceived5 =
        mapOf(
            "Column1" to "DataColumn1",
            "Column2" to "DataColumn2",
            "Column3" to "DataColumn3",
            "Column4" to "DataColumn4"
        )

    private fun addOrderIdToDataReceived(dataReceived: Map<String, String>): Map<String, String> {
        return dataReceived + ("orderId" to UUID.randomUUID().toString())
    }

    val fakeDataWithOrderId1 = addOrderIdToDataReceived(fakeDataReceived1)
    val fakeDataWithOrderId2 = addOrderIdToDataReceived(fakeDataReceived2)
    val fakeDataWithOrderId3 = addOrderIdToDataReceived(fakeDataReceived3)
    val fakeDataWithOrderId4 = addOrderIdToDataReceived(fakeDataReceived4)
    val fakeDataWithOrderId5 = addOrderIdToDataReceived(fakeDataReceived5)
}