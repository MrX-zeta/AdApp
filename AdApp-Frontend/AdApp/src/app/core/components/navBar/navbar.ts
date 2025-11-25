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

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Obtener el ID del usuario actual
    this.authService.currentUser$.subscribe(user => {
      this.currentUserId = user?.id || null;
    });
  }

  getProfileLink(): string {
    return this.currentUserId ? `/artist/edit/${this.currentUserId}` : '/artist/edit';
  }
}
