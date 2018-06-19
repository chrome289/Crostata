package xyz.siddharthseth.crostata.data.providers

import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.CrostataApiService

/**
 *repository provider for login api
 */
object LoginRepositoryProvider {
    fun getLoginRepository(): LoginRepository {
        return LoginRepository(CrostataApiService.create())
    }
}

