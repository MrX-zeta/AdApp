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
        this.songs = songs.filter(s => s.artistId === artistId);
        console.log('Songs loaded:', this.songs);
      },
      error: (error) => {
        console.error('Error loading songs:', error);
      }
    });
  }

  loadEvents(artistId: number): void {
    this.apiService.get<any[]>('/events').subscribe({
      next: (events) => {
        this.events = events.filter(e => e.artistId === artistId);
        console.log('Events loaded:', this.events);
      },
      error: (error) => {
        console.error('Error loading events:', error);
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
    this.eventForm.reset();
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
    
    const payload = {
      id: this.artistId,
      nombre: formValue.name,
      correo: formValue.email,
      contrasena: '', // Keep existing password
      rol: 'artist',
      fotoUrl: '',
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
      // Editar canción existente
      if (this.songForm.invalid) { this.songForm.markAllAsTouched(); return; }
      const payload = { 
        title: this.songForm.get('title')!.value,
        artistId: this.artistId
      };
      this.isLoading = true;
      this.apiService.put(`/songs/${this.editingSongId}/`, payload).subscribe({
        next: () => {
          console.log('Canción actualizada');
          const song = this.songs.find(s => s.id === this.editingSongId);
          if (song) song.title = payload.title;
          this.isLoading = false;
          this.closeSong();
        },
        error: (error) => {
          console.error('Error al actualizar canción:', error);
          alert('Error al actualizar la canción');
          this.isLoading = false;
        }
      });
    } else {
      // Crear nueva canción
      if (this.songForm.invalid || !this.songFile) { this.songForm.markAllAsTouched(); return; }
      const payload = { 
        title: this.songForm.get('title')!.value, 
        file: this.songFile,
        artistId: this.artistId
      };
      console.log('Subir canción', payload);
      this.closeSong();
    }
  }

  submitEvent() {
    if (this.eventForm.invalid) { this.eventForm.markAllAsTouched(); return; }
    
    if (this.editingEventId) {
      // Editar evento existente
      const payload = {
        title: this.eventForm.get('title')!.value,
        description: this.eventForm.get('description')!.value,
        artistId: this.artistId
      };
      this.isLoading = true;
      this.apiService.put(`/events/${this.editingEventId}/`, payload).subscribe({
        next: () => {
          console.log('Evento actualizado');
          const event = this.events.find(e => e.id === this.editingEventId);
          if (event) {
            event.title = payload.title;
            event.description = payload.description;
          }
          this.isLoading = false;
          this.closeEvent();
        },
        error: (error) => {
          console.error('Error al actualizar evento:', error);
          alert('Error al actualizar el evento');
          this.isLoading = false;
        }
      });
    } else {
      // Crear nuevo evento
      const payload = {
        title: this.eventForm.get('title')!.value,
        description: this.eventForm.get('description')!.value,
        artistId: this.artistId
      };
      console.log('Publicar evento', payload);
      this.closeEvent();
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
      this.apiService.delete(`/events/${eventId}/`).subscribe({
        next: () => {
          console.log('Evento eliminado');
          this.events = this.events.filter(e => e.id !== eventId);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error al eliminar evento:', error);
          alert('Error al eliminar el evento');
          this.isLoading = false;
        }
      });
    }
  }
}
