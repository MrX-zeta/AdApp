import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FollowerProfileComponent } from './follower-profile/follower-profile'; 
import { FollowerProfileEditComponent } from './follower-profile-edit/follower-profile-edit'; 

const routes: Routes = [
  
  { path: 'profile', component: FollowerProfileComponent }, 
  
  { path: 'edit', component: FollowerProfileEditComponent }, 

  { path: '', redirectTo: 'profile', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FollowerRoutingModule { }