import com.google.api.services.sheets.v4.model.ValueRange
import java.util.*

class DataBuilder() {

    val initialColumnList = listOf("product", "weight", "price", "volume", "quantity")

    val fakeDataReceived1 = mapOf(
        "price" to "200",
        "product" to "potato",
        "weight" to "100"
    )
    val fakeDataReceived2 = mapOf(
        "price" to "400",
        "product" to "apple juice",
        "volume" to "5"
    )
    val fakeDataReceived3 = mapOf(
        "price" to "30",
        "product" to "garlic",
        "quantity" to "10"
    )

    val fakeDataReceived4 =
        mapOf(
            "price" to "50",
            "product" to "apple",
            "weight" to "7",
            "color" to "green"
        )

    val fakeDataReceived5 =
        mapOf(
            "Саня" to "Четкий",
            "Ваня" to "Стример",
            "Володя" to "Клевый",
            "Дядя" to "Вася"
        )

    fun addOrderIdToDataReceived(dataReceived: Map<String, String>): Map<String, String> {
        val orderId = UUID.randomUUID().toString()
        val mutableMap = dataReceived.toMutableMap()
        mutableMap.put("orderId", orderId)
        return mutableMap
    }

    val fakeDataWithOrderId1 = addOrderIdToDataReceived(fakeDataReceived1)
    val fakeDataWithOrderId2 = addOrderIdToDataReceived(fakeDataReceived2)
    val fakeDataWithOrderId3 = addOrderIdToDataReceived(fakeDataReceived3)
    val fakeDataWithOrderId4 = addOrderIdToDataReceived(fakeDataReceived4)
    val fakeDataWithOrderId5 = addOrderIdToDataReceived(fakeDataReceived5)
}