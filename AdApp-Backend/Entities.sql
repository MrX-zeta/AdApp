-- 1. Habilitar la extensión para generar UUIDs (si no está habilitada)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

---
-- TABLA: Usuario
-- Almacena la información base de todos en el sistema (artistas y seguidores).
[cite_start]-- Corresponde a tu diagrama relacional [cite: 114-118] [cite_start]y de clases[cite: 77].
---
CREATE TABLE Usuario (
    [cite_start]-- Usamos UUID en lugar de Int [cite: 78] para IDs únicos
    usuarioId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    [cite_start]nombre VARCHAR(100) NOT NULL[cite: 79],
    [cite_start]correo VARCHAR(255) NOT NULL UNIQUE[cite: 80],
    contrasena VARCHAR(255) NOT NULL, -- ¡Recuerda guardar esto como un HASH!
    [cite_start]rol VARCHAR(50) NOT NULL[cite: 82], -- 'Artista' o 'Usuario general'
    createdAt TIMESTAMPTZ DEFAULT NOW()
);

---
-- TABLA: Artista
-- Almacena los datos *específicos* del perfil de un artista.
[cite_start]-- Se conecta 1-a-1 con la tabla Usuario, implementando la herencia[cite: 133, 138].
---
CREATE TABLE Artista (
    artistaId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- Llave foránea que la conecta con el usuario base
    usuarioId UUID NOT NULL UNIQUE,
    [cite_start]nombreArtistico VARCHAR(100) NOT NULL[cite: 94],
    [cite_start]descripcion TEXT[cite: 96],
    [cite_start]fotoUrl VARCHAR(255)[cite: 98],
    
    CONSTRAINT fk_usuario
        FOREIGN KEY(usuarioId) 
        REFERENCES Usuario(usuarioId)
        ON DELETE CASCADE -- Si el usuario se borra, su perfil de artista también
);

---
-- TABLA: Cancion
-- Almacena las canciones subidas por un artista (Relación 1-a-N).
[cite_start]-- Corresponde a tu diagrama relacional [cite: 156-159].
---
CREATE TABLE Cancion (
    cancionId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    artistaId UUID NOT NULL,
    [cite_start]titulo VARCHAR(150) NOT NULL[cite: 104, 159],
    [cite_start]archivoUrl VARCHAR(255) NOT NULL, -- Para el reproductor [cite: 30]
    [cite_start]duracion INT, -- Duración en segundos [cite: 30]
    
    CONSTRAINT fk_artista
        FOREIGN KEY(artistaId) 
        REFERENCES Artista(artistaId)
        ON DELETE CASCADE -- Si el artista se borra, sus canciones también
);

---
-- TABLA: Evento
-- Almacena los eventos publicados por un artista (Relación 1-a-N).
[cite_start]-- Corresponde a tu diagrama relacional [cite: 142-148].
---
CREATE TABLE Evento (
    eventoId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    artistaId UUID NOT NULL,
    [cite_start]titulo VARCHAR(150) NOT NULL[cite: 88, 145],
    [cite_start]descripcion TEXT[cite: 90, 147],
    [cite_start]fechaEvento TIMESTAMPTZ NOT NULL[cite: 89, 146],
    [cite_start]lugar VARCHAR(255) NOT NULL, -- Definido en tus requerimientos [cite: 31]
    [cite_start]estado VARCHAR(50) DEFAULT 'Próximo'[cite: 148], -- E.g., 'Próximo', 'Cancelado'
    
    CONSTRAINT fk_artista
        FOREIGN KEY(artistaId) 
        REFERENCES Artista(artistaId)
        ON DELETE CASCADE -- Si el artista se borra, sus eventos también
);

---
-- TABLA: RedSocial
-- Almacena las redes sociales de un artista (Relación 1-a-N).
-- Basado en tu diagrama ER.
---
CREATE TABLE RedSocial (
    redSocialId UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    artistaId UUID NOT NULL,
    nombreRed VARCHAR(50) NOT NULL, -- E.g., 'Instagram', 'Facebook', 'Spotify'
    url VARCHAR(255) NOT NULL,
    
    CONSTRAINT fk_artista
        FOREIGN KEY(artistaId) 
        REFERENCES Artista(artistaId)
        ON DELETE CASCADE
);

---
-- TABLA: Seguidores (Tabla PIVOTE/JOIN)
-- Implementa la relación Muchos-a-Muchos (M-N) "Sigue".
-- Un Usuario (seguidor) puede seguir a muchos Artistas.
-- Un Artista puede ser seguido por muchos Usuarios.
---
CREATE TABLE Seguidores (
    -- El 'usuarioId' es el seguidor
    usuarioId UUID NOT NULL,
    -- El 'artistaId' es el seguido
    artistaId UUID NOT NULL,
    
    CONSTRAINT fk_usuario_seguidor
        FOREIGN KEY(usuarioId) 
        REFERENCES Usuario(usuarioId)
        ON DELETE CASCADE,
        
    CONSTRAINT fk_artista_seguido
        FOREIGN KEY(artistaId) 
        REFERENCES Artista(artistaId)
        ON DELETE CASCADE,
        
    -- Clave primaria compuesta para asegurar que un usuario solo siga 1 vez a un artista
    PRIMARY KEY (usuarioId, artistaId)
);