import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface Artist {
  id: number;
  nombre: string;
  correo: string;
  contrasena: string;
  rol: string;
  fotoUrl: string;
  contactNum: string;
  description: string;
  followersCount?: number;
}

@Component({
  selector: 'app-dashboard-page',
  standalone: false,
  templateUrl: './dashboard-clean.html',
  styleUrl: './dashboard-page.css',
})
export class DashboardPage implements OnInit {
  private readonly API_URL = 'http://localhost:8081';
  
  // Featured artists for main section
  featuredArtists: Artist[] = [];
  
  // Recommended artists for sidebar
  recommendedArtists: Artist[] = [];
  
  // Loading state
  isLoading = false;
  
  // Current user role
  currentUserRole?: string;
  currentUserId?: number;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Obtener el rol y el ID del usuario actual
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUserRole = user.rol;
        this.currentUserId = user.id;
        console.log('Current user role:', this.currentUserRole, 'ID:', this.currentUserId);
        
        // Cargar artistas seguidos después de obtener el usuario
        if (this.isFollower()) {
          this.loadRecommendedArtists();
        }
      }
    });
    
    this.loadFeaturedArtists();

    // Escuchar eventos de actualización de seguimiento
    window.addEventListener('followStatusChanged', () => {
      console.log('Follow status changed, reloading followed artists');
      if (this.isFollower() && this.currentUserId) {
        this.loadRecommendedArtists();
      }
    });
  }

  loadFeaturedArtists() {
    console.log('Loading featured artists');
    this.isLoading = true;
    
    this.http.get<Artist[]>(`${this.API_URL}/artists`).subscribe({
      next: (artists) => {
        this.featuredArtists = artists;
        
        // Cargar el contador de seguidores para cada artista
        this.featuredArtists.forEach(artist => {
          this.loadFollowersCount(artist);
        });
        
        this.isLoading = false;
        
        // Mostrar mensaje si no hay artistas
        if (artists.length === 0) {
          console.log('No hay artistas disponibles en este momento');
        } else {
          console.log(`Se cargaron ${artists.length} artistas`);
        }
      },
      error: (error) => {
        console.error('Error loading featured artists:', error);
        this.featuredArtists = [];
        this.isLoading = false;
      }
    });
  }

  loadRecommendedArtists() {
    if (!this.currentUserId) {
      console.log('No user ID available to load followed artists');
      this.recommendedArtists = [];
      return;
    }

    console.log('Loading followed artists for user:', this.currentUserId);
    
    // El endpoint /follower/{id}/following ya devuelve los artistas completos
    this.http.get<Artist[]>(`${this.API_URL}/follower/${this.currentUserId}/following`).subscribe({
      next: (followedArtists) => {
        console.log('Followed artists received:', followedArtists);
        
        if (followedArtists && followedArtists.length > 0) {
          this.recommendedArtists = followedArtists;
          
          // Cargar el contador de seguidores para cada artista
          this.recommendedArtists.forEach(artist => {
            this.loadFollowersCount(artist);
          });
          
          console.log('Recommended artists loaded:', this.recommendedArtists.length);
        } else {
          // Si no sigue a nadie, la lista queda vacía
          console.log('User is not following any artists yet');
          this.recommendedArtists = [];
        }
      },
      error: (error) => {
        console.error('Error loading followed artists:', error);
        console.error('Error details:', error.message);
        this.recommendedArtists = [];
      }
    });
  }

  viewArtistProfile(artist: Artist) {
    console.log('Navigating to artist profile:', artist.nombre);
    this.router.navigate(['/artist/profile', artist.id]);
  }

  followArtist(artist: Artist) {
    console.log('Following artist:', artist.nombre);
    // TODO: Call follow service to create follower relationship
  }

  formatFollowers(count: number): string {
    if (count >= 1000) {
      return `${(count / 1000).toFixed(1).replace('.0', '')}k`;
    }
    return count.toString();
  }

  getFollowersText(count: number): string {
    return `${count.toLocaleString('es-ES')} seguidores`;
  }

  getArtistName(artist: Artist): string {
    return artist.nombre;
  }

  getArtistDescription(artist: Artist): string {
    return artist.description || 'Sin descripción';
  }

  loadFollowersCount(artist: Artist): void {
    this.http.get<{ followersCount: number }>(`${this.API_URL}/artist/${artist.id}/followersCount`).subscribe({
      next: (data) => {
        artist.followersCount = data.followersCount;
      },
      error: (error) => {
        console.error(`Error loading followers count for artist ${artist.id}:`, error);
        artist.followersCount = 0;
      }
    });
  }

  getFollowersCountText(artist: Artist): string {
    const count = artist.followersCount ?? 0;
    return count === 1 ? '1 seguidor' : `${count} seguidores`;
  }

  isFollower(): boolean {
    return this.currentUserRole === 'follower';
  }

  getArtistPhotoUrl(artist: Artist): string {
    if (artist.fotoUrl && artist.fotoUrl.trim() !== '') {
      return `${this.API_URL}${artist.fotoUrl}`;
    }
    return '/media/icons/perfil.png';
  }
}
