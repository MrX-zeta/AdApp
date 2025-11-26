import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  protected hide = true;
  errorMessage: string = '';
  isLoading: boolean = false;

  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  toggleVisibility() {
    this.hide = !this.hide;
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { email, password } = this.form.getRawValue();

    this.authService.login({ email, password }).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);
        // Siempre redirigir a dashboard después del login
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Error en login:', error);
        this.errorMessage = error.error?.message || 'Error al iniciar sesión. Verifica tus credenciales.';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
