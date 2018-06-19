package xyz.siddharthseth.crostata.data.providers

import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.data.service.CrostataApiService

/**
 * repository provider for content api
 */

object ContentRepositoryProvider {
    fun getContentRepository(): ContentRepository {
        return ContentRepository(CrostataApiService.create())
    }
}