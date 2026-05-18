import { provideHttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../environments/environment';
import { EncryptionService } from './encryption-service';
import { Algorithm } from './algorithms';

describe('EncryptionService', () => {
  let service: EncryptionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        EncryptionService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(EncryptionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call the encrypt API', () => {
    const dummyResponse = { result: 'encryptedText' };
    const algorithm = 'AES_CBC_256' as Algorithm;
    const input = 'plainText';

    service.encrypt(input, algorithm).subscribe((res) => {
      expect(res).toBe('encryptedText');
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/encrypt`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ input, algorithm });
    req.flush(dummyResponse);
  });

  it('should call the decrypt API', () => {
    const dummyResponse = { result: 'plainText' };
    const algorithm = 'AES_CBC_256' as Algorithm;
    const input = 'encryptedText';

    service.decrypt(input, algorithm).subscribe((res) => {
      expect(res).toBe('plainText');
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/decrypt`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ input, algorithm });
    req.flush(dummyResponse);
  });

  it('should propagate HTTP errors', () => {
    let capturedError: any = null;

    service.encrypt('plainText', 'AES_GCM_256').subscribe({
      next: () => fail('expected request to fail'),
      error: (error: HttpErrorResponse) => {
        capturedError = error;
      },
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/encrypt`);
    req.flush({ message: 'boom' }, { status: 500, statusText: 'Server Error' });

    expect(capturedError).not.toBeNull();
    expect(capturedError?.status).toBe(500);
  });
});
