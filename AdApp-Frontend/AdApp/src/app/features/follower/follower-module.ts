import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FollowerRoutingModule } from './follower-routing-module';
import { FollowerProfileComponent } from './follower-profile/follower-profile'; 


@NgModule({
  declarations: [
    FollowerProfileComponent 
  ],
  imports: [
    CommonModule,
    FollowerRoutingModule 
  ]
})
export class FollowerModule { }