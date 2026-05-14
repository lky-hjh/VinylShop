package com.example.shoppingapp.util

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * 密码工具类
 * 使用 SHA-256 + Salt 对密码进行哈希加密
 * 遵循安全最佳实践，替代明文存储密码
 */
object PasswordUtils {

    private const val SALT_LENGTH = 16
    private const val HASH_ALGORITHM = "SHA-256"

    /**
     * 对密码进行哈希加密（自动生成随机 Salt）
     * 返回格式: salt:hash（Base64 编码）
     */
    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        val hash = hashWithSalt(password, salt)
        val saltBase64 = android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP)
        val hashBase64 = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        return "$saltBase64:$hashBase64"
    }

    /**
     * 验证密码是否与存储的哈希匹配
     */
    fun verify(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false
        val salt = android.util.Base64.decode(parts[0], android.util.Base64.NO_WRAP)
        val expectedHash = android.util.Base64.decode(parts[1], android.util.Base64.NO_WRAP)
        val actualHash = hashWithSalt(password, salt)
        return expectedHash.contentEquals(actualHash)
    }

    /**
     * 使用指定 Salt 计算 SHA-256 哈希
     */
    private fun hashWithSalt(password: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        digest.update(salt)
        return digest.digest(password.toByteArray(Charsets.UTF_8))
    }
}
