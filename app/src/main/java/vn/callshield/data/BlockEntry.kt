package vn.callshield.data
data class BlockEntry(
    val id: String,
    val brand: String,
    val phone: String,
    val note: String = "",
    val enabled: Boolean = true
)
