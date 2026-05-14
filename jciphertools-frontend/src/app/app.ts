import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { EncryptTextForm } from './encryption/encrypt-text-form/encrypt-text-form';

@Component({
  selector: 'app-root',
  imports: [EncryptTextForm],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  protected readonly title = signal('jciphertools-frontend');
}
