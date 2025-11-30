-- =========================================
-- Script SQL para AdApp - Backend Kotlin
-- Coincide con los modelos del Domain
-- =========================================

-- Eliminar tablas si existen (en orden inverso por dependencias)
DROP TABLE IF EXISTS follower_artist CASCADE;
DROP TABLE IF EXISTS social_media CASCADE;
DROP TABLE IF EXISTS songs CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS artists CASCADE;
DROP TABLE IF EXISTS followers CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =========================================
-- TABLA: users
-- Modelo: User (Usuarioid, nombre, correo, contrasena, rol)
-- =========================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena VARCHAR(255),
    rol VARCHAR(20) CHECK (rol IN ('artist', 'follower'))
);

COMMENT ON TABLE users IS 'Tabla base de usuarios (artistas y seguidores)';
COMMENT ON COLUMN users.id IS 'ID del usuario (Usuarioid en el backend)';
COMMENT ON COLUMN users.nombre IS 'Nombre del usuario (UserName)';
COMMENT ON COLUMN users.correo IS 'Email del usuario (UserEmail)';
COMMENT ON COLUMN users.contrasena IS 'Contraseña del usuario (UserPsswd)';
COMMENT ON COLUMN users.rol IS 'Rol: artist o follower (UserRol)';

-- =========================================
-- TABLA: artists
-- Modelo: Artist (hereda User + fotoUrl, contactNum, description)
-- =========================================
CREATE TABLE artists (
    id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    foto_url VARCHAR(500),
    contact_num VARCHAR(20),
    description TEXT
);

COMMENT ON TABLE artists IS 'Información extendida de usuarios artistas';
COMMENT ON COLUMN artists.id IS 'FK a users.id (ArtistId)';
COMMENT ON COLUMN artists.foto_url IS 'URL de foto de perfil (ArtistFotoUrl)';
COMMENT ON COLUMN artists.contact_num IS 'Número de contacto (ArtistContactNum)';
COMMENT ON COLUMN artists.description IS 'Descripción del artista (ArtistDescription)';

-- =========================================
-- TABLA: followers
-- Modelo: Follower (hereda User)
-- =========================================
CREATE TABLE followers (
    id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE followers IS 'Usuarios seguidores de artistas';
COMMENT ON COLUMN followers.id IS 'FK a users.id (FollowerId)';

-- =========================================
-- TABLA: songs
-- Modelo: Song (SongId, artistId, title, url)
-- =========================================
CREATE TABLE songs (
    id SERIAL PRIMARY KEY,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    title VARCHAR(200),
    url VARCHAR(500)
);

COMMENT ON TABLE songs IS 'Canciones publicadas por artistas';
COMMENT ON COLUMN songs.id IS 'ID de la canción (SongId)';
COMMENT ON COLUMN songs.artist_id IS 'FK a artists.id (artistId: UserId)';
COMMENT ON COLUMN songs.title IS 'Título de la canción (SongTitle)';
COMMENT ON COLUMN songs.url IS 'URL del archivo de audio (SongUrl)';

-- =========================================
-- TABLA: events
-- Modelo: Event (Event_Id, ArtistId, title, description, Event_Date, Status)
-- =========================================
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    title VARCHAR(200),
    description TEXT,
    event_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'cancelled', 'completed'))
);

COMMENT ON TABLE events IS 'Eventos organizados por artistas';
COMMENT ON COLUMN events.id IS 'ID del evento (Event_Id: EventId)';
COMMENT ON COLUMN events.artist_id IS 'FK a artists.id (ArtistId: UserId)';
COMMENT ON COLUMN events.title IS 'Título del evento (EventTitle)';
COMMENT ON COLUMN events.description IS 'Descripción del evento (EventDescription)';
COMMENT ON COLUMN events.event_date IS 'Fecha y hora del evento (Event_Date: EventDate)';
COMMENT ON COLUMN events.status IS 'Estado: active, cancelled, completed (Status: EventStatus)';

-- =========================================
-- TABLA: social_media
-- Modelo: SocialMedia (SocialMediaId, artistId, url)
-- =========================================
CREATE TABLE social_media (
    id SERIAL PRIMARY KEY,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    url VARCHAR(500)
);

COMMENT ON TABLE social_media IS 'Redes sociales de artistas';
COMMENT ON COLUMN social_media.id IS 'ID de la red social (SocialMediaId)';
COMMENT ON COLUMN social_media.artist_id IS 'FK a artists.id (artistId: UserId)';
COMMENT ON COLUMN social_media.url IS 'URL de la red social (SocialMediaUrl)';

-- =========================================
-- TABLA: follower_artist
-- Modelo: FollowerArtist (followerId, artistId)
-- Relación muchos a muchos
-- =========================================
CREATE TABLE follower_artist (
    follower_id INTEGER NOT NULL REFERENCES followers(id) ON DELETE CASCADE,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    PRIMARY KEY (follower_id, artist_id)
);

COMMENT ON TABLE follower_artist IS 'Relación muchos a muchos: seguidores ↔ artistas';
COMMENT ON COLUMN follower_artist.follower_id IS 'FK a followers.id';
COMMENT ON COLUMN follower_artist.artist_id IS 'FK a artists.id';

-- =========================================
-- ÍNDICES para optimizar consultas
-- =========================================

-- Users
CREATE INDEX idx_users_correo ON users(correo);
CREATE INDEX idx_users_rol ON users(rol);

-- Artists
CREATE INDEX idx_artists_id ON artists(id);

-- Songs
CREATE INDEX idx_songs_artist_id ON songs(artist_id);

-- Events
CREATE INDEX idx_events_artist_id ON events(artist_id);
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_status ON events(status);

-- Social Media
CREATE INDEX idx_social_media_artist_id ON social_media(artist_id);

-- Follower-Artist
CREATE INDEX idx_follower_artist_follower ON follower_artist(follower_id);
CREATE INDEX idx_follower_artist_artist ON follower_artist(artist_id);

-- =========================================
-- FIN DEL SCRIPT
-- Tablas creadas sin datos de prueba
-- =========================================

