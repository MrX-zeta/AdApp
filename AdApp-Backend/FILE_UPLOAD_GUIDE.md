# Guía de uso: Subida de archivos (Imágenes y Audio)

## ✅ Backend configurado exitosamente

El backend ya está listo para manejar:
- ✅ Subida de imágenes
- ✅ Subida de archivos de audio
- ✅ Servir archivos estáticos
- ✅ Eliminación de archivos

---

## 📁 Estructura de almacenamiento

Los archivos se guardan en:
```
AdApp-Backend/
└── uploads/
    ├── images/     ← Imágenes (jpg, png, gif, etc.)
    └── audio/      ← Audio (mp3, wav, ogg, etc.)
```

---

## 🔌 Endpoints disponibles

### 1. **Subir imagen**
```http
POST http://127.0.0.1:8081/upload/image
Content-Type: multipart/form-data

file: [seleccionar archivo de imagen]
```

**Respuesta exitosa:**
```json
{
  "message": "Image uploaded successfully",
  "url": "/uploads/images/1732800000000_a1b2c3d4.jpg"
}
```

### 2. **Subir audio**
```http
POST http://127.0.0.1:8081/upload/audio
Content-Type: multipart/form-data

file: [seleccionar archivo de audio]
```

**Respuesta exitosa:**
```json
{
  "message": "Audio uploaded successfully",
  "url": "/uploads/audio/1732800000000_e5f6g7h8.mp3"
}
```

### 3. **Acceder a un archivo**
```http
GET http://127.0.0.1:8081/uploads/images/1732800000000_a1b2c3d4.jpg
```
Devuelve el archivo directamente.

### 4. **Eliminar archivo**
```http
DELETE http://127.0.0.1:8081/upload?path=/uploads/images/1732800000000_a1b2c3d4.jpg
```

**Respuesta exitosa:**
```json
{
  "message": "File deleted successfully"
}
```

---

## 🧪 Pruebas con Postman

### Subir una imagen:

1. Crea una nueva request en Postman
2. Método: **POST**
3. URL: `http://127.0.0.1:8081/upload/image`
4. En la pestaña **Body**:
   - Selecciona **form-data**
   - Agrega un campo con key: `file`
   - Cambia el tipo a **File**
   - Selecciona una imagen de tu computadora
5. Envía la request
6. Copia la URL que regresa en `"url"`
7. Pégala en el navegador: `http://127.0.0.1:8081/uploads/images/...`

---

## 💻 Uso desde el frontend (Angular)

```typescript
// Subir imagen
uploadImage(file: File): Observable<any> {
  const formData = new FormData();
  formData.append('file', file);
  
  return this.http.post('http://127.0.0.1:8081/upload/image', formData);
}

// Ejemplo de uso en componente
onFileSelected(event: any) {
  const file: File = event.target.files[0];
  
  this.uploadImage(file).subscribe({
    next: (response) => {
      console.log('Imagen subida:', response.url);
      // Guardar la URL en el modelo del artista
      this.artist.fotoUrl = response.url;
    },
    error: (error) => {
      console.error('Error al subir imagen:', error);
    }
  });
}
```

---

## 📝 Formatos soportados

### Imágenes:
- image/jpeg (.jpg, .jpeg)
- image/png (.png)
- image/gif (.gif)
- image/webp (.webp)
- image/svg+xml (.svg)

### Audio:
- audio/mpeg (.mp3)
- audio/wav (.wav)
- audio/ogg (.ogg)
- audio/aac (.aac)
- audio/flac (.flac)

---

## ⚠️ Consideraciones importantes

1. **Seguridad**: 
   - Los archivos se validan por tipo MIME
   - Se generan nombres únicos para evitar colisiones

2. **Tamaño de archivos**:
   - Por defecto, Ktor no tiene límite
   - Puedes configurar un límite en `application.yaml`

3. **Persistencia**:
   - Los archivos se guardan en disco local
   - Si reinicias el servidor, los archivos persisten
   - Para producción, considera usar un CDN (AWS S3, Cloudinary, etc.)

4. **URLs relativas**:
   - El backend devuelve URLs relativas: `/uploads/images/...`
   - El frontend debe completar con el dominio: `http://127.0.0.1:8081/uploads/images/...`

---

## 🚀 Siguiente paso

Para integrar con el modelo de Artist o Song, solo necesitas:

1. Subir el archivo primero
2. Obtener la URL del response
3. Guardar esa URL en el campo correspondiente (`fotoUrl` o `audioUrl`)

**Ejemplo completo:**
```typescript
// 1. Subir imagen
this.uploadImage(file).subscribe(response => {
  // 2. Actualizar artista con la URL
  this.artistService.updateArtist({
    id: this.artistId,
    fotoUrl: response.url,  // ← URL de la imagen subida
    // ... otros campos
  }).subscribe(() => {
    console.log('Artista actualizado con nueva foto');
  });
});
```

