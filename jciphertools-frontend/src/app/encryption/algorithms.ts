export type Algorithm = 'RSA_OAEP' | 'AES_CBC_256' | 'AES_GCM_256';

export interface Algorithms {
  value: Algorithm;
  label: string;
}