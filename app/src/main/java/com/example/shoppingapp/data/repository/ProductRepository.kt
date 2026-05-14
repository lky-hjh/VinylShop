package com.example.shoppingapp.data.repository

import com.example.shoppingapp.data.local.dao.ProductDao
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.model.toProduct
import com.example.shoppingapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<Resource<List<Product>>> {
        return productDao.getAllProducts().map { entities ->
            Resource.success(entities.map { it.toProduct() })
        }
    }

    suspend fun getProductById(productId: String): Resource<Product> {
        return try {
            val entity = productDao.getProductById(productId)
            if (entity != null) Resource.success(entity.toProduct())
            else Resource.error("商品不存在")
        } catch (e: Exception) {
            Resource.error("获取商品失败: ${e.message}", e)
        }
    }

    fun getProductByIdFlow(productId: String): Flow<Resource<Product?>> {
        return productDao.getProductByIdFlow(productId).map { entity ->
            Resource.success(entity?.toProduct())
        }
    }

    fun getFeaturedProducts(): Flow<Resource<List<Product>>> {
        return productDao.getFeaturedProducts().map { entities ->
            Resource.success(entities.map { it.toProduct() })
        }
    }

    fun getNewProducts(): Flow<Resource<List<Product>>> {
        return productDao.getNewProducts().map { entities ->
            Resource.success(entities.map { it.toProduct() })
        }
    }

    fun getProductsByGenre(genre: String): Flow<Resource<List<Product>>> {
        return productDao.getProductsByGenre(genre).map { entities ->
            Resource.success(entities.map { it.toProduct() })
        }
    }

    fun searchProducts(query: String): Flow<Resource<List<Product>>> {
        return productDao.searchProducts(query).map { entities ->
            Resource.success(entities.map { it.toProduct() })
        }
    }

    fun searchProductsWithFilter(
        query: String,
        genre: String?,
        minPrice: Double,
        maxPrice: Double
    ): Flow<Resource<List<Product>>> {
        return productDao.searchProductsWithFilter(query, genre, minPrice, maxPrice)
            .map { entities ->
                Resource.success(entities.map { it.toProduct() })
            }
    }

    fun getAllGenres(): Flow<Resource<List<String>>> {
        return productDao.getAllGenres().map { genres ->
            Resource.success(genres)
        }
    }

    suspend fun decrementStock(productId: String, quantity: Int): Resource<Int> {
        return try {
            val rows = productDao.decrementStock(productId, quantity)
            if (rows > 0) Resource.success(rows)
            else Resource.error("库存不足")
        } catch (e: Exception) {
            Resource.error("更新库存失败: ${e.message}", e)
        }
    }

    suspend fun insertSeedData(products: List<ProductEntity>) {
        productDao.insertAll(products)
    }

    suspend fun deleteProduct(productId: String): Resource<Unit> {
        return try {
            productDao.deleteById(productId)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("删除商品失败: ${e.message}", e)
        }
    }

    suspend fun addProduct(product: Product): Resource<Product> {
        return try {
            val entity = ProductEntity(
                id = product.id,
                name = product.name,
                artist = product.artist,
                genre = product.genre,
                price = product.price,
                description = product.description,
                imageUrl = product.imageUrl,
                rating = product.rating,
                stock = product.stock,
                releaseDate = product.releaseDate,
                isFeatured = product.isFeatured,
                isNew = product.isNew
            )
            productDao.insert(entity)
            Resource.success(product)
        } catch (e: Exception) {
            Resource.error("添加商品失败: ${e.message}", e)
        }
    }

    suspend fun updateProduct(product: Product): Resource<Product> {
        return try {
            val entity = ProductEntity(
                id = product.id,
                name = product.name,
                artist = product.artist,
                genre = product.genre,
                price = product.price,
                description = product.description,
                imageUrl = product.imageUrl,
                rating = product.rating,
                stock = product.stock,
                releaseDate = product.releaseDate,
                isFeatured = product.isFeatured,
                isNew = product.isNew
            )
            productDao.update(entity)
            Resource.success(product)
        } catch (e: Exception) {
            Resource.error("更新商品失败: ${e.message}", e)
        }
    }
}
