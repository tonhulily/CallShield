package vn.callshield.data
object PhoneNormalizer {
    fun normalize(raw: String): String {
        val trimmed = raw.trim()
        val digits = trimmed.filter { it.isDigit() }
        return when {
            trimmed.startsWith("+84") -> "0" + digits.removePrefix("84")
            trimmed.startsWith("0084") -> "0" + digits.removePrefix("0084")
            else -> digits
        }
    }
}
