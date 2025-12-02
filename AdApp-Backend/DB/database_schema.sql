-- TABLA: users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena VARCHAR(255),
    rol VARCHAR(20) CHECK (rol IN ('artist', 'follower'))
);

-- TABLA: artists
CREATE TABLE artists (
    id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    foto_url VARCHAR(500),
    contact_num VARCHAR(20),
    description TEXT
);

-- TABLA: followers
CREATE TABLE followers (
    id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

-- TABLA: songs
CREATE TABLE songs (
    id SERIAL PRIMARY KEY,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    title VARCHAR(200),
    url VARCHAR(500),
    date_upload BIGINT
);

-- TABLA: events
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    title VARCHAR(200),
    description TEXT,
    event_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'cancelled', 'completed'))
);

-- TABLA: social_media
CREATE TABLE social_media (
    id SERIAL PRIMARY KEY,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    url VARCHAR(500)
);

-- TABLA: follower_artist
CREATE TABLE follower_artist (
    follower_id INTEGER NOT NULL REFERENCES followers(id) ON DELETE CASCADE,
    artist_id INTEGER NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    PRIMARY KEY (follower_id, artist_id)
);

-- ÍNDICES
CREATE INDEX idx_users_correo ON users(correo);
CREATE INDEX idx_songs_artist_id ON songs(artist_id);