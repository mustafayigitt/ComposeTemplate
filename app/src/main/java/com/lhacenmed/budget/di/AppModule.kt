package com.lhacenmed.budget.di

import com.lhacenmed.budget.BuildConfig
import com.lhacenmed.budget.data.repository.SpendingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL  // ← keep this for Retrofit

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://xoydbvnuftlpradakvky.supabase.co",
        supabaseKey = "sb_publishable__oIzuu76wpHYpnw63o_XAg_KS_BqXBi"
    ) {
        install(Postgrest)
    }

    @Provides
    @Singleton
    fun provideSpendingRepository(client: SupabaseClient) = SpendingRepository(client)
}
