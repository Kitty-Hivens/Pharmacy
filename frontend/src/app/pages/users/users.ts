import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Modules
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToolbarModule } from 'primeng/toolbar';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { AvatarModule } from 'primeng/avatar';
import { TagModule } from 'primeng/tag';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { TooltipModule } from 'primeng/tooltip';

// Services & Models
import { MessageService, ConfirmationService } from 'primeng/api';
import { TranslateModule } from '@ngx-translate/core';
import { UserService, UserResponseDto, UserCreateDto } from '../../api';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ToolbarModule,
    DialogModule,
    ToastModule,
    ConfirmDialogModule,
    AvatarModule,
    TagModule,
    PasswordModule,
    SelectModule,
    TooltipModule,
    TranslateModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './users.html',
  styleUrl: './users.scss'
})
export class UsersComponent implements OnInit {
  users: UserResponseDto[] = [];
  loading = true;
  userDialog = false;
  submitted = false;
  user: Partial<UserCreateDto> = {};

  roles = [
    { label: 'Administrator', value: 'ADMIN' },
    { label: 'Pharmacist', value: 'PHARMACIST' }
  ];

  constructor(
    private userService: UserService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load users'
        });
        this.loading = false;
      }
    });
  }

  openNew() {
    this.user = { role: 'PHARMACIST' };
    this.submitted = false;
    this.userDialog = true;
  }

  deleteUser(usr: UserResponseDto) {
    if (usr.username === 'admin') {
      this.messageService.add({
        severity: 'warn',
        summary: 'Warning',
        detail: 'Cannot delete the admin user'
      });
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${usr.firstName} ${usr.lastName}?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.userService.deleteUser(usr.id!).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'User deleted'
            });
            this.loadUsers();
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Failed to delete user'
            });
          }
        });
      }
    });
  }

  saveUser() {
    this.submitted = true;

    if (!this.user.firstName?.trim() || !this.user.lastName?.trim() ||
      !this.user.username?.trim() || !this.user.password || !this.user.role) {
      return;
    }

    if (this.user.password.length < 6) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Warning',
        detail: 'Password must be at least 6 characters'
      });
      return;
    }

    const dto: UserCreateDto = {
      firstName: this.user.firstName!,
      lastName: this.user.lastName!,
      position: this.user.position,
      username: this.user.username!,
      password: this.user.password!,
      role: this.user.role!
    };

    this.userService.registerUser(dto).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Employee registered successfully'
        });
        this.hideDialog();
        this.loadUsers();
      },
      error: (err) => {
        const errorMsg = err.error || 'Failed to register user';
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: errorMsg
        });
      }
    });
  }

  hideDialog() {
    this.userDialog = false;
    this.submitted = false;
  }

  getInitials(firstName?: string, lastName?: string): string {
    const first = firstName?.charAt(0) || '';
    const last = lastName?.charAt(0) || '';
    return (first + last).toUpperCase();
  }
}
