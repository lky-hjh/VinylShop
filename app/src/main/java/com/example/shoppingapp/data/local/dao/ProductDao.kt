package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shoppingapp.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY releaseDate DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdFlow(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE isFeatured = 1")
    fun getFeaturedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isNew = 1 ORDER BY releaseDate DESC")
    fun getNewProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE genre = :genre")
    fun getProductsByGenre(genre: String): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR artist LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN name LIKE :query || '%' THEN 0 
                 WHEN artist LIKE :query || '%' THEN 1 
                 ELSE 2 
            END
    """
    )
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM products 
        WHERE (name LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        AND (:genre IS NULL OR genre = :genre)
        AND price BETWEEN :minPrice AND :maxPrice
        ORDER BY 
            CASE WHEN name LIKE :query || '%' THEN 0 
                 WHEN artist LIKE :query || '%' THEN 1 
                 ELSE 2 
            END
    """
    )
    fun searchProductsWithFilter(
        query: String,
        genre: String?,
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<ProductEntity>>

    @Query("SELECT DISTINCT genre FROM products ORDER BY genre")
    fun getAllGenres(): Flow<List<String>>

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId AND stock >= :quantity")
    suspend fun decrementStock(productId: String, quantity: Int): Int

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteById(productId: String)
}
