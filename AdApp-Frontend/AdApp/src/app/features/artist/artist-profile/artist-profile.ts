import { Component, OnInit } from '@angular/core';
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
    followers: 1000
  };

  // Lists for songs and events
  songs: any[] = [];
  events: any[] = [];
  isLoading = false;
  artistId?: number;
  socialMedias: ParsedSocialMedia[] = [];

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
        this.loadSocialMedias();
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
