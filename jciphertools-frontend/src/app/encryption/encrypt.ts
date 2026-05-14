import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type Algorithm = 'RSA_OAEP' | 'AES_CBC_256';

interface CipherResponseDto {
  result: string;
}

@Injectable({
  providedIn: 'root',
})
export class Encrypt {
  private readonly http = inject(HttpClient);

  private readonly base = environment.apiBaseUrl;

  encrypt(input: string, algorithm: Algorithm): Observable<string> {
    return this.http
      .post<CipherResponseDto>(`${this.base}/encrypt`, { input, algorithm })
      .pipe(map(r => r.result));
  }

  decrypt(input: string, algorithm: Algorithm): Observable<string> {
    return this.http
      .post<CipherResponseDto>(`${this.base}/decrypt`, { input, algorithm })
      .pipe(map(r => r.result));
  }
  
}
