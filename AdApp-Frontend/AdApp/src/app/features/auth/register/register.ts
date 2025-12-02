import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: false, 
  templateUrl: './register.html',
  styleUrl: './register.css', 
})
export class RegisterComponent implements OnInit { 

  protected hide = [true, true];
  errorMessage: string = '';
  isLoading: boolean = false;

  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    
    this.form = this.fb.nonNullable.group({
      username: ['', [Validators.required]], 
      email: ['', [Validators.required, Validators.email]],
      userType: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]], 
    });
  }
  
  
  toggleVisibility(index: number) {
    this.hide[index] = !this.hide[index];
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  
    const { username, email, userType, password, confirmPassword } = this.form.getRawValue();
    
    if (password !== confirmPassword) {
      this.errorMessage = "Las contraseñas no coinciden.";
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    
    this.authService.register({ username, email, userType, password }).subscribe({
      next: (response) => {
        console.log('Registro exitoso:', response);
        // Siempre redirigir a dashboard después del registro
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Error en registro:', error);
        this.errorMessage = error.error?.message || 'Error al registrar. Intenta nuevamente.';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}