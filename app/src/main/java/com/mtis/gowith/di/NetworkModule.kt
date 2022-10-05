package com.mtis.gowith.di

import com.mtis.gowith.widget.utils.Utils.API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor()).build()
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .client(provideHttpClient())
            .addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory():GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    // To do : API 연결이 필요할 경우 API 서비스를 연결해야한다.
//    @Provides
//    @Singleton
//    fun provideGithubApiService(retrofit:Retrofit):GithubAPI {
//        return retrofit.create(GithubApi::class.java)
//    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
}