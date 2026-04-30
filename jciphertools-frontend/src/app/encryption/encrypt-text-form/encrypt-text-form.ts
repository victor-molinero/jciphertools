
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Encrypt, Algorithm } from '../encrypt';

@Component({
  selector: 'app-encrypt-text-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './encrypt-text-form.html',
  styleUrl: './encrypt-text-form.css',
})
export class EncryptTextForm {

  form: FormGroup;
  output = '';
  errorMessage = '';
  loading = false;

  readonly algorithms: { value: Algorithm; label: string }[] = [
    { value: 'AES_CBC_256', label: 'AES-CBC-256' },
    { value: 'RSA_OAEP',    label: 'RSA-OAEP (SHA-256)' }
  ];

  constructor(private fb: FormBuilder, private cipher: Encrypt) {
    this.form = this.fb.group({
      input:     ['', [Validators.required]],
      algorithm: ['AES_CBC_256', [Validators.required]]
    });
  }

  get inputControl() { return this.form.get('input')!; }
  get algorithmControl() { return this.form.get('algorithm')!; }

  encrypt(): void {
    if (this.form.invalid) return;
    this.execute('encrypt');
  }

  decrypt(): void {
    if (this.form.invalid) return;
    this.execute('decrypt');
  }

  private execute(operation: 'encrypt' | 'decrypt'): void {
    this.loading = true;
    this.output = '';
    this.errorMessage = '';

    const { input, algorithm } = this.form.value;
    const call$ = operation === 'encrypt'
      ? this.cipher.encrypt(input, algorithm)
      : this.cipher.decrypt(input, algorithm);

    call$.subscribe({
      next: result => {
        this.output = result;
        this.loading = false;
      },
      error: err => {
        this.errorMessage = err?.error?.message ?? 'An unexpected error occurred.';
        this.loading = false;
      }
    });
  }
}