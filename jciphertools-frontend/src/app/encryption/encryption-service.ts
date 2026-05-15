import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EncryptionResponseDto } from './encryption-response-dto';
import { Algorithm, Algorithms} from './algorithms';

@Injectable({
  providedIn: 'root',
})
export class EncryptionService {

  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;

  encrypt(input: string, algorithm: Algorithm): Observable<string> {
    return this.http
      .post<EncryptionResponseDto>(`${this.base}/encrypt`, { input, algorithm })
      .pipe(map(r => r.result));
  }

  decrypt(input: string, algorithm: Algorithm): Observable<string> {
    return this.http
      .post<EncryptionResponseDto>(`${this.base}/decrypt`, { input, algorithm })
      .pipe(map(r => r.result));
  }
  
}
