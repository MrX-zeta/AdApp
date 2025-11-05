import { Component, OnInit } from '@angular/core';

interface Artist {
  id: string;
  stageName: string;
  fullName: string;
  followers: number;
  description: string;
  avatarUrl: string;
}

@Component({
  selector: 'app-dashboard-page',
  standalone: false,
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.css',
})
export class DashboardPage implements OnInit {
  // Featured artists for main section
  featuredArtists: Artist[] = [];
  
  // Recommended artists for sidebar
  recommendedArtists: Artist[] = [];
  
  // Loading state
  isLoading = false;

  constructor() {}

  ngOnInit(): void {
    this.loadFeaturedArtists();
    this.loadRecommendedArtists();
  }

  loadFeaturedArtists() {
    // TODO: Load featured artists from service
    console.log('Loading featured artists');
    this.isLoading = true;
    
    // Example data - replace with actual API call
    this.featuredArtists = [
      {
        id: '1',
        stageName: 'Mac Miller',
        fullName: 'Malcolm James McCormick',
        followers: 1000,
        description: 'Músico y cantante en solitario. Hip-Hop y R&B. Exploro el jazz.',
        avatarUrl: '/assets/avatars/mac_miller.jpg'
      },
      {
        id: '2',
        stageName: 'Sarah J',
        fullName: 'Sarah Johnson',
        followers: 2500,
        description: 'Cantante de jazz y soul. Me inspiro en los clásicos.',
        avatarUrl: '/assets/avatars/sarah_j.jpg'
      },
      {
        id: '3',
        stageName: 'DJ Dave',
        fullName: 'David Martinez',
        followers: 5000,
        description: 'Productor de música electrónica. House, techno y trance.',
        avatarUrl: '/assets/avatars/dj_dave.jpg'
      }
    ];
    
    this.isLoading = false;
  }

  loadRecommendedArtists() {
    // TODO: Load recommended artists from service
    console.log('Loading recommended artists');
    
    // Example data - could be same or different artists
    this.recommendedArtists = [
      {
        id: '1',
        stageName: 'Mac Miller',
        fullName: 'Malcolm James McCormick',
        followers: 1000,
        description: 'Músico y cantante en solitario. Hip-Hop y R&B. Exploro el jazz.',
        avatarUrl: '/assets/avatars/mac_miller.jpg'
      },
      {
        id: '2',
        stageName: 'Sarah J',
        fullName: 'Sarah Johnson',
        followers: 2500,
        description: 'Cantante de jazz y soul. Me inspiro en los clásicos.',
        avatarUrl: '/assets/avatars/sarah_j.jpg'
      },
      {
        id: '4',
        stageName: 'DJ Dave',
        fullName: 'David Martinez',
        followers: 5000,
        description: 'Productor de música electrónica. House, techno y trance.',
        avatarUrl: '/assets/avatars/dj_dave.jpg'
      }
    ];
  }

  viewArtistProfile(artist: Artist) {
    console.log('Navigating to artist profile:', artist.stageName);
    // TODO: Navigate to artist profile route
    // Example: this.router.navigate(['/artist', artist.id]);
  }

  followArtist(artist: Artist) {
    console.log('Following artist:', artist.stageName);
    // TODO: Call follow service
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
}
