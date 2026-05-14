package com.example.shoppingapp.data.repository

import com.example.shoppingapp.data.local.dao.UserDao
import com.example.shoppingapp.data.local.entity.UserEntity
import com.example.shoppingapp.domain.model.User
import com.example.shoppingapp.domain.model.toUser
import com.example.shoppingapp.util.PasswordUtils
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.generateUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun login(username: String, password: String): Resource<User> {
        return try {
            val userEntity = userDao.getUserByUsername(username)
            if (userEntity != null && PasswordUtils.verify(password, userEntity.password)) {
                Resource.success(userEntity.toUser())
            } else {
                Resource.error("用户名或密码错误")
            }
        } catch (e: Exception) {
            Resource.error("登录失败: ${e.message}", e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Resource<User> {
        return try {
            // Check if username exists
            if (userDao.getUserByUsername(username) != null) {
                return Resource.error("用户名已被占用")
            }
            // Check if email exists
            if (userDao.getUserByEmail(email) != null) {
                return Resource.error("邮箱已被注册")
            }

            val hashedPassword = PasswordUtils.hash(password)
            val user = UserEntity(
                id = generateUUID(),
                username = username,
                email = email,
                password = hashedPassword
            )
            userDao.insert(user)
            Resource.success(user.toUser())
        } catch (e: Exception) {
            Resource.error("注册失败: ${e.message}", e)
        }
    }

    suspend fun getUserById(userId: String): Resource<User> {
        return try {
            val entity = userDao.getUserById(userId)
            if (entity != null) Resource.success(entity.toUser())
            else Resource.error("用户不存在")
        } catch (e: Exception) {
            Resource.error("获取用户信息失败: ${e.message}", e)
        }
    }

    fun getUserByIdFlow(userId: String): Flow<Resource<User?>> {
        return userDao.getUserByIdFlow(userId).map { entity ->
            Resource.success(entity?.toUser())
        }
    }

    suspend fun updateUser(user: User): Resource<User> {
        return try {
            userDao.update(
                UserEntity(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    password = user.password,
                    phone = user.phone,
                    avatar = user.avatar,
                    address = user.address,
                    role = user.role,
                    createdAt = user.createdAt
                )
            )
            Resource.success(user)
        } catch (e: Exception) {
            Resource.error("更新用户信息失败: ${e.message}", e)
        }
    }
}
