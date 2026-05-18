import { provideZonelessChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { EncryptionService } from './encryption/encryption-service';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    const cipherStub = {
      encrypt: () => of(''),
      decrypt: () => of(''),
    };

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideZonelessChangeDetection(),
        { provide: EncryptionService, useValue: cipherStub },
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render encryption form shell', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-encrypt-text-form')).not.toBeNull();
  });
});
