import { Component, OnInit } from '@angular/core';

interface Artist {
  name: string;
}

@Component({
  selector: 'app-follower-profile-edit',
  templateUrl: './follower-profile-edit.html',
  styleUrl: './follower-profile-edit.css',
  standalone: false,
})
export class FollowerProfileEditComponent implements OnInit {
  userName: string = 'Nombre Actual del Seguidor';
  followingCount: number = 15;
  artists: Artist[] = [
    { name: 'Artista 1' },
    { name: 'Artista 2' },
    { name: 'Artista 3' }
  ];

  constructor() { }

  ngOnInit(): void { }

  saveChanges(): void {
    console.log(`Guardando nuevo nombre: ${this.userName}`);
    alert(`Nombre actualizado a: ${this.userName}`);
  }
}