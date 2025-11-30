import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';

interface ArtistResponse {
  id: number;
  nombre: string;
  correo: string;
  contrasena: string;
  rol: string;
  fotoUrl: string;
  contactNum: string;
  instagram?: string;
  facebook?: string;
  description?: string;
}

interface SocialMediaResponse {
  SocialMediaId: number;
  artistId: number;
  url: string;
}

@Component({
  selector: 'app-artist-edit',
  standalone: false,
  templateUrl: './artist-edit.html',
  styleUrl: './artist-edit.css',
})
export class ArtistEdit implements OnInit {
  form!: FormGroup;
  songForm!: FormGroup;
  eventForm!: FormGroup;
  showSongModal = false;
  showEventModal = false;
  showSuccessModal = false;
  songFile?: File;
  artistId?: number;
  isLoading = false;
  songs: any[] = [];
  events: any[] = [];
  editingSongId?: number;
  editingEventId?: number;
  followers: number = 0;
  profileImageUrl: string = '/media/icons/perfil.png';
  isUploadingImage: boolean = false;
  currentPassword: string = '';
  audioPlayer: HTMLAudioElement | null = null;
  currentPlayingSong: any = null;

  phoneValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null; // Allow empty
    }
    const phonePattern = /^[0-9\s\-\+\(\)]{7,}$/;
    return phonePattern.test(control.value) ? null : { invalidPhone: true };
  }

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.maxLength(60)]],
      description: ['', [Validators.required, Validators.maxLength(240)]],
      instagram: [''],
      facebook: [''],
      phone: ['', this.phoneValidator.bind(this)],
      email: ['', [Validators.email]],
    });

    this.songForm = this.fb.nonNullable.group({
      title: ['', [Validators.required, Validators.maxLength(80)]],
      audio: [null, Validators.required],
    });

    this.eventForm = this.fb.nonNullable.group({
      title: ['', [Validators.required, Validators.maxLength(80)]],
      description: ['', [Validators.required, Validators.maxLength(240)]],
    });
  }

  ngOnInit(): void {
    // Get artist ID from route params
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      console.log('Route param id:', id);
      if (id) {
        this.artistId = parseInt(id, 10);
        console.log('Artist ID set to:', this.artistId);
        this.loadArtist(this.artistId);
      } else {
        console.warn('No ID found in route params');
        // Si no hay ID en la ruta, intentar obtener del usuario actual
        const currentUser = localStorage.getItem('currentUser');
        if (currentUser) {
          const user = JSON.parse(currentUser);
          this.artistId = user.id;
          console.log('Using current user ID:', this.artistId);
          if (this.artistId) {
            this.loadArtist(this.artistId);
          }
        }
      }
    });
  }

  loadArtist(id: number): void {
    console.log('=== LOADING ARTIST DATA ===');
    console.log('Loading artist with ID:', id);
    this.isLoading = true;

    // Cargar datos básicos del artista
    this.apiService.get<ArtistResponse>(`/artist/${id}/`).subscribe({
      next: (artist) => {
        console.log('=== ARTIST DATA RECEIVED ===');
        console.log('Raw artist data:', artist);
        
        // Cargar redes sociales desde /sm
        this.apiService.get<SocialMediaResponse[]>('/sm').subscribe({
          next: (socialMediaList) => {
            console.log('=== SOCIAL MEDIA DATA RECEIVED ===');
            console.log('All social media:', socialMediaList);
            
            // Filtrar redes sociales del artista actual
            const artistSocialMedia = socialMediaList.filter(sm => sm.artistId === id);
            console.log('Artist social media:', artistSocialMedia);
            
            // Extraer Instagram y Facebook con prefijo
            const instagramEntry = artistSocialMedia.find(sm => 
              sm.url.toLowerCase().startsWith('instagram:')
            );
            const instagram = instagramEntry 
              ? instagramEntry.url.replace(/^instagram:/i, '') 
              : '';
            
            const facebookEntry = artistSocialMedia.find(sm => 
              sm.url.toLowerCase().startsWith('facebook:')
            );
            const facebook = facebookEntry 
              ? facebookEntry.url.replace(/^facebook:/i, '') 
              : '';
            
            console.log('Instagram entry:', instagramEntry);
            console.log('Extracted Instagram:', instagram);
            console.log('Facebook entry:', facebookEntry);
            console.log('Extracted Facebook:', facebook);
            
            // Actualizar el formulario con todos los datos
            this.form.patchValue({
              name: artist.nombre || '',
              description: artist.description || '',
              instagram: instagram,
              facebook: facebook,
              phone: artist.contactNum || '',
              email: artist.correo || '',
            });
            
            // Cargar foto de perfil si existe
            if (artist.fotoUrl) {
              this.profileImageUrl = `http://localhost:8081${artist.fotoUrl}`;
            }
            
            // Guardar la contraseña actual para futuras actualizaciones
            this.currentPassword = artist.contrasena;
            
            console.log('Form after patchValue:', this.form.value);
            this.loadSongs(id);
            this.loadEvents(id);
            this.isLoading = false;
          },
          error: (error) => {
            console.error('=== ERROR LOADING SOCIAL MEDIA ===');
            console.error('Error details:', error);
            
            // Aún así llenar los datos básicos del artista
            this.form.patchValue({
              name: artist.nombre || '',
              description: artist.description || '',
              instagram: '',
              facebook: '',
              phone: artist.contactNum || '',
              email: artist.correo || '',
            });
            
            this.isLoading = false;
          }
        });
      },
      error: (error) => {
        console.error('=== ERROR LOADING ARTIST ===');
        console.error('Error details:', error);
        this.isLoading = false;
        alert('Error al cargar el artista');
      }
    });
  }

  loadSongs(artistId: number): void {
    this.apiService.get<any[]>('/songs').subscribe({
      next: (songs) => {
        this.songs = songs
          .filter(s => s.artistId === artistId)
          .map(s => ({
            ...s,
            date: s.dateUploaded || Date.now()
          }));
        console.log('Songs loaded:', this.songs);
      },
      error: (error) => {
        console.error('Error loading songs:', error);
      }
    });
  }

  loadEvents(artistId?: number): void {
    const targetArtistId = artistId || this.artistId;
    if (!targetArtistId) {
      console.error('No artist ID available to load events');
      return;
    }

    this.apiService.get<any[]>('/event').subscribe({
      next: (events) => {
        this.events = events
          .filter(e => e.artistId === targetArtistId)
          .map(e => ({
            ...e,
            date: e.dateEvent || e.eventDate || Date.now()
          }));
        console.log('Events loaded:', this.events);
      },
      error: (error) => {
        console.error('Error loading events:', error);
        this.events = [];
      }
    });
  }

  addSong() {
    this.editingSongId = undefined;
    this.songForm.reset();
    this.songFile = undefined;
    this.showSongModal = true;
  }

  publishEvent() {
    this.editingEventId = undefined;
    this.eventForm.reset({
      title: '',
      description: ''
    });
    this.showEventModal = true;
  }

  save() {
    console.log('=== SAVE METHOD CALLED ===');
    console.log('Form valid:', this.form.valid);
    console.log('Artist ID:', this.artistId);
    
    if (this.form.invalid) {
      console.warn('Form is invalid');
      this.form.markAllAsTouched();
      return;
    }

    if (!this.artistId) {
      console.error('Artist ID is missing!');
      alert('No se puede guardar: ID de artista no encontrado');
      return;
    }

    const formValue = this.form.getRawValue();
    console.log('Form value:', formValue);
    
    // Extraer solo el path relativo de la foto (sin http://localhost:8081)
    const photoPath = this.profileImageUrl.includes('http://localhost:8081') 
      ? this.profileImageUrl.replace('http://localhost:8081', '')
      : (this.profileImageUrl === '/media/icons/perfil.png' ? '' : this.profileImageUrl);
    
    const payload = {
      id: this.artistId,
      nombre: formValue.name,
      correo: formValue.email,
      contrasena: this.currentPassword, // Keep existing password
      rol: 'artist',
      fotoUrl: photoPath,
      contactNum: formValue.phone || '',
      instagram: formValue.instagram || '',
      facebook: formValue.facebook || '',
      description: formValue.description || ''
    };

    console.log('Payload to send:', payload);
    console.log('PUT URL:', `/artist/${this.artistId}/`);

    this.isLoading = true;
    this.apiService.put(`/artist/${this.artistId}/`, payload).subscribe({
      next: (response) => {
        console.log('Artist updated successfully', response);
        this.isLoading = false;
        this.showSuccessModal = true;
        setTimeout(() => {
          this.router.navigate(['/artist/profile']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error updating artist:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error body:', error.error);
        this.isLoading = false;
        alert(`Error al actualizar el artista: ${error.error?.message || error.message}`);
      }
    });
  }

  cancel() {
    console.log('Cancelar edición');
    this.router.navigate(['/artist/profile']);
  }

  // Modal helpers
  closeSong() { this.showSongModal = false; this.songForm.reset(); this.songFile = undefined; }
  closeEvent() { this.showEventModal = false; this.eventForm.reset(); }
  closeSuccessModal() { this.showSuccessModal = false; this.router.navigate(['/artist/profile']); }

  onSongFileSelected(evt: Event) {
    const input = evt.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.songFile = input.files[0];
      this.songForm.patchValue({ audio: this.songFile.name });
    }
  }

  submitSong() {
    if (this.editingSongId) {
      // Editar canción existente (solo título)
      if (this.songForm.invalid) { this.songForm.markAllAsTouched(); return; }
      
      const song = this.songs.find(s => s.id === this.editingSongId);
      if (!song) {
        alert('Error: No se encontró la canción');
        return;
      }

      const payload = { 
        id: this.editingSongId,
        title: this.songForm.get('title')!.value,
        artistId: this.artistId!,
        url: song.url // Mantener la URL existente
      };
      
      this.isLoading = true;
      this.apiService.put(`/songs/${this.editingSongId}/`, payload).subscribe({
        next: () => {
          console.log('Canción actualizada');
          if (song) song.title = payload.title;
          this.isLoading = false;
          this.closeSong();
          this.loadSongs(this.artistId!);
        },
        error: (error) => {
          console.error('Error al actualizar canción:', error);
          alert('Error al actualizar la canción');
          this.isLoading = false;
        }
      });
    } else {
      // Crear nueva canción con subida de archivo
      if (this.songForm.invalid || !this.songFile) { 
        this.songForm.markAllAsTouched(); 
        if (!this.songFile) {
          alert('Por favor selecciona un archivo de audio');
        }
        return; 
      }

      if (!this.artistId) {
        alert('Error: No se pudo identificar el artista');
        return;
      }

      this.isLoading = true;
      console.log('Subiendo archivo de audio...');

      // Paso 1: Subir el archivo de audio
      const audioFormData = new FormData();
      audioFormData.append('file', this.songFile);

      this.apiService.post<{ audioUrl: string }>('/upload/audio', audioFormData).subscribe({
        next: (uploadResponse) => {
          console.log('Audio subido:', uploadResponse);
          
          // Paso 2: Crear el registro de la canción en la BD
          const newId = this.songs.length > 0 ? Math.max(...this.songs.map(s => s.id)) + 1 : 1;
          const songPayload = {
            id: newId,
            artistId: this.artistId!,
            title: this.songForm.get('title')!.value,
            url: uploadResponse.audioUrl,
            dateUploaded: Date.now()
          };

          this.apiService.post('/songs', songPayload).subscribe({
            next: (response) => {
              console.log('Canción creada exitosamente:', response);
              this.isLoading = false;
              this.closeSong();
              this.loadSongs(this.artistId!);
              alert('Canción subida exitosamente');
            },
            error: (error) => {
              console.error('Error al crear canción:', error);
              this.isLoading = false;
              alert('Error al registrar la canción: ' + (error.error?.message || error.message));
            }
          });
        },
        error: (error) => {
          console.error('Error al subir audio:', error);
          this.isLoading = false;
          alert('Error al subir el archivo de audio: ' + (error.error?.message || error.message));
        }
      });
    }
  }

  submitEvent() {
    if (this.eventForm.invalid) { 
      this.eventForm.markAllAsTouched(); 
      return; 
    }

    if (!this.artistId) {
      alert('Error: No se pudo identificar el artista');
      return;
    }
    
    if (this.editingEventId) {
      // Editar evento existente
      const payload = {
        artistId: this.artistId,
        title: this.eventForm.get('title')!.value,
        description: this.eventForm.get('description')!.value,
        dateEvent: Date.now(),
        status: 'active'
      };
      this.isLoading = true;
      this.apiService.put(`/event/${this.editingEventId}`, payload).subscribe({
        next: () => {
          console.log('Evento actualizado');
          const event = this.events.find(e => e.id === this.editingEventId);
          if (event) {
            event.title = payload.title;
            event.description = payload.description;
          }
          this.isLoading = false;
          this.closeEvent();
          this.loadEvents(); // Recargar eventos
        },
        error: (error) => {
          console.error('Error al actualizar evento:', error);
          alert('Error al actualizar el evento');
          this.isLoading = false;
        }
      });
    } else {
      // Crear nuevo evento
      const newId = this.events.length > 0 ? Math.max(...this.events.map(e => e.id)) + 1 : 1;
      
      const payload = {
        id: newId,
        artistId: this.artistId,
        title: this.eventForm.get('title')!.value,
        description: this.eventForm.get('description')!.value,
        dateEvent: Date.now(),
        status: 'active'
      };
      
      console.log('Publicar evento:', payload);
      this.isLoading = true;
      
      this.apiService.post('/event', payload).subscribe({
        next: (response) => {
          console.log('Evento creado exitosamente:', response);
          this.isLoading = false;
          this.closeEvent();
          this.loadEvents(); // Recargar eventos
          alert('Evento publicado exitosamente');
        },
        error: (error) => {
          console.error('Error al crear evento:', error);
          alert('Error al publicar el evento: ' + (error.error?.message || error.message));
          this.isLoading = false;
        }
      });
    }
  }

  editSong(songId: number) {
    const song = this.songs.find(s => s.id === songId);
    if (song) {
      this.editingSongId = songId;
      this.songForm.patchValue({ title: song.title });
      this.showSongModal = true;
    }
  }

  editEvent(eventId: number) {
    const event = this.events.find(e => e.id === eventId);
    if (event) {
      this.editingEventId = eventId;
      this.eventForm.patchValue({ 
        title: event.title,
        description: event.description
      });
      this.showEventModal = true;
    }
  }

  deleteSong(songId: number) {
    if (confirm('¿Estás seguro que quieres eliminar esta canción?')) {
      this.isLoading = true;
      this.apiService.delete(`/songs/${songId}/`).subscribe({
        next: () => {
          console.log('Canción eliminada');
          this.songs = this.songs.filter(s => s.id !== songId);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error al eliminar canción:', error);
          alert('Error al eliminar la canción');
          this.isLoading = false;
        }
      });
    }
  }

  deleteEvent(eventId: number) {
    if (confirm('¿Estás seguro que quieres eliminar este evento?')) {
      this.isLoading = true;
      this.apiService.delete(`/event/${eventId}`).subscribe({
        next: () => {
          console.log('Evento eliminado');
          this.events = this.events.filter(e => e.id !== eventId);
          this.isLoading = false;
          alert('Evento eliminado exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar evento:', error);
          alert('Error al eliminar el evento: ' + (error.error?.message || error.message));
          this.isLoading = false;
        }
      });
    }
  }

  triggerFileInput() {
    const fileInput = document.getElementById('profileImageInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.click();
    }
  }

  onProfileImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      // Validar que sea una imagen
      if (!file.type.startsWith('image/')) {
        alert('Por favor selecciona un archivo de imagen válido');
        return;
      }
      
      // Validar tamaño (máximo 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('La imagen no debe superar los 5MB');
        return;
      }
      
      this.uploadProfileImage(file);
    }
  }

  uploadProfileImage(file: File) {
    if (!this.artistId) {
      console.error('No artist ID available');
      return;
    }

    this.isUploadingImage = true;
    const formData = new FormData();
    formData.append('file', file);

    this.apiService.post<{ imageUrl: string }>('/upload/image', formData).subscribe({
      next: (response) => {
        console.log('Image uploaded successfully:', response);
        this.profileImageUrl = `http://localhost:8081${response.imageUrl}`;
        
        // Actualizar la URL de la foto en el perfil del artista
        this.updateArtistPhotoUrl(response.imageUrl);
        
        this.isUploadingImage = false;
      },
      error: (error) => {
        console.error('Error uploading image:', error);
        alert('Error al subir la imagen. Por favor intenta de nuevo.');
        this.isUploadingImage = false;
      }
    });
  }

  updateArtistPhotoUrl(photoUrl: string) {
    if (!this.artistId) return;

    const updatePayload = {
      id: this.artistId,
      nombre: this.form.value.name,
      correo: this.form.value.email,
      contrasena: this.currentPassword, // Usar la contraseña actual
      rol: 'artist',
      fotoUrl: photoUrl,
      contactNum: this.form.value.phone || '',
      description: this.form.value.description
    };

    this.apiService.put(`/artist/${this.artistId}/`, updatePayload).subscribe({
      next: () => {
        console.log('Artist photo URL updated successfully');
      },
      error: (error) => {
        console.error('Error updating artist photo URL:', error);
      }
    });
  }

  togglePlaySong(song: any) {
    const audioUrl = `http://localhost:8081${song.url}`;
    
    // Si no hay reproductor o es una canción diferente
    if (!this.audioPlayer || this.currentPlayingSong?.id !== song.id) {
      // Pausar canción anterior si existe
      if (this.audioPlayer) {
        this.audioPlayer.pause();
      }
      
      // Crear nuevo reproductor
      this.audioPlayer = new Audio(audioUrl);
      this.currentPlayingSong = song;
      
      // Reproducir
      this.audioPlayer.play().catch(error => {
        console.error('Error al reproducir audio:', error);
        alert('Error al reproducir la canción');
      });
      
      // Limpiar cuando termine
      this.audioPlayer.onended = () => {
        this.currentPlayingSong = null;
      };
    } else {
      // Misma canción: pausar o reanudar
      if (this.audioPlayer.paused) {
        this.audioPlayer.play().catch(error => {
          console.error('Error al reproducir audio:', error);
        });
      } else {
        this.audioPlayer.pause();
      }
    }
  }

  deleteAccount() {
    if (!this.artistId) {
      alert('Error: No se pudo identificar el artista');
      return;
    }

    const confirmation = confirm(
      '¿Estás seguro de que quieres eliminar tu cuenta?\n\n' +
      'Esta acción NO se puede deshacer y eliminará:\n' +
      '• Tu perfil de artista\n' +
      '• Todas tus canciones\n' +
      '• Todos tus eventos\n' +
      '• Todas tus redes sociales\n' +
      '• Todos tus seguidores\n\n' +
      'Escribe "ELIMINAR" en mayúsculas para confirmar:'
    );

    if (!confirmation) {
      return;
    }

    const finalConfirmation = prompt(
      'Esta es tu última oportunidad.\n\n' +
      'Escribe "ELIMINAR" (en mayúsculas) para confirmar la eliminación definitiva de tu cuenta:'
    );

    if (finalConfirmation !== 'ELIMINAR') {
      alert('Eliminación cancelada. Tu cuenta está segura.');
      return;
    }

    this.isLoading = true;
    console.log('Eliminando cuenta del artista ID:', this.artistId);

    this.apiService.delete(`/artist/${this.artistId}`).subscribe({
      next: () => {
        console.log('Cuenta eliminada exitosamente');
        this.isLoading = false;
        alert('Tu cuenta ha sido eliminada exitosamente.');
        
        // Limpiar localStorage y redirigir al inicio
        localStorage.removeItem('currentUser');
        localStorage.removeItem('authToken');
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error al eliminar la cuenta:', error);
        this.isLoading = false;
        alert('Error al eliminar la cuenta: ' + (error.error?.message || error.message));
      }
    });
  }
}
