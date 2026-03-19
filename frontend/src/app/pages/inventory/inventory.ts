import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';

// PrimeNG Modules
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ToastModule } from 'primeng/toast';

// Services & Models
import { MessageService } from 'primeng/api';
import { TranslateModule } from '@ngx-translate/core';
import { InventoryService, InventoryResponseDto, Pageable } from '../../api';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ToolbarModule,
    TagModule,
    TooltipModule,
    ToastModule,
    TranslateModule
],
  providers: [MessageService],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})
export class InventoryComponent implements OnInit {
  inventory: InventoryResponseDto[] = [];
  loading = true;
  first = 0;
  rows = 10;
  totalRecords = 0;
  searchValue = '';

  constructor(
    private inventoryService: InventoryService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory(event?: any) {
    this.loading = true;

    const page = event ? event.first / event.rows : 0;
    const size = event ? event.rows : this.rows;

    const pageable: Pageable = {
      page: page,
      size: size,
      sort: ['expirationDate,asc']
    };

    this.inventoryService.getInventory(pageable, this.searchValue).subscribe({
      next: (response) => {
        this.inventory = response.content || [];
        this.totalRecords = response.totalElements || 0;
        this.loading = false;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load inventory'
        });
        this.loading = false;
      }
    });
  }

  onSearch() {
    this.first = 0;
    this.loadInventory();
  }

  getDaysUntilExpiry(expirationDate?: string): string {
    if (!expirationDate) return '';
    const expiry = new Date(expirationDate);
    const today = new Date();
    const diffTime = expiry.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return 'Expired';
    if (diffDays === 0) return 'Expires today';
    if (diffDays === 1) return 'Expires tomorrow';
    return `${diffDays} days left`;
  }

  isExpiringSoon(expirationDate?: string): boolean {
    if (!expirationDate) return false;
    const expiry = new Date(expirationDate);
    const today = new Date();
    const diffTime = expiry.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays <= 30;
  }

  getStockStatus(quantity?: number): string {
    if (!quantity || quantity === 0) return 'OUT';
    if (quantity < 10) return 'LOW';
    if (quantity < 50) return 'MEDIUM';
    return 'GOOD';
  }

  getStockSeverity(quantity?: number): "success" | "warn" | "danger" | "info" | undefined {
    if (!quantity || quantity === 0) return 'danger';
    if (quantity < 10) return 'warn';
    if (quantity < 50) return 'info';
    return 'success';
  }
}
