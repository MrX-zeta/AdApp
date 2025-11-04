import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'auth/login' },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth-module').then(m => m.AuthModule)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard-module').then(m => m.DashboardModule)
  },
  {
    path: 'artist',
    loadChildren: () => import('./features/artist/artist-module').then(m => m.ArtistModule)
  },
  {
    path: 'follower', // Este será el prefijo de la URL (e.g., /follower)
    loadChildren: () => import('./features/follower/follower-module').then(m => m.FollowerModule)
  },
  { path: '**', redirectTo: 'dashboard' }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
