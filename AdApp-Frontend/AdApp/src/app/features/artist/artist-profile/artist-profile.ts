import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { SocialMedia, ParsedSocialMedia } from '../../../shared/models/social-media.model';

interface ArtistResponse {
  id: number;
  nombre: string;
  correo: string;
  contrasena: string;
  rol: string;
  fotoUrl: string;
  contactNum: string;
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
    followers: 0
  };

  // Lists for songs and events
  songs: any[] = [];
  events: any[] = [];
  isLoading = false;
  artistId?: number;
  socialMedias: ParsedSocialMedia[] = [];

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Intentar obtener el ID de la ruta primero
    this.route.params.subscribe(params => {
      const routeId = params['id'];
      
      if (routeId) {
        // Si hay ID en la ruta, usarlo (caso: follower viendo perfil de artista)
        this.artistId = parseInt(routeId, 10);
        console.log('Loading artist profile from route ID:', this.artistId);
        this.loadArtistProfile();
        this.loadSongs();
        this.loadEvents();
      } else {
        // Si no hay ID en la ruta, obtener el ID del usuario autenticado (caso: artista viendo su propio perfil)
        this.authService.currentUser$.subscribe(user => {
          if (user?.id) {
            this.artistId = user.id;
            console.log('Loading artist profile from current user ID:', this.artistId);
            this.loadArtistProfile();
            this.loadSongs();
            this.loadEvents();
          }
        });
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
          instagram: '', // Se cargará desde loadSocialMedias()
          facebook: '', // Se cargará desde loadSocialMedias()
          phone: data.contactNum || '',
          email: data.correo,
          profileImage: data.fotoUrl || '/media/icons/perfil.png',
          coverImage: '',
          followers: 0 // Inicia en 0, se incrementará con seguidores reales
        };
        this.isLoading = false;
        // Cargar redes sociales después de cargar el perfil
        this.loadSocialMedias();
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
          followers: 0
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
    if (!this.artistId) {
      console.error('No artist ID available to load events');
      return;
    }

    console.log('Loading events for artist:', this.artistId);
    
    this.apiService.get<any[]>('/event').subscribe({
      next: (events) => {
        // Filtrar solo los eventos del artista actual
        const artistEvents = events.filter(e => e.artistId === this.artistId);
        
        // Transformar los eventos al formato esperado por la vista
        this.events = artistEvents.map(event => ({
          id: event.id,
          type: 'event',
          title: event.title,
          description: event.description,
          date: new Date(event.eventDate), // Convertir ISO string a Date
          eventDate: event.eventDate,
          status: event.status
        }));
        
        console.log('Events loaded:', this.events);
      },
      error: (error) => {
        console.error('Error loading events:', error);
        this.events = [];
      }
    });
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

  loadSocialMedias() {
    if (!this.artistId) {
      console.error('No artist ID available');
      return;
    }

    console.log('Loading social medias for artist:', this.artistId);
    
    this.apiService.get<SocialMedia[]>(`/socialMedia/artist/${this.artistId}/`).subscribe({
      next: (data) => {
        console.log('Social medias loaded:', data);
        console.log('Number of social media entries:', data.length);
        
        this.socialMedias = data.map(sm => this.parseSocialMedia(sm));
        
        // Limpiar redes sociales previas
        this.artist.instagram = '';
        this.artist.facebook = '';
        
        // Actualizar el objeto artist con las redes sociales
        data.forEach(sm => {
          console.log('Processing social media:', sm);
          const url = sm.url.toLowerCase();
          if (url.startsWith('instagram:')) {
            this.artist.instagram = sm.url.replace(/^instagram:/i, '').trim();
            console.log('Instagram updated to:', this.artist.instagram);
          } else if (url.startsWith('facebook:')) {
            this.artist.facebook = sm.url.replace(/^facebook:/i, '').trim();
            console.log('Facebook updated to:', this.artist.facebook);
          }
        });
        
        console.log('Artist object updated with social media:', this.artist);
      },
      error: (error) => {
        console.error('Error loading social medias:', error);
        console.error('Error details:', error);
        this.socialMedias = [];
        this.artist.instagram = '';
        this.artist.facebook = '';
      }
    });
  }

  parseSocialMedia(socialMedia: SocialMedia): ParsedSocialMedia {
    const url = socialMedia.url.toLowerCase();
    
    // Detectar la plataforma y extraer el username
    if (url.includes('instagram:') || url.includes('instagram.com')) {
      const username = url.replace('instagram:', '').replace('https://', '').replace('http://', '').replace('instagram.com/', '').replace('@', '').trim();
      return {
        platform: 'instagram',
        username: username,
        fullUrl: `https://instagram.com/${username}`,
        displayName: `@${username}`
      };
    } else if (url.includes('facebook:') || url.includes('facebook.com')) {
      const username = url.replace('facebook:', '').replace('https://', '').replace('http://', '').replace('facebook.com/', '').trim();
      return {
        platform: 'facebook',
        username: username,
        fullUrl: url.includes('http') ? socialMedia.url : `https://facebook.com/${username}`,
        displayName: username
      };
    } else if (url.includes('twitter:') || url.includes('twitter.com') || url.includes('x.com')) {
      const username = url.replace('twitter:', '').replace('https://', '').replace('http://', '').replace('twitter.com/', '').replace('x.com/', '').replace('@', '').trim();
      return {
        platform: 'twitter',
        username: username,
        fullUrl: `https://x.com/${username}`,
        displayName: `@${username}`
      };
    } else if (url.includes('youtube:') || url.includes('youtube.com')) {
      const username = url.replace('youtube:', '').replace('https://', '').replace('http://', '').replace('youtube.com/', '').trim();
      return {
        platform: 'youtube',
        username: username,
        fullUrl: url.includes('http') ? socialMedia.url : `https://youtube.com/${username}`,
        displayName: username
      };
    } else if (url.includes('tiktok:') || url.includes('tiktok.com')) {
      const username = url.replace('tiktok:', '').replace('https://', '').replace('http://', '').replace('tiktok.com/', '').replace('@', '').trim();
      return {
        platform: 'tiktok',
        username: username,
        fullUrl: `https://tiktok.com/@${username}`,
        displayName: `@${username}`
      };
    }
    
    // Por defecto
    return {
      platform: 'other',
      username: socialMedia.url,
      fullUrl: socialMedia.url.includes('http') ? socialMedia.url : `https://${socialMedia.url}`,
      displayName: socialMedia.url
    };
  }

  getSocialMediaIcon(platform: string): string {
    const icons: { [key: string]: string } = {
      'instagram': '📷',
      'facebook': '👍',
      'twitter': '🐦',
      'youtube': '▶️',
      'tiktok': '🎵',
      'other': '🔗'
    };
    return icons[platform] || icons['other'];
  }
}
