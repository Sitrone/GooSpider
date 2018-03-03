package io.github.sununiq.util

import org.apache.commons.codec.binary.Hex
import org.apache.http.Header
import org.apache.http.message.BasicHeader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList


fun <T> T?.println() = println(this)

fun ByteArray.toHexString() = Hex.encodeHexString(this)

fun String.fromHex2ByteArray() = Hex.decodeHex(this.toCharArray())

fun getFilePath(relativePath: String): Path {
    return Paths.get(Thread.currentThread().contextClassLoader.getResource(relativePath).toURI())
}

fun Map<String, String>.toHeaders(): Array<Header> {
    val list = ArrayList<Header>(this.size)
    this.forEach { t, u ->
        list.add(BasicHeader(t, u))
    }
    return list.toArray() as Array<Header>
}