import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Navbar } from './components/navBar/navbar';
import { Footer } from './components/footer/footer';



@NgModule({
  declarations: [
    Navbar,
    Footer
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [Navbar, Footer]
})
export class CoreModule { }
