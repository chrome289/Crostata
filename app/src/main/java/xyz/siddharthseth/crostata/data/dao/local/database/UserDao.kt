package xyz.siddharthseth.crostata.data.dao.local.database

import androidx.room.*
import io.reactivex.Single
import xyz.siddharthseth.crostata.domain.model.User

@Dao
interface UserDao {
    @Insert
    fun add(user: User): Long

    @Query("SELECT * FROM users LIMIT 1")
    fun get(): Single<User>

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}
