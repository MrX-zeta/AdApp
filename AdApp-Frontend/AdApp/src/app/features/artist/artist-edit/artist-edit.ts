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
  songForm!: FormGroup;
  eventForm!: FormGroup;
  showSongModal = false;
  showEventModal = false;
  songFile?: File;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.maxLength(60)]],
      description: ['', [Validators.required, Validators.maxLength(240)]],
      instagram: [''],
      facebook: [''],
      phone: [''],
      email: ['', [Validators.email]],
    });

    this.songForm = this.fb.nonNullable.group({
      title: ['', [Validators.required, Validators.maxLength(80)]],
      audio: [null, Validators.required],
    });

    this.eventForm = this.fb.nonNullable.group({
      title: ['', [Validators.required, Validators.maxLength(80)]],
      description: ['', [Validators.required, Validators.maxLength(240)]],
    });
  }

  addSong() {
    this.showSongModal = true;
  }

  publishEvent() {
    this.showEventModal = true;
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

  // Modal helpers
  closeSong() { this.showSongModal = false; this.songForm.reset(); this.songFile = undefined; }
  closeEvent() { this.showEventModal = false; this.eventForm.reset(); }

  onSongFileSelected(evt: Event) {
    const input = evt.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.songFile = input.files[0];
      this.songForm.patchValue({ audio: this.songFile.name });
    }
  }

  submitSong() {
    if (this.songForm.invalid || !this.songFile) { this.songForm.markAllAsTouched(); return; }
    const payload = { title: this.songForm.get('title')!.value, file: this.songFile };
    console.log('Subir canción', payload);
    this.closeSong();
  }

  submitEvent() {
    if (this.eventForm.invalid) { this.eventForm.markAllAsTouched(); return; }
  const payload = this.eventForm.getRawValue(); // título + descripción
    console.log('Publicar evento', payload);
    this.closeEvent();
  }
}
