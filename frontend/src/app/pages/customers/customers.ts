import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Modules
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { AvatarModule } from 'primeng/avatar';
import { TooltipModule } from 'primeng/tooltip';

// Services & Models
import { MessageService, ConfirmationService } from 'primeng/api';
import { TranslateModule } from '@ngx-translate/core';
import {
  CustomerService,
  CustomerResponseDto,
  CustomerCreateDto,
  CustomerUpdateDto
} from '../../api';

// Core
import { RoleService } from '../../core/role.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ToolbarModule,
    TagModule,
    DialogModule,
    ToastModule,
    ConfirmDialogModule,
    InputNumberModule,
    AvatarModule,
    TranslateModule,
    TooltipModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './customers.html',
  styleUrl: './customers.scss'
})
export class CustomersComponent implements OnInit {
  customers: CustomerResponseDto[] = [];
  loading = true;
  customerDialog = false;
  submitted = false;
  customer: Partial<CustomerResponseDto & CustomerCreateDto> = {};

  /** Exposed to template for structural directives */
  get isAdmin(): boolean {
    return this.roleService.isAdmin();
  }

  constructor(
    private customerService: CustomerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private roleService: RoleService
  ) {}

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data;
        this.loading = false;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load customers'
        });
        this.loading = false;
      }
    });
  }

  openNew() {
    this.customer = { discountRate: 0 };
    this.submitted = false;
    this.customerDialog = true;
  }

  editCustomer(cust: CustomerResponseDto) {
    this.customer = { ...cust };
    this.customerDialog = true;
  }

  deleteCustomer(cust: CustomerResponseDto) {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${cust.firstName} ${cust.lastName}?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.customerService.deleteCustomer(cust.id!).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Customer deleted'
            });
            this.loadCustomers();
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Failed to delete'
            });
          }
        });
      }
    });
  }

  saveCustomer() {
    this.submitted = true;

    if (!this.customer.firstName?.trim() || !this.customer.lastName?.trim() ||
      !this.customer.phone?.trim() || this.customer.discountRate === undefined) {
      return;
    }

    if (this.customer.id) {
      const updateDto: CustomerUpdateDto = {
        firstName: this.customer.firstName,
        lastName: this.customer.lastName,
        phone: this.customer.phone,
        discountRate: this.customer.discountRate
      };

      this.customerService.updateCustomer(this.customer.id, updateDto).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Customer updated'
          });
          this.hideDialog();
          this.loadCustomers();
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to update'
          });
        }
      });
    } else {
      const createDto: CustomerCreateDto = {
        firstName: this.customer.firstName!,
        lastName: this.customer.lastName!,
        phone: this.customer.phone!,
        discountRate: this.customer.discountRate!
      };

      this.customerService.createCustomer(createDto).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Customer created'
          });
          this.hideDialog();
          this.loadCustomers();
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to create'
          });
        }
      });
    }
  }

  hideDialog() {
    this.customerDialog = false;
    this.submitted = false;
  }

  getInitials(firstName?: string, lastName?: string): string {
    const first = firstName?.charAt(0) || '';
    const last = lastName?.charAt(0) || '';
    return (first + last).toUpperCase();
  }

  getDiscountSeverity(discount?: number): "success" | "warn" | "info" | "secondary" | undefined {
    if (!discount) return 'secondary';
    if (discount >= 20) return 'success';
    if (discount >= 10) return 'warn';
    return 'secondary';
  }
}
