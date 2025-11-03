import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from './components/navBar/navbar';
import { Footer } from './components/footer/footer';



@NgModule({
  declarations: [
    Navbar,
    Footer
  ],
  imports: [
    CommonModule
  ]
})
export class CoreModule { }
