
# AdApp

[![Kotlin](https://img.shields.io/badge/Kotlin-7f52ff?logo=kotlin&logoColor=white)](https://kotlinlang.org/) [![Ktor](https://img.shields.io/badge/Ktor-000000?logo=ktor&logoColor=white)](https://ktor.io/) [![Angular](https://img.shields.io/badge/Angular-DD0031?logo=angular&logoColor=white)](https://angular.io/) [![Postgres](https://img.shields.io/badge/PostgreSQL-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/) [![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=github-actions&logoColor=white)](https://github.com/features/actions) 

---

📌 Descripción
AdApp es una aplicación fullstack para la promoción profesional de artistas locales. Este repositorio contiene el backend en Ktor/Kotlin y la SPA en Angular/TypeScript que consumen la API pública y gestionan contenido multimedia.

Problema que resuelve
- Centralizar publicación y descubrimiento de contenido multimedia (canciones, imágenes) y eventos de artistas.
- Facilitar la relación artista–seguidor (follow/unfollow) y la gestión de perfiles profesionales.

Responsabilidades principales (este repo)
- API REST: usuarios, artistas, canciones, eventos, redes sociales, relaciones follower-artist.
- Autenticación: JWT (stateless).  
- Persistencia: PostgreSQL (H2 usado para desarrollo/tests).  
- Gestión de archivos: subida y entrega (uploads/images, uploads/audio).

---

Flujo general (Arquitectura de Microservicios)

```
Frontend (Angular)
      |
      ↓
  Gateway (API público)
      |
      +----→ users-ms (Kotlin/Ktor)
      |        ├─ PostgreSQL + Prisma
      |        │  ├─ users
      |        │  ├─ follows
      |        │  ├─ password_resets
      |        │  └─ stats
      |        └─ Expone:
      |           ├─ POST /auth/createUser
      |           ├─ POST /auth/login
      |           ├─ PUT /users/{id}
      |           ├─ POST /auth/createUserGoogle
      |           └─ POST /auth/generateToken
      |
      └─→ RabbitMQ (eventos)
             ├─ user.publishedContent (when artist uploads)
             ├─ user.followed (when follower follows)
             └─ [otros eventos de dominio]
```

Flujo de ejemplo:
1. Usuario se registra vía Gateway → `/auth/createUser` → users-ms persiste en PostgreSQL + emite evento `user.created`.  
2. Usuario inicia sesión → `/auth/login` → users-ms valida y retorna JWT.  
3. Artista sube imagen/audio → otros servicios reciben evento `user.publishedContent` vía RabbitMQ.  
4. Follower sigue artista → evento `user.followed` propagado a través de la cola de mensajes.

---

Modelo de datos (síntesis)
- users(id, nombre, correo UNIQUE, contrasena, rol{artist,follower})
- artists(id FK users.id, foto_url, contact_num, description)
- followers(id FK users.id)
- songs(id, artist_id FK, title, url, date_upload)
- events(id, artist_id FK, title, description, event_date, status)
- social_media(id, artist_id FK, url)
- follower_artist(follower_id, artist_id)

(Ver: `AdApp-Backend/DB/database_schema.sql`)

---

Comunicación con otros servicios
- Principal: HTTP/REST (JSON).  
- Autenticación: JWT (configuración en `application.yaml`).  
- Persistencia: PostgreSQL (prod) / H2 (dev/tests).  
- Almacenamiento de archivos: local en el servidor (uploads/images, uploads/audio), servido estáticamente por Ktor.

---

Decisiones técnicas (resumen)
- Ktor + Netty para backend: rendimiento y asincronía con coroutines.  
- Diseño por dominios (Domain / Application / Infrastructure): facilita pruebas y mantenimiento.  
- DI con Koin: permite swapping rápido entre Postgres e InMemory para tests.  
- Repositorios duales para acelerar TDD.  
- JWT para auth: stateless y simple integración con SPA.

---

🎓 El Proceso de Desarrollo

AdApp fue construida adoptando un enfoque ágil, colaborativo y orientado a resolver desafíos reales de una plataforma de gestión de artistas. Cada fase requirió decisiones arquitectónicas precisas y ajustes sobre la marcha.

Fases de Construcción

**Planificación y Diseño**
- Definimos perfiles de usuario: Artistas (publicadores de contenido), Followers (descubridores y seguidores).
- Modelado de datos: PostgreSQL para usuarios, relaciones y metadatos; almacenamiento local (uploads/) para multimedia.
- Arquitectura de microservicios: separación por dominio (Users, Artists, Songs, Events, Followers).
- Estrategia de comunicación: HTTP/REST para operaciones síncronas, RabbitMQ para eventos (user.publishedContent, user.followed).
- Definición de seguridad: JWT + validaciones, OAuth Google (preparado), rate limiting.

**Implementación del Backend**
- Ktor + Kotlin/Netty para rendimiento asincrónico.
- Clean architecture por dominio: Domain (modelos, repositorios), Application (use-cases), Infrastructure (controllers, routes).
- DI con Koin para facilitar pruebas y swapping entre Postgres e InMemory.
- Integración de PostgreSQL con Prisma (ORM tipado).
- Endpoints críticos: `/auth/*`, `/users/*`, `/artists/*`, `/songs/*`, `/events/*`, `/upload/*`.
- RabbitMQ consumer para eventos de dominio.

**Implementación del Frontend**
- SPA en Angular + TypeScript con módulos por features (auth, artist, follower, dashboard).
- Servicios centralizados: ApiService (client HTTP), AuthService (gestión de sesión y JWT).
- Componentes reutilizables: cards, perfiles, formularios.
- Guard de autenticación para proteger rutas.
- Consumo de API con manejo de errores y feedback visual.

**Testing y Optimización**
- Validación de endpoints: registro, login, subida de archivos, seguimiento de artistas.
- Optimizaciones: validación MIME en uploads, manejo de errores uniforme, compresión de imágenes.
- Pruebas unitarias en backend con repositorios InMemory.
- Tests de flujo e2e en frontend (login → registro → búsqueda artista → follow).
- Performance: caching de tokens, lazy loading en listados.

**Despliegue en AWS**
- Backend: EC2 (t3.micro/small), Ktor sobre Netty.
- BD: RDS PostgreSQL (managed).
- Frontend: S3 bucket + CloudFront (CDN).
- Almacenamiento de multimedia: local en servidor (uploads/images, uploads/audio) — servido estáticamente por Ktor.
- CI/CD: GitHub Actions — automatización de build, tests y deploy en cada push a main (ver `.github/workflows/deploy.yml`).

---

Inicio rápido (3 minutos)

**Opción 1: Con Docker (recomendado)**
```bash
docker-compose up
# Frontend: http://localhost:4200
# Backend: http://localhost:8081
# PostgreSQL: localhost:5432
```

**Opción 2: Manual (JDK 11+, Node.js 16+, PostgreSQL local)**

Backend (PowerShell):
```powershell
cd "AdApp-Backend"
.\gradlew.bat run  # Inicia en http://localhost:8081
```

Frontend (PowerShell, otra terminal):
```powershell
cd "AdApp-Frontend\AdApp"
npm install
npm start  # Abre http://localhost:4200
```

---

Pruebas
- Backend:
```powershell
cd "AdApp-Backend"
.\gradlew.bat test
```

---

¿Qué aprendimos en equipo?

Aprendizajes técnicos
- Diseño por dominio aceleró la evolución independiente de componentes.  
- Repositorios InMemory vs Postgres facilitaron TDD y debugging.  
- File storage local simplifica el stack inicial; a futuro se puede migrar a S3/R2 y CDN sin cambios arquitectónicos.  
- Seguridad: JWT + validaciones; falta aplicar hashing adaptativo (bcrypt) si no está ya implementado.

Aprendizajes blandos
- Comunicación clara y asíncrona: PRs con contexto, reuniones focalizadas y respuestas rápidas.  
- Iteración práctica: priorizamos entrega con mejoras continuas.  
- Colaboración en debugging sin egos.

Equipo
- Brian Luis Ruiz Pérez — Backend, BD, arquitecto de software, DevOps, y tester — @MrX-zeta  
- Diego Alberto Zárate — Frontend, maquetado, UX/UI, prototipado y tests — @Diego-Zarate18  
- Karolina Trujillo — Frontend, maquetado y UX — @KarolinaTrujillo  
- Eduardo Montoya — Documentación, liderazgo de proyecto y QA/bugs — @edmonbl

El proyecto se desarrolló de manera óptima: comunicación clara, ambiente colaborativo y decisiones planificadas con anticipación.

---

Roadmap

**V1 (Actual - MVP)**
- Autenticación JWT y registro de usuarios.
- Perfiles de artista + gestión de redes sociales.
- Subida y reproducción de canciones y eventos.
- Sistema de follows (follower-artist).
- Despliegue en AWS (EC2 + RDS + S3).

**V2 (Corto plazo)**
- Hashing de contraseñas (bcrypt).
- Búsqueda y filtrado avanzado de artistas/canciones.
- Notificaciones en tiempo real (WebSocket o Server-Sent Events).
- Rating/comentarios en canciones y eventos.

**V3 (Mediano plazo)**
- Migración de uploads a S3/R2 + CDN.
- Integración OAuth (Google, Spotify).
- Analytics: estadísticas de artista (reproducciones, seguidores, alcance).
- App móvil (React Native o Flutter).

**V4 (Largo plazo)**
- Monetización: tickets de eventos, venta de merch.
- Recomendaciones personalizadas (ML).
- Streaming de eventos en vivo.
- Marketplace de colaboraciones artista-artista.
