import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-artist-edit',
  standalone: false,
  templateUrl: './artist-edit.html',
  styleUrl: './artist-edit.css',
})
export class ArtistEdit {
  form!: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.maxLength(60)]],
      description: ['', [Validators.required, Validators.maxLength(240)]],
      instagram: [''],
      facebook: [''],
      phone: [''],
      email: ['', [Validators.email]],
    });
  }

  addSong() {
    console.log('Agregar canción');
  }

  publishEvent() {
    console.log('Publicar evento');
  }

  save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    console.log('Guardar perfil', this.form.getRawValue());
  }

  cancel() {
    console.log('Cancelar edición');
  }
}
