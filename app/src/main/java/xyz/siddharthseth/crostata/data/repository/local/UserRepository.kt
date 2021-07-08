package xyz.siddharthseth.crostata.data.repository.local

import io.reactivex.Completable
import io.reactivex.Single
import xyz.siddharthseth.crostata.data.dao.local.database.UserDao
import xyz.siddharthseth.crostata.domain.model.User
import xyz.siddharthseth.crostata.util.transformer.AsyncCompletableTransformer
import xyz.siddharthseth.crostata.util.transformer.AsyncSingleTransformer

class UserRepository(private val userDao: UserDao) {

    var cachedUser: User? = null

    fun add(item: User): Completable {
        return Completable.fromAction { userDao.add(item) }
            .compose(AsyncCompletableTransformer.applyIoScheduler())
    }

    fun remove() {
        cachedUser = null
    }

    fun update(item: User): Completable {
        return Completable.fromAction { userDao.update(item) }
            .compose(AsyncCompletableTransformer.applyIoScheduler())
    }

    fun get(): Single<User> {
        return userDao.get()
            .compose(AsyncSingleTransformer.applyIoScheduler())
            .map {
                cachedUser = it
                return@map it
            }
    }
}