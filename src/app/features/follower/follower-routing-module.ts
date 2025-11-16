import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FollowerProfileComponent } from './follower-profile/follower-profile';

const routes: Routes = [
  
  { path: 'profile', component: FollowerProfileComponent }, 
  
  
  { path: '', redirectTo: 'profile', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FollowerRoutingModule { }