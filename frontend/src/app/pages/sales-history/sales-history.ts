import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

// PrimeNG Modules
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { DatePickerModule } from 'primeng/datepicker';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { Ripple } from 'primeng/ripple';

// i18n
import { TranslateModule } from '@ngx-translate/core';

// API Services & Models
import {
  SaleService,
  SaleResponseDto,
  PageSaleResponseDto,
  Pageable
} from '../../api';

/**
 * Sales History Component.
 * Displays paginated sales records with filtering capabilities.
 *
 * Features:
 * - Server-side pagination
 * - Date range filtering
 * - Detailed view of sale items
 * - Expandable rows for line items
 */
@Component({
  selector: 'app-sales-history',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    CardModule,
    TagModule,
    TooltipModule,
    DialogModule,
    DatePickerModule,
    IconFieldModule,
    InputIconModule,
    TranslateModule,
    Ripple
  ],
  templateUrl: './sales-history.html',
  styleUrl: './sales-history.scss'
})
export class SalesHistoryComponent implements OnInit, OnDestroy {
  // Pagination State
  sales: SaleResponseDto[] = [];
  totalRecords = 0;
  loading = true;
  first = 0;
  rows = 10;

  // Filter State
  dateFrom: Date | null = null;
  dateTo: Date | null = null;

  // Detail Dialog
  selectedSale: SaleResponseDto | null = null;
  detailsVisible = false;

  private destroy$ = new Subject<void>();

  constructor(
    private saleService: SaleService,
  ) {}

  ngOnInit() {
    this.loadSales();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Loads sales with current pagination and filter settings.
   */
  loadSales(event?: any) {
    this.loading = true;

    const page = event ? event.first / event.rows : 0;
    const size = event ? event.rows : this.rows;

    const pageable: Pageable = {
      page: page,
      size: size,
      sort: ['saleDateTime,desc']
    };

    // Convert dates to ISO strings if present
    const fromStr = this.dateFrom ? this.toLocalISO(this.dateFrom) : undefined;
    const toStr   = this.dateTo ? this.toLocalISO(this.dateTo, true) : undefined;

    this.saleService.getAllSales(pageable, fromStr, toStr)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: PageSaleResponseDto) => {
          this.sales = response.content || [];
          this.totalRecords = response.totalElements || 0;
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load sales:', err);
          this.sales = [];
          this.totalRecords = 0;
          this.loading = false;
        }
      });
  }

  /**
   * Applies date filters and reloads data.
   */
  applyFilters() {
    this.first = 0; // Reset to first page
    this.loadSales();
  }

  /**
   * Clears all filters and reloads data.
   */
  clearFilters() {
    this.dateFrom = null;
    this.dateTo = null;
    this.first = 0;
    this.loadSales();
  }

  /**
   * Opens the details dialog for a specific sale.
   */
  viewDetails(sale: SaleResponseDto) {
    this.selectedSale = sale;
    this.detailsVisible = true;
  }

  /**
   * Prints the current sale receipt using browser print dialog.
   */
  printReceipt(sale?: SaleResponseDto) {
    if (sale) {
      this.selectedSale = sale;
      this.detailsVisible = true;
      // Small delay to ensure dialog is rendered before printing
      setTimeout(() => window.print(), 100);
    } else if (this.selectedSale) {
      window.print();
    }
  }

  /**
   * Formats the date-time string for display.
   */
  formatDateTime(dateTime?: string): string {
    if (!dateTime) return '-';
    const date = new Date(dateTime);
    return date.toLocaleString();
  }

  /**
   * Calculates total items quantity in a sale.
   */
  getTotalItems(sale: SaleResponseDto): number {
    return sale.items?.reduce((sum, item) => sum + (item.quantity || 0), 0) || 0;
  }


  private toLocalISO(date: Date, endOfDay = false): string {
    const d = new Date(date);
    if (endOfDay) d.setHours(23, 59, 59, 999);
    else d.setHours(0, 0, 0, 0);
    return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 19);
  }
}
