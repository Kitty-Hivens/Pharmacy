import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// PrimeNG
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { RippleModule } from 'primeng/ripple';
import { MessageModule } from 'primeng/message';

// i18n
import { TranslateService, TranslateModule } from '@ngx-translate/core';

// API
import { AuthControllerService } from '../../api';
import { AuthRequest } from '../../api';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CheckboxModule,
    InputTextModule,
    PasswordModule,
    RippleModule,
    MessageModule,
    TranslateModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  authRequest: AuthRequest = {
    username: '',
    password: ''
  };

  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthControllerService,
    private router: Router,
    public translate: TranslateService
  ) {
    const savedLang = localStorage.getItem('lang') || 'en';
    this.translate.use(savedLang);
  }

  switchLanguage(lang: string) {
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
  }

  login() {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.authRequest).subscribe({
      next: (response) => {
        // 1. Проверяем, пришел ли токен
        if (response.token) {
          // 2. Сохраняем токен
          localStorage.setItem('token', response.token);
          this.router.navigate(['/dashboard']);
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Ошибка входа:', err);
        // Пытаемся достать текст ошибки с бэкенда, иначе показываем дефолтную
        this.errorMessage = err.error?.message || 'LOGIN.ERROR';
        this.loading = false;
      }
    });
  }
}
