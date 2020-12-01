package p7gruppe.p7.offloading.fileutils;

import java.security.MessageDigest

class Hasher {
    fun hash(msg: String): String {
        val bytes = msg.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}