import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ArtistRoutingModule } from './artist-routing-module';
import { ArtistProfile } from './artist-profile/artist-profile';
import { ArtistEdit } from './artist-edit/artist-edit';


@NgModule({
  declarations: [
    ArtistProfile,
    ArtistEdit
  ],
  imports: [
    CommonModule,
    ArtistRoutingModule
  ]
})
export class ArtistModule { }
