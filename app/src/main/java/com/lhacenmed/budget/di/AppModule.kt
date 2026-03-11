package com.lhacenmed.budget.di

import android.content.Context
import androidx.room.Room
import com.lhacenmed.budget.BuildConfig
import com.lhacenmed.budget.data.local.AppDatabase
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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://xoydbvnuftlpradakvky.supabase.co",
        supabaseKey = "sb_publishable__oIzuu76wpHYpnw63o_XAg_KS_BqXBi"
    ) {
        install(Auth)
        install(Postgrest)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "budget.db").build()

    @Provides
    @Singleton
    fun provideSpendingRepository(
        client: SupabaseClient,
        db: AppDatabase
    ) = SpendingRepository(client, db)
}
