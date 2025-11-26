import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ArtistProfile } from './artist-profile/artist-profile';
import { ArtistEdit } from './artist-edit/artist-edit';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'profile' },
  { path: 'profile', component: ArtistProfile },
  { path: 'profile/:id', component: ArtistProfile },
  { path: 'edit', component: ArtistEdit },
  { path: 'edit/:id', component: ArtistEdit },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ArtistRoutingModule { }
