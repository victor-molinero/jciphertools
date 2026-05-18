import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { of, Subject, throwError } from 'rxjs';
import { EncryptionService } from '../encryption-service';
import { EncryptTextForm } from './encrypt-text-form';

type EncryptionServiceSpy = jasmine.SpyObj<Pick<EncryptionService, 'encrypt' | 'decrypt'>>;

describe('EncryptTextForm', () => {
  let fixture: ComponentFixture<EncryptTextForm>;
  let component: EncryptTextForm;
  let cipher: EncryptionServiceSpy;

  beforeEach(async () => {
    cipher = jasmine.createSpyObj<Pick<EncryptionService, 'encrypt' | 'decrypt'>>(
      'EncryptionService',
      ['encrypt', 'decrypt'],
    );
    cipher.encrypt.and.returnValue(of(''));
    cipher.decrypt.and.returnValue(of(''));

    await TestBed.configureTestingModule({
      imports: [EncryptTextForm],
      providers: [provideZonelessChangeDetection(), { provide: EncryptionService, useValue: cipher }],
    }).compileComponents();

    fixture = TestBed.createComponent(EncryptTextForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('initializes form and local state', () => {
    expect(component.form.getRawValue()).toEqual({ input: '', algorithm: 'AES_CBC_256' });
    expect(component.output()).toBe('');
    expect(component.errorMessage()).toBe('');
    expect(component.loading()).toBeFalse();
  });

  it('does not encrypt when form is invalid', () => {
    component.form.controls.input.setValue('');

    component.encrypt();

    expect(cipher.encrypt).not.toHaveBeenCalled();
  });

  it('does not decrypt when form is invalid', () => {
    component.form.controls.input.setValue('');

    component.decrypt();

    expect(cipher.decrypt).not.toHaveBeenCalled();
  });

  it('encrypts and updates state on success', () => {
    const response$ = new Subject<string>();
    cipher.encrypt.and.returnValue(response$.asObservable());
    component.form.setValue({ input: 'hello', algorithm: 'AES_CBC_256' });

    component.encrypt();

    expect(cipher.encrypt).toHaveBeenCalledWith('hello', 'AES_CBC_256');
    expect(component.loading()).toBeTrue();

    response$.next('ciphertext');
    response$.complete();

    expect(component.output()).toBe('ciphertext');
    expect(component.errorMessage()).toBe('');
    expect(component.loading()).toBeFalse();
  });

  it('decrypts and updates state on success', () => {
    cipher.decrypt.and.returnValue(of('plaintext'));
    component.form.setValue({ input: 'ciphertext', algorithm: 'RSA_OAEP' });

    component.decrypt();

    expect(cipher.decrypt).toHaveBeenCalledWith('ciphertext', 'RSA_OAEP');
    expect(component.output()).toBe('plaintext');
    expect(component.errorMessage()).toBe('');
    expect(component.loading()).toBeFalse();
  });

  it('surfaces backend error message when available', () => {
    cipher.encrypt.and.returnValue(throwError(() => ({ error: { message: 'Invalid payload' } })));
    component.form.setValue({ input: 'hello', algorithm: 'AES_GCM_256' });

    component.encrypt();

    expect(component.errorMessage()).toBe('Invalid payload');
    expect(component.output()).toBe('');
    expect(component.loading()).toBeFalse();
  });

  it('uses fallback error message for unknown errors', () => {
    cipher.encrypt.and.returnValue(throwError(() => new Error('boom')));
    component.form.setValue({ input: 'hello', algorithm: 'AES_GCM_256' });

    component.encrypt();

    expect(component.errorMessage()).toBe('An unexpected error occurred.');
    expect(component.loading()).toBeFalse();
  });

  it('disables action buttons while form is invalid', () => {
    component.form.setValue({ input: '', algorithm: 'AES_CBC_256' });
    fixture.detectChanges();

    const [encryptButton, decryptButton] = getActionButtons(fixture);
    expect(encryptButton.disabled).toBeTrue();
    expect(decryptButton.disabled).toBeTrue();
  });

  it('shows processing label and disables actions while loading', () => {
    component.form.setValue({ input: 'hello', algorithm: 'AES_CBC_256' });
    component.loading.set(true);
    fixture.detectChanges();

    const [encryptButton, decryptButton] = getActionButtons(fixture);
    expect(encryptButton.disabled).toBeTrue();
    expect(decryptButton.disabled).toBeTrue();
    expect(encryptButton.textContent).toContain('Processing');
    expect(decryptButton.textContent).toContain('Processing');
  });

  it('renders output and error message sections from signals', () => {
    component.output.set('encrypted-value');
    component.errorMessage.set('Something went wrong');
    fixture.detectChanges();

    const textareas = fixture.nativeElement.querySelectorAll('textarea');
    const resultTextarea = textareas.item(1) as HTMLTextAreaElement;
    const errorBox = fixture.nativeElement.querySelector('.error-message') as HTMLDivElement;

    expect(resultTextarea.value).toBe('encrypted-value');
    expect(errorBox.textContent).toContain('Something went wrong');
  });
});

function getActionButtons(
  fixture: ComponentFixture<EncryptTextForm>,
): [HTMLButtonElement, HTMLButtonElement] {
  const buttons = fixture.nativeElement.querySelectorAll('button');
  return [buttons.item(0) as HTMLButtonElement, buttons.item(1) as HTMLButtonElement];
}