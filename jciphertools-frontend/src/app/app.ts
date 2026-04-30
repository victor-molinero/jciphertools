import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EncryptTextForm } from "./encryption/encrypt-text-form/encrypt-text-form";

@Component({
  selector: 'app-root',
  imports: [EncryptTextForm],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('jciphertools-frontend');
}
