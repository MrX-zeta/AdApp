import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar implements OnInit {
  currentUserId: number | null = null;
  currentUserRole: string | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Obtener el ID y rol del usuario actual
    this.authService.currentUser$.subscribe(user => {
      this.currentUserId = user?.id || null;
      this.currentUserRole = user?.rol || null;
    });
  }

  getProfileLink(): string {
    if (!this.currentUserId || !this.currentUserRole) {
      return '/auth/login';
    }

    // Redirigir según el rol del usuario
    if (this.currentUserRole.toLowerCase() === 'artist') {
      return `/artist/edit/${this.currentUserId}`;
    } else if (this.currentUserRole.toLowerCase() === 'follower') {
      return `/follower/profile`;
    }
    
    return '/dashboard';
  }
}
