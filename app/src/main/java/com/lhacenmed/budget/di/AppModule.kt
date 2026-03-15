package com.lhacenmed.budget.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.lhacenmed.budget.BuildConfig
import com.lhacenmed.budget.data.local.AppDatabase
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
            // Load any stored session from SharedPreferences immediately on startup —
            // no network call required. If a valid (or recently-expired) session exists,
            // the SDK emits Authenticated right away so the user enters the app offline.
            autoLoadFromStorage = true

            // Do NOT force a token refresh on every cold start. The SDK will refresh
            // lazily when a network request actually needs a fresh token. This prevents
            // the app blocking on startup when the device is offline.
            alwaysAutoRefresh = false
        }
        install(Postgrest)
    }

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "budget_db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides @Singleton
    fun provideSpendingRepository(client: SupabaseClient, db: AppDatabase) =
        SpendingRepository(client, db)

    @Provides
    fun providePendingItemDao(db: AppDatabase): PendingItemDao = db.pendingItemDao()

    @Provides
    fun provideGroceryDao(db: AppDatabase): GroceryDao = db.groceryDao()
}
