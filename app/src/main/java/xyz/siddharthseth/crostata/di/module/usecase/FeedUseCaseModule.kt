package xyz.siddharthseth.crostata.di.module.usecase

import dagger.Module
import dagger.Provides
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.di.scope.AppScope
import xyz.siddharthseth.crostata.domain.usecase.feed.GetFeedUseCase
import xyz.siddharthseth.crostata.domain.usecase.post.GetPostsUseCase
import xyz.siddharthseth.crostata.domain.usecase.profile.GetProfileUseCase

@Module
class FeedUseCaseModule {
    @Provides
    @AppScope
    fun providesGetFeedUseCase(apiManager: ApiManager): GetFeedUseCase {
        return GetFeedUseCase(apiManager)
    }

    @Provides
    @AppScope
    fun providesGetProfileUseCase(apiManager: ApiManager): GetProfileUseCase {
        return GetProfileUseCase(apiManager)
    }

    @Provides
    @AppScope
    fun providesGetPostUseCase(apiManager: ApiManager): GetPostsUseCase {
        return GetPostsUseCase(apiManager)
    }
}