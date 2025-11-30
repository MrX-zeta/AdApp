# Guía de Subida de Audio - Backend

## ✅ Configuración Completada

El backend ya está configurado para manejar la subida de archivos de audio con las siguientes características:

### 📁 Estructura de Directorios
```
uploads/
├── images/     # Imágenes de perfil
└── audio/      # Archivos de audio (canciones)
```

### 🎵 Formatos de Audio Soportados
- **MP3** (audio/mpeg, audio/mp3)
- **WAV** (audio/wav, audio/x-wav, audio/wave)
- **OGG** (audio/ogg)
- **M4A** (audio/mp4, audio/x-m4a)
- **FLAC** (audio/flac)
- **AAC** (audio/aac)
- **WMA** (audio/x-ms-wma)

### 📊 Límites de Tamaño
- **Tamaño máximo:** 50 MB por archivo
- **Configurado en:** `application.yaml` y `FileUploadService.kt`

## 🔌 Endpoints Disponibles

### 1. Subir Audio
```http
POST /upload/audio
Content-Type: multipart/form-data

Body:
- file: [archivo de audio]
```

**Respuesta exitosa (201):**
```json
{
  "message": "Audio uploaded successfully",
  "audioUrl": "/uploads/audio/1732896543210_a7b3c4d5.mp3"
}
```

**Respuesta error (400):**
```json
{
  "message": "Invalid file type. Only audio files are allowed."
}
```

### 2. Subir Imagen
```http
POST /upload/image
Content-Type: multipart/form-data

Body:
- file: [archivo de imagen]
```

### 3. Eliminar Archivo
```http
DELETE /upload?path=/uploads/audio/archivo.mp3
```

### 4. Acceder a Archivos
```http
GET /uploads/audio/archivo.mp3
GET /uploads/images/foto.jpg
```

## 🧪 Pruebas con cURL

### Subir un archivo MP3
```bash
curl -X POST http://localhost:8081/upload/audio \
  -F "file=@cancion.mp3"
```

### Subir un archivo WAV
```bash
curl -X POST http://localhost:8081/upload/audio \
  -F "file=@audio.wav"
```

### Eliminar un archivo
```bash
curl -X DELETE "http://localhost:8081/upload?path=/uploads/audio/1732896543210_a7b3c4d5.mp3"
```

## 🔍 Verificación

1. **Verificar que los directorios existen:**
   ```bash
   ls -la uploads/
   ls -la uploads/audio/
   ls -la uploads/images/
   ```

2. **Verificar que el servicio está corriendo:**
   ```bash
   curl http://localhost:8081/uploads/test
   # Respuesta esperada: "File service is working!"
   ```

3. **Verificar que los archivos se sirven correctamente:**
   ```bash
   # Después de subir un archivo, acceder a él:
   curl http://localhost:8081/uploads/audio/nombre-del-archivo.mp3
   ```

## 🛠️ Servicios y Archivos Modificados

### Archivos Actualizados:
1. **FileUploadService.kt** - Validación mejorada de formatos de audio
2. **FileUploadRouting.kt** - Endpoint `/upload/audio` ya configurado
3. **StaticFiles.kt** - Servir archivos desde `/uploads`
4. **application.yaml** - Límite de 50MB configurado
5. **Serialization.kt** - ContentNegotiation configurado

### Dependencias de Koin:
- `FileUploadService` está registrado en `AppModule.kt`
- Inyección automática disponible en todas las rutas

## 🎯 Integración con Songs

El endpoint de canciones debe usar el servicio de subida así:

```kotlin
post("/songs") {
    val multipart = call.receiveMultipart()
    var audioUrl: String? = null
    var title: String? = null
    
    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                audioUrl = fileService.saveAudio(part)
            }
            is PartData.FormItem -> {
                if (part.name == "title") title = part.value
            }
            else -> {}
        }
        part.dispose()
    }
    
    // Guardar canción con audioUrl en la base de datos
}
```

## ✨ Características de Seguridad

- ✅ Validación de tipo de archivo (MIME type)
- ✅ Nombres únicos con timestamp + UUID
- ✅ Límite de tamaño de archivo
- ✅ Directorios separados por tipo
- ✅ Manejo de errores robusto
- ✅ CORS configurado para localhost:4200

## 📝 Notas Importantes

1. Los directorios se crean automáticamente al iniciar el servidor
2. Los archivos mantienen su extensión original
3. Los nombres se generan con: `{timestamp}_{uuid}.{extension}`
4. El path relativo se guarda en la base de datos
5. Los archivos se sirven a través de `/uploads/audio/{filename}`

## 🚀 Todo Listo

El backend está **completamente configurado** para:
- ✅ Recibir archivos de audio de hasta 50MB
- ✅ Validar formatos de audio comunes
- ✅ Almacenar archivos en `uploads/audio/`
- ✅ Servir archivos estáticos
- ✅ Eliminar archivos cuando sea necesario
