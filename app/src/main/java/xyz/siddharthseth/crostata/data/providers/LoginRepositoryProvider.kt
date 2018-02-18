package xyz.siddharthseth.crostata.data.providers

import xyz.siddharthseth.crostata.data.repository.LoginRepository
import xyz.siddharthseth.crostata.data.service.CrostataApiService

object LoginRepositoryProvider {
    fun getLoginRepository(): LoginRepository {
        return LoginRepository(CrostataApiService.Create())
    }
}

