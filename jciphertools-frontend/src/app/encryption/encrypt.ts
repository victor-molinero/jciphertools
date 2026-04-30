import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';

export type Algorithm = 'RSA_OAEP' | 'AES_CBC_256';

interface CipherResponseDto {
  result: string;
}

@Injectable({
  providedIn: 'root',
})
export class Encrypt {

  encrypt(input: string, algorithm: Algorithm): Observable<string> {
    return new Observable<string>(observer => {
      observer.next("Encrypted result");
      observer.complete();
    });
  }

  decrypt(input: string, algorithm: Algorithm): Observable<string> {
    return new Observable<string>(observer => {
      observer.next("Decrypted result");
      observer.complete();
    });
  }
  
}
