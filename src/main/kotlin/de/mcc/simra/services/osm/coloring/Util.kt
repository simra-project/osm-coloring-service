package de.mcc.simra.services.osm.coloring

import java.security.MessageDigest

private val charset = Charsets.UTF_8
private val HEX_ARRAY: ByteArray = "0123456789abcdef".toByteArray(Charsets.US_ASCII)


fun String.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    val bytes = this.toByteArray(charset)
    md.update(bytes)
    val digest = md.digest()
    return digest.toHexString()
}

/**
 * Function based on https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
 */
fun ByteArray.toHexString(): String {
    val hexChars = ByteArray(this.size * 2)
    for (j in 0 until this.size) {
        val v: Int = (this[j]).toInt() and 0xFF
        hexChars[j * 2] = HEX_ARRAY[v ushr 4]
        hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }
    return String(hexChars, charset)
}