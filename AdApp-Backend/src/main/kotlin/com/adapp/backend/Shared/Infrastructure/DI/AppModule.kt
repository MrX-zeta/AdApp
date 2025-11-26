package com.adapp.backend.Shared.Infrastructure.DI

import com.adapp.backend.User.Infrastructure.Repositories.InMemoryUserRepository
import com.adapp.backend.Artist.Infrastructure.Repositories.InMemoryArtistRepository
import com.adapp.backend.SocialMedia.Infrastructure.Repositories.InMemorySocialMediaRepository
import com.adapp.backend.Follower.Infrastructure.Repositories.InMemoryFollowerRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Repositorios como singletons compartidos
    single { InMemoryUserRepository() }
    single { InMemoryArtistRepository() }
    single { InMemorySocialMediaRepository() }
    single { InMemoryFollowerRepository() }
}

