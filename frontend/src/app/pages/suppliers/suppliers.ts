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
import { AvatarModule } from 'primeng/avatar';
import { TooltipModule } from 'primeng/tooltip';

// Services & Models
import { MessageService } from 'primeng/api';
import { TranslateModule } from '@ngx-translate/core';
import { SupplierService, SupplierDto } from '../../api';

@Component({
  selector: 'app-suppliers',
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
    AvatarModule,
    TranslateModule,
    TooltipModule
  ],
  providers: [MessageService],
  templateUrl: './suppliers.html',
  styleUrl: './suppliers.scss'
})
export class SuppliersComponent implements OnInit {
  suppliers: SupplierDto[] = [];
  loading = true;
  supplierDialog = false;
  submitted = false;
  supplier: Partial<SupplierDto> = {};

  constructor(
    private supplierService: SupplierService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadSuppliers();
  }

  loadSuppliers() {
    this.loading = true;
    this.supplierService.getAllSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
        this.loading = false;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load suppliers'
        });
        this.loading = false;
      }
    });
  }

  openNew() {
    this.supplier = {};
    this.submitted = false;
    this.supplierDialog = true;
  }

  saveSupplier() {
    this.submitted = true;

    if (!this.supplier.name?.trim()) {
      return;
    }

    const dto: SupplierDto = {
      name: this.supplier.name,
      contactPerson: this.supplier.contactPerson,
      email: this.supplier.email,
      phone: this.supplier.phone
    };

    this.supplierService.createSupplier(dto).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Supplier created'
        });
        this.hideDialog();
        this.loadSuppliers();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to create supplier'
        });
      }
    });
  }

  hideDialog() {
    this.supplierDialog = false;
    this.submitted = false;
  }

  getInitials(name?: string): string {
    if (!name) return '?';
    const words = name.split(' ');
    if (words.length >= 2) {
      return (words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}
