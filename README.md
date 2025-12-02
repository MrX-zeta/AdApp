# AdApp

## Descripción del Proyecto
AdApp es una plataforma web desarrollada por el equipo DEVBOX de la Universidad Politécnica de Chiapas. Su propósito es centralizar y profesionalizar la promoción de artistas locales (músicos, cantantes y bandas), permitiéndoles crear perfiles profesionales, gestionar contenido musical y difundir eventos para conectar con su audiencia.

## Objetivos
* **General:** Desarrollar una plataforma web para el registro y promoción de artistas locales mediante la gestión de contenido multimedia y agenda de eventos.
* **Específicos:**
  * Implementar sistema de registro y autenticación.
  * Incorporar módulo de carga y reproducción de audio.
  * Permitir publicación y visualización de eventos.
  * Garantizar una interfaz intuitiva y accesible.

## Funcionalidades (Requerimientos)
El sistema cubre los siguientes requerimientos funcionales:
* **F-1 Registro de Artistas:** Gestión de datos básicos, biografía y redes sociales.
* **F-2 Reproducción de Canciones:** Carga de archivos de audio y reproducción en línea.
* **F-3 Publicación de Eventos:** Gestión de fechas, lugares y descripciones de presentaciones.
* **F-4 Visualización de Eventos:** Listado de actividades próximas para usuarios.
* **F-5 Gestión de Perfil:** Edición de contenido y datos del artista.
* **F-6 Contacto Directo:** Visualización de medios de contacto para contrataciones.

## Stack Tecnológico
**Frontend:**
* Angular + TypeScript
* Tailwind CSS
* Despliegue: AWS S3 + Cloudflare.

**Backend:**
* Ktor + JavaScript/TypeScript
* Despliegue: AWS EC2.

**Base de Datos y Almacenamiento:**
* PostgreSQL (AWS RDS/EC2)
* AWS S3 Bucket (Multimedia).
