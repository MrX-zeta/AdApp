import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

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
    ReactiveFormsModule,
    ArtistRoutingModule
  ]
})
export class ArtistModule { }
