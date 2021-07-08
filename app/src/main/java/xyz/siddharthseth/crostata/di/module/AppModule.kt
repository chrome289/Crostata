package xyz.siddharthseth.crostata.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.siddharthseth.crostata.di.scope.AppScope
import xyz.siddharthseth.crostata.util.TimeConverter
import java.util.*


@Module
class AppModule(private val application: Application) {
    @AppScope
    @Provides
    fun providesApplication(): Application {
        return application
    }

    @AppScope
    @Provides
    fun providesContext(): Context {
        return application.applicationContext
    }

    @AppScope
    @Provides
    fun providesCalendar(): Calendar {
        return Calendar.getInstance()
    }


    @AppScope
    @Provides
    fun providesTimeConverter(application: Application, calendar: Calendar): TimeConverter {
        return TimeConverter(application, calendar)
    }

}