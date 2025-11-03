import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArtistCard } from './components/artist-card/artist-card';
import { EventCard } from './components/event-card/event-card';



@NgModule({
  declarations: [
    ArtistCard,
    EventCard
  ],
  imports: [
    CommonModule
  ]
})
export class SharedModule { }
