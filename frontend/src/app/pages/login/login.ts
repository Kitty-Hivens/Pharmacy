import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { RippleModule } from 'primeng/ripple';
import { MessageModule } from 'primeng/message';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../api';
import { AuthRequest } from '../../api';

/**
 * Login Page Component.
 * Handles user authentication and token/role storage.
 */
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
    private authService: AuthService,
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

    this.authService.login(
      this.authRequest,
      'body',
      false,
      { httpHeaderAccept: 'application/json' as any }
    ).subscribe({
      next: (response) => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          // Store role for RBAC-driven UI
          localStorage.setItem('role', response.role || '');
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = 'LOGIN.ERROR';
        }
        this.loading = false;
      },
      error: (err) => {
        console.error();
        this.errorMessage = 'LOGIN.ERROR';
        this.loading = false;
      }
    });
  }
}
