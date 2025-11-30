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

  songs: any[] = [];
  events: any[] = [];
  isLoading = false;
  artistId?: number;
  socialMedias: ParsedSocialMedia[] = [];
  isFollowing = false;
  currentUserId?: number;
  currentUserRole?: string;
  audioPlayer: HTMLAudioElement | null = null;
  currentPlayingSong: any = null;

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Obtener información del usuario actual
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUserId = user.id;
        this.currentUserRole = user.rol;
      }
    });

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
        
        // Construir URL completa de la foto si existe
        const photoUrl = data.fotoUrl 
          ? `http://localhost:8081${data.fotoUrl}` 
          : '/media/icons/perfil.png';
        
        this.artist = {
          name: data.nombre,
          description: data.description || 'Sin descripción',
          instagram: '', // Se cargará desde loadSocialMedias()
          facebook: '', // Se cargará desde loadSocialMedias()
          phone: data.contactNum || '',
          email: data.correo,
          profileImage: photoUrl,
          coverImage: '',
          followers: 0 // Se cargará desde loadFollowersCount()
        };
        this.isLoading = false;
        // Cargar redes sociales y cantidad de seguidores después de cargar el perfil
        this.loadSocialMedias();
        this.loadFollowersCount();
        this.checkIfFollowing();
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
    if (!this.artistId) {
      console.error('No artist ID available to load songs');
      return;
    }

    console.log('Loading songs for artist:', this.artistId);
    
    this.apiService.get<any[]>('/songs').subscribe({
      next: (songs) => {
        // Filtrar solo las canciones del artista actual
        const artistSongs = songs.filter(s => s.artistId === this.artistId);
        
        // Transformar las canciones al formato esperado por la vista
        this.songs = artistSongs.map(song => ({
          id: song.id,
          type: 'song',
          title: song.title,
          url: song.url,
          dateUploaded: song.dateUploaded || Date.now(),
          date: new Date(song.dateUploaded || Date.now()), // Para compatibilidad
          status: song.status
        }));
        
        console.log('Songs loaded:', this.songs);
      },
      error: (error) => {
        console.error('Error loading songs:', error);
        this.songs = [];
      }
    });
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

  loadFollowersCount(): void {
    if (!this.artistId) {
      console.error('No artist ID available');
      return;
    }

    this.apiService.get<{ followersCount: number }>(`/artist/${this.artistId}/followersCount`).subscribe({
      next: (data) => {
        this.artist.followers = data.followersCount;
        console.log('Followers count loaded:', data.followersCount);
      },
      error: (error) => {
        console.error('Error loading followers count:', error);
        this.artist.followers = 0;
      }
    });
  }

  checkIfFollowing(): void {
    if (!this.currentUserId || !this.artistId || this.currentUserRole !== 'follower') {
      this.isFollowing = false;
      return;
    }

    this.apiService.get<{ isFollowing: boolean }>(`/follower/${this.currentUserId}/isFollowing/${this.artistId}`).subscribe({
      next: (data) => {
        this.isFollowing = data.isFollowing;
        console.log('Is following:', data.isFollowing);
      },
      error: (error) => {
        console.error('Error checking if following:', error);
        this.isFollowing = false;
      }
    });
  }

  toggleFollow(): void {
    if (!this.currentUserId || !this.artistId || this.currentUserRole !== 'follower') {
      console.error('Cannot follow: missing user info or not a follower');
      return;
    }

    if (this.isFollowing) {
      // Dejar de seguir
      this.apiService.delete(`/follower/${this.currentUserId}/follow/${this.artistId}`).subscribe({
        next: () => {
          this.isFollowing = false;
          this.artist.followers--;
          console.log('Unfollowed artist');
          // Disparar evento personalizado para notificar el cambio
          window.dispatchEvent(new CustomEvent('followStatusChanged'));
        },
        error: (error) => {
          console.error('Error unfollowing artist:', error);
        }
      });
    } else {
      // Seguir
      this.apiService.post(`/follower/${this.currentUserId}/follow/${this.artistId}`, {}).subscribe({
        next: () => {
          this.isFollowing = true;
          this.artist.followers++;
          console.log('Followed artist');
          // Disparar evento personalizado para notificar el cambio
          window.dispatchEvent(new CustomEvent('followStatusChanged'));
        },
        error: (error) => {
          console.error('Error following artist:', error);
        }
      });
    }
  }

  canFollow(): boolean {
    return this.currentUserRole === 'follower' && this.currentUserId !== this.artistId;
  }

  togglePlaySong(song: any) {
    const audioUrl = `http://localhost:8081${song.url}`;
    
    // Si no hay reproductor o es una canción diferente
    if (!this.audioPlayer || this.currentPlayingSong?.id !== song.id) {
      // Pausar y limpiar canción anterior si existe
      if (this.audioPlayer) {
        this.audioPlayer.pause();
        this.audioPlayer.currentTime = 0;
        this.audioPlayer = null;
      }
      
      // Esperar un momento antes de crear el nuevo reproductor
      setTimeout(() => {
        // Crear nuevo reproductor
        this.audioPlayer = new Audio(audioUrl);
        this.currentPlayingSong = song;
        
        // Configurar evento de fin antes de reproducir
        this.audioPlayer.onended = () => {
          this.currentPlayingSong = null;
          this.audioPlayer = null;
        };
        
        // Configurar evento de error
        this.audioPlayer.onerror = (error) => {
          console.error('Error al cargar audio:', error);
          alert('No se pudo cargar la canción');
          this.currentPlayingSong = null;
          this.audioPlayer = null;
        };
        
        // Reproducir cuando esté listo
        this.audioPlayer.play().catch(error => {
          console.error('Error al reproducir audio:', error);
          alert('Error al reproducir la canción. Verifica que el archivo existe.');
          this.currentPlayingSong = null;
          this.audioPlayer = null;
        });
      }, 100);
    } else {
      // Misma canción: pausar o reanudar
      if (this.audioPlayer.paused) {
        this.audioPlayer.play().catch(error => {
          console.error('Error al reproducir audio:', error);
          alert('Error al reanudar la reproducción');
        });
      } else {
        this.audioPlayer.pause();
      }
    }
  }
}
