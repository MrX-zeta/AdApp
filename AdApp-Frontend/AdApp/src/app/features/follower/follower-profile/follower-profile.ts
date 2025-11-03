import { Component, OnInit } from '@angular/core';

interface Artist {
  name: string;
  avatarUrl: string;
}

@Component({
  selector: 'app-follower-profile',
  templateUrl: './follower-profile.html', 
  styleUrl: './follower-profile.css', 
  standalone: false,
})
export class FollowerProfileComponent implements OnInit {

  
  follower = {
    name: 'Juan Pérez',
    followingCount: 12,
   
    avatarUrl: '/assets/avatars/juan_perez.jpg' 
  };

  
  artists: Artist[] = [
    { name: 'Mac Miller', avatarUrl: '/assets/avatars/mac_miller.jpg' },
    { name: 'Mac Miller', avatarUrl: '/assets/avatars/mac_miller.jpg' },
  ];

  constructor() { }

  ngOnInit(): void {
    
  }
}