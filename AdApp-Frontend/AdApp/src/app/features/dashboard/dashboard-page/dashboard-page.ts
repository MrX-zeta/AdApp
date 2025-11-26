import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

interface Artist {
  id: number;
  nombre: string;
  correo: string;
  contrasena: string;
  rol: string;
  fotoUrl: string;
  contactNum: string;
  description: string;
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

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFeaturedArtists();
    this.loadRecommendedArtists();
  }

  loadFeaturedArtists() {
    console.log('Loading featured artists');
    this.isLoading = true;
    
    this.http.get<Artist[]>(`${this.API_URL}/artists`).subscribe({
      next: (artists) => {
        this.featuredArtists = artists;
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
    console.log('Loading recommended artists');
    
    this.http.get<Artist[]>(`${this.API_URL}/artists`).subscribe({
      next: (artists) => {
        this.recommendedArtists = artists;
      },
      error: (error) => {
        console.error('Error loading recommended artists:', error);
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
}
