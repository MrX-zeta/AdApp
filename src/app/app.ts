import { Component, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('AdApp');
  showShell = true;

  constructor(private router: Router) {
    this.showShell = !this.router.url.startsWith('/auth');
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => {
        this.showShell = !e.urlAfterRedirects.startsWith('/auth');
      });
  }
}
