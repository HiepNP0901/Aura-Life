package com.drs.auralife.data

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://phimapi.com"

    @Volatile
    private var retrofitInstance: Retrofit? = null

    private fun createOkHttpClient(context: Context): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong() // 5MB
        val appContext = context.applicationContext
        val cache = Cache(appContext.cacheDir, cacheSize)

        return OkHttpClient
            .Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .header("Cache-Control", "public, max-age=${86400}")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }.build()
    }

    fun create(context: Context): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            retrofitInstance ?: Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(createOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofitInstance = it }
        }
    }
}
