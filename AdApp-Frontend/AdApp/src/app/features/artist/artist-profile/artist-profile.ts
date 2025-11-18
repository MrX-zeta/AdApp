import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';

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

@Component({
  selector: 'app-artist-profile',
  standalone: false,
  templateUrl: './artist-profile.html',
  styleUrl: './artist-profile.css',
})
export class ArtistProfile implements OnInit {
  // Artist profile data
  artist = {
    name: '',
    description: '',
    instagram: '',
    facebook: '',
    phone: '',
    email: '',
    profileImage: '',
    coverImage: '',
    followers: 1000
  };

  // Lists for songs and events
  songs: any[] = [];
  events: any[] = [];
  isLoading = false;
  artistId?: number;

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Obtener el ID del usuario actual
    this.authService.currentUser$.subscribe(user => {
      if (user?.id) {
        this.artistId = user.id;
        this.loadArtistProfile();
        this.loadSongs();
        this.loadEvents();
      }
    });
  }

  loadArtistProfile() {
    if (!this.artistId) {
      console.error('No artist ID available');
      return;
    }

    console.log('Loading artist profile');
    this.isLoading = true;
    
    this.apiService.get<ArtistResponse>(`/artist/${this.artistId}/`).subscribe({
      next: (data) => {
        console.log('Artist profile loaded:', data);
        this.artist = {
          name: data.nombre,
          description: data.description || 'Sin descripción',
          instagram: data.instagram || '',
          facebook: data.facebook || '',
          phone: data.contactNum || '',
          email: data.correo,
          profileImage: data.fotoUrl || '/media/icons/perfil.png',
          coverImage: '',
          followers: 1000 // Por ahora estático
        };
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading artist profile:', error);
        this.isLoading = false;
        // Usar datos de ejemplo si falla
        this.artist = {
          name: 'Artist Name',
          description: 'Sin descripción',
          instagram: '',
          facebook: '',
          phone: '',
          email: '',
          profileImage: '/media/icons/perfil.png',
          coverImage: '',
          followers: 1000
        };
      }
    });
  }

  loadSongs() {
    // TODO: Load songs from service
    console.log('Loading songs');
    this.songs = [
      {
        type: 'song',
        title: 'Circles (demo)',
        description: 'Canción demo subida para escuchas tempranas.',
        date: new Date('2025-10-18')
      }
    ];
  }

  loadEvents() {
    // TODO: Load events from service
    console.log('Loading events');
    this.events = [
      {
        type: 'event',
        title: 'Concierto en Las Palmas',
        description: 'El día 18 de octubre de 2025 me presentaré en Las Palmas en un evento que realizamos entre muchos otros artistas. El evento comenzará a las 8 de la noche. Te esperamos.',
        date: new Date('2025-10-18')
      },
      {
        type: 'event',
        title: 'Concierto en Las Palmas',
        description: 'El día 18 de octubre de 2025 me presentaré en Las Palmas en un evento que realizamos entre muchos otros artistas. El evento comenzará a las 8 de la noche. Te esperamos.',
        date: new Date('2025-10-16')
      }
    ];
  }

  formatInstagram(): string {
    if (!this.artist.instagram) return '';
    // Si ya tiene @, devolverlo tal cual, si no, agregarlo
    return this.artist.instagram.startsWith('@') ? this.artist.instagram : `@${this.artist.instagram}`;
  }

  getInstagramLink(): string {
    if (!this.artist.instagram) return '#';
    const username = this.artist.instagram.replace('@', '').replace('instagram.com/', '').replace('https://', '').replace('http://', '');
    return `https://instagram.com/${username}`;
  }

  getFacebookLink(): string {
    if (!this.artist.facebook) return '#';
    if (this.artist.facebook.includes('facebook.com')) return this.artist.facebook;
    return `https://facebook.com/${this.artist.facebook}`;
  }

  playSong(song: any) {
    console.log('Playing song', song);
  }

  viewEvent(event: any) {
    console.log('Viewing event', event);
  }

  viewItem(item: any) {
    if (item.type === 'song') {
      this.playSong(item);
    } else if (item.type === 'event') {
      this.viewEvent(item);
    }
  }

  editProfile() {
    console.log('Navigate to edit profile');
    // Navegar a la página de edición con el ID del artista
    if (this.artistId) {
      window.location.href = `/artist/edit/${this.artistId}`;
    }
  }
}
