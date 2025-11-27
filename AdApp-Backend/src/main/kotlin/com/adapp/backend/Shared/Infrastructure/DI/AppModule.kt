package com.adapp.backend.Shared.Infrastructure.DI

import com.adapp.backend.User.Infrastructure.Repositories.InMemoryUserRepository
import com.adapp.backend.Artist.Infrastructure.Repositories.InMemoryArtistRepository
import com.adapp.backend.SocialMedia.Infrastructure.Repositories.InMemorySocialMediaRepository
import com.adapp.backend.Follower.Infrastructure.Repositories.InMemoryFollowerRepository
import com.adapp.backend.FollowerArtist.Infraestructure.Repositories.InMemoryFollowerArtistRepository
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository
import com.adapp.backend.FollowerArtist.Application.FollowArtist.FollowArtist
import com.adapp.backend.FollowerArtist.Application.UnfollowArtist.UnfollowArtist
import com.adapp.backend.FollowerArtist.Application.GetFollowedArtists.GetFollowedArtists
import com.adapp.backend.FollowerArtist.Application.GetArtistFollowers.GetArtistFollowers
import com.adapp.backend.FollowerArtist.Infraestructure.Controllers.KtorFollowerArtistController
import org.koin.dsl.module

val repositoryModule = module {
    // Repositorios como singletons compartidos
    single { InMemoryUserRepository() }
    single { InMemoryArtistRepository() }
    single { InMemorySocialMediaRepository() }
    single { InMemoryFollowerRepository() }
    single<FollowerArtistRepository> { InMemoryFollowerArtistRepository() }
    
    // FollowerArtist Use Cases
    single { FollowArtist(get()) }
    single { UnfollowArtist(get()) }
    single { GetFollowedArtists(get()) }
    single { GetArtistFollowers(get()) }
    
    // FollowerArtist Controller
    single { KtorFollowerArtistController(get(), get(), get(), get(), get()) }
}

