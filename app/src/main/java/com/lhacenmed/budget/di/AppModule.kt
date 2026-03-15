package com.lhacenmed.budget.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.lhacenmed.budget.BuildConfig
import com.lhacenmed.budget.data.local.AppDatabase
import com.lhacenmed.budget.data.local.CachedContributionDao
import com.lhacenmed.budget.data.local.CachedSpendingDao
import com.lhacenmed.budget.data.local.GroceryDao
import com.lhacenmed.budget.data.local.PendingItemDao
import com.lhacenmed.budget.data.repository.SpendingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://xoydbvnuftlpradakvky.supabase.co",
        supabaseKey = "sb_publishable__oIzuu76wpHYpnw63o_XAg_KS_BqXBi"
    ) {
        install(Auth) {
            autoLoadFromStorage = true
            alwaysAutoRefresh   = false
        }
        install(Postgrest)
    }

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "budget_db")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()

    @Provides @Singleton
    fun provideSpendingRepository(client: SupabaseClient, db: AppDatabase) =
        SpendingRepository(client, db)

    @Provides fun providePendingItemDao(db: AppDatabase): PendingItemDao               = db.pendingItemDao()
    @Provides fun provideGroceryDao(db: AppDatabase): GroceryDao                       = db.groceryDao()
    @Provides fun provideCachedSpendingDao(db: AppDatabase): CachedSpendingDao         = db.cachedSpendingDao()
    @Provides fun provideCachedContributionDao(db: AppDatabase): CachedContributionDao = db.cachedContributionDao()
}
