package nl.amro.android.app.http

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {

    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val TMDB_BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkYjNmNWMwNjNmNmY5ZmMzODQ0MWM2Yjg5ZWUxNzIwOSIsIm5iZiI6MTc2OTYwNDE2OS42Mywic3ViIjoiNjk3YTA0NDkxY2Q1MmIyYjVkZWYzYjVkIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.lQIY-kZByuFb97e68KP2Dxe9dlOSO1bKY_eS0hmLbsg"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.BODY
        }

        defaultRequest {
            url(TMDB_BASE_URL)
            header(HttpHeaders.Authorization, "Bearer $TMDB_BEARER_TOKEN")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}
