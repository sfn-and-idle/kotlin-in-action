package ch10

fun main() {

    println("\n\n===== 10.1.1 =====")

}

interface JsonObject {
    fun setSimpleProperty(propertyName: String, value: Any?)
    fun createObject(propertyName: String): JsonObject
    fun createArray(propertyName: String): JsonObject
}

interface Seed : JsonObject {
    fun spawn(): Any?
    fun createCompositePoroperty(
        propertyName: String,
        isList: Boolean
    ): JsonObject

    override fun createObject(propertyName: String) =
        createCompositePoroperty(propertyName, false)

    override fun createArray(propertyName: String) =
        createCompositePoroperty(propertyName, true)

    // ...
}

// ===== 10.1.1 =====
