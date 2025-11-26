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

interface Follower {
  id: number;
  nombre: string;
  correo: string;
  contrasena: string;
  rol: string;
}

@Component({
  selector: 'app-follower-profile',
  templateUrl: './follower-profile.html', 
  styleUrl: './follower-profile.css', 
  standalone: false,
})
export class FollowerProfileComponent implements OnInit {
  private readonly API_URL = 'http://localhost:8081';
  
  follower: Follower | null = null;
  followingCount: number = 0;
  artists: Artist[] = [];

  constructor(
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadFollowerProfile();
    this.loadFollowedArtists();
  }

  loadFollowerProfile(): void {
    // Obtener el usuario actual del localStorage
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
      console.error('No current user found');
      this.router.navigate(['/auth/login']);
      return;
    }

    const user = JSON.parse(currentUser);
    console.log('Loading follower profile for user:', user);

    // Cargar información del follower desde el endpoint
    this.http.get<Follower>(`${this.API_URL}/follower/${user.id}/`).subscribe({
      next: (follower) => {
        this.follower = follower;
        console.log('Follower profile loaded:', follower);
      },
      error: (error) => {
        console.error('Error loading follower profile:', error);
      }
    });
  }

  loadFollowedArtists(): void {
    // TODO: Implementar endpoint para obtener artistas seguidos por el follower
    // Por ahora, cargar todos los artistas disponibles
    this.http.get<Artist[]>(`${this.API_URL}/artists`).subscribe({
      next: (artists) => {
        this.artists = artists;
        this.followingCount = artists.length;
        console.log('Followed artists loaded:', artists);
      },
      error: (error) => {
        console.error('Error loading followed artists:', error);
        this.artists = [];
        this.followingCount = 0;
      }
    });
  }

  viewArtistProfile(artist: Artist): void {
    console.log('Navigating to artist profile:', artist.nombre);
    this.router.navigate(['/artist/profile', artist.id]);
  }
}