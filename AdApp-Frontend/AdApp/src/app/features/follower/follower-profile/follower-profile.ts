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
  private readonly API_URL = 'http://34.230.214.149:8081';
  
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
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
      console.error('No current user found');
      return;
    }

    const user = JSON.parse(currentUser);
    const followerId = user.id;

    console.log('Loading followed artists for follower:', followerId);
    
    this.http.get<Artist[]>(`${this.API_URL}/follower/${followerId}/following`).subscribe({
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

  getArtistPhotoUrl(artist: Artist): string {
    return artist.fotoUrl ? `${this.API_URL}${artist.fotoUrl}` : '/media/icons/perfil.png';
  }
}