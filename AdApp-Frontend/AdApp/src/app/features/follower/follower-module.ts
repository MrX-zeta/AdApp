import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FollowerRoutingModule } from './follower-routing-module';
import { FollowerProfileComponent } from './follower-profile/follower-profile';
import { FormsModule } from '@angular/forms';

import { FollowerProfileEditComponent } from './follower-profile-edit/follower-profile-edit'; 

@NgModule({
  declarations: [
    FollowerProfileComponent,
    FollowerProfileEditComponent
  ],
  imports: [
    CommonModule,
    FollowerRoutingModule,
    FormsModule
  ]
})
export class FollowerModule { }