package com.adapp.backend.Shared.Infrastructure.DI

import com.adapp.backend.User.Infrastructure.Repositories.PostgresUserRepository
import com.adapp.backend.User.Domain.Repositories.UserRepository
import com.adapp.backend.Artist.Infrastructure.Repositories.PostgresArtistRepository
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.SocialMedia.Infrastructure.Repositories.PostgresSocialMediaRepository
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository
import com.adapp.backend.Follower.Infrastructure.Repositories.PostgresFollowerRepository
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.Event.Infrastructure.Repositories.PostgresEventRepository
import com.adapp.backend.Event.Domain.Repositories.EventRepository
import com.adapp.backend.FollowerArtist.Infraestructure.Repositories.PostgresFollowerArtistRepository
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository
import com.adapp.backend.FollowerArtist.Application.FollowArtist.FollowArtist
import com.adapp.backend.FollowerArtist.Application.UnfollowArtist.UnfollowArtist
import com.adapp.backend.FollowerArtist.Application.GetFollowedArtists.GetFollowedArtists
import com.adapp.backend.FollowerArtist.Application.GetArtistFollowers.GetArtistFollowers
import com.adapp.backend.FollowerArtist.Infraestructure.Controllers.KtorFollowerArtistController
import com.adapp.backend.Song.Infrastructure.Routes.PostgresSongsRepository
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.Shared.Infrastructure.Services.FileUploadService
import org.koin.dsl.module

val repositoryModule = module {
    // Repositorios como singletons compartidos
    // PostgreSQL para User, Artist y SocialMedia
    single<UserRepository> { PostgresUserRepository() }
    single<ArtistRepository> { PostgresArtistRepository() }
    single<SocialMediaRepository> { PostgresSocialMediaRepository() }

    // PostgreSQL para Follower, Event, FollowerArtist y Song
    single<FollowerRepository> { PostgresFollowerRepository() }
    single<EventRepository> { PostgresEventRepository() }
    single<FollowerArtistRepository> { PostgresFollowerArtistRepository() }
    single<SongRepository> { PostgresSongsRepository() }

    // FollowerArtist Use Cases
    single { FollowArtist(get()) }
    single { UnfollowArtist(get()) }
    single { GetFollowedArtists(get()) }
    single { GetArtistFollowers(get()) }
    
    // FollowerArtist Controller
    single { KtorFollowerArtistController(get(), get(), get(), get(), get()) }

    // Servicios
    single { FileUploadService() }
}
