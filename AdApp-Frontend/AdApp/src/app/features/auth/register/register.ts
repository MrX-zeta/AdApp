import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: false, 
  templateUrl: './register.html',
  styleUrl: './register.css', 
})
export class RegisterComponent implements OnInit { 

  protected hide = [true, true]; 

  form!: FormGroup;

  constructor(private fb: FormBuilder) { }

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
      console.error("Las contraseñas no coinciden.");
      return;
    }
    
    console.log('Registering new user with', { username, email, userType, password });
  }
}