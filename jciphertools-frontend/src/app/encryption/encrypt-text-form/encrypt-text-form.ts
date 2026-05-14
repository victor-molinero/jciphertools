import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Encrypt, Algorithm } from '../encrypt';

@Component({
  selector: 'app-encrypt-text-form',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './encrypt-text-form.html',
  styleUrl: './encrypt-text-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EncryptTextForm {
  private readonly fb = inject(FormBuilder);
  private readonly cipher = inject(Encrypt);

  readonly form = this.fb.nonNullable.group({
    input: ['', [Validators.required]],
    algorithm: ['AES_CBC_256' as Algorithm, [Validators.required]]
  });
  readonly output = signal('');
  readonly errorMessage = signal('');
  readonly loading = signal(false);

  readonly algorithms: { value: Algorithm; label: string }[] = [
    { value: 'AES_CBC_256', label: 'AES-CBC-256' },
    { value: 'RSA_OAEP',    label: 'RSA-OAEP (SHA-256)' }
  ];

  encrypt(): void {
    if (this.form.invalid) return;
    this.execute('encrypt');
  }

  decrypt(): void {
    if (this.form.invalid) return;
    this.execute('decrypt');
  }

  private execute(operation: 'encrypt' | 'decrypt'): void {
    this.loading.set(true);
    this.output.set('');
    this.errorMessage.set('');

    const { input, algorithm } = this.form.getRawValue();
    const call$ = operation === 'encrypt'
      ? this.cipher.encrypt(input, algorithm)
      : this.cipher.decrypt(input, algorithm);

    call$.subscribe({
      next: result => {
        this.output.set(result);
        this.loading.set(false);
      },
      error: (err: unknown) => {
        this.errorMessage.set(this.extractErrorMessage(err));
        this.loading.set(false);
      }
    });
  }

  private extractErrorMessage(err: unknown): string {
    if (typeof err === 'object' && err !== null && 'error' in err) {
      const nestedError = (err as { error?: { message?: string } }).error;
      if (nestedError?.message) {
        return nestedError.message;
      }
    }

    return 'An unexpected error occurred.';
  }
}