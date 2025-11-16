import { Component, OnInit } from '@angular/core';

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
    coverImage: ''
  };

  // Lists for songs and events
  songs: any[] = [];
  events: any[] = [];

  constructor() {}

  ngOnInit(): void {
    this.loadArtistProfile();
    this.loadSongs();
    this.loadEvents();
  }

  loadArtistProfile() {
    // TODO: Load artist profile data from service
    console.log('Loading artist profile');
    // Example data
    this.artist = {
      name: 'Mac Miller',
      description: 'Músico y cantante en solitario. Hip hop y R&B, exploro el jazz.',
      instagram: '@mac_off',
      facebook: 'Mac Miller',
      phone: '+123-456-0789',
      email: 'macmiller@gmail.com',
      profileImage: '/media/icons/perfil.png',
      coverImage: ''
    };
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
    // TODO: Navigate to artist-edit route
  }
}
