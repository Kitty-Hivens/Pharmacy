import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, forkJoin, takeUntil } from 'rxjs';

// PrimeNG Modules
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { SkeletonModule } from 'primeng/skeleton';
import { CardModule } from 'primeng/card';

// i18n
import { TranslateModule } from '@ngx-translate/core';

// API Services
import {
  SaleService,
  CustomerService,
  MedicineService,
  InventoryService,
  SaleResponseDto,
  Pageable
} from '../../api';

interface DashboardStats {
  totalSales: number;
  salesGrowth: number;
  totalCustomers: number;
  newCustomers: number;
  lowStockCount: number;
  totalMedicines: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    TableModule,
    SkeletonModule,
    CardModule,
    TranslateModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  stats: DashboardStats = {
    totalSales: 0,
    salesGrowth: 0,
    totalCustomers: 0,
    newCustomers: 0,
    lowStockCount: 0,
    totalMedicines: 0
  };

  recentSales: SaleResponseDto[] = [];
  loading = true;
  private destroy$ = new Subject<void>();

  constructor(
    private saleService: SaleService,
    private customerService: CustomerService,
    private medicineService: MedicineService,
    private inventoryService: InventoryService
  ) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDashboardData() {
    this.loading = true;

    const pageable: Pageable = {
      page: 0,
      size: 5,
      sort: ['saleDateTime,desc']
    };

    const inventoryPageable: Pageable = {
      page: 0,
      size: 1000,
      sort: ['stockQuantity,asc']
    };

    forkJoin({
      sales: this.saleService.getAllSales(pageable),
      customers: this.customerService.getAllCustomers(),
      medicines: this.medicineService.getAllMedicines(),
      inventory: this.inventoryService.getInventory(inventoryPageable)
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          // Recent sales
          this.recentSales = data.sales.content || [];

          // Calculate total sales amount
          this.stats.totalSales = this.recentSales.reduce(
            (sum, sale) => sum + (sale.totalAmount || 0),
            0
          );

          // Mock growth (in real app, compare with previous period)
          this.stats.salesGrowth = 15;

          // Customers
          this.stats.totalCustomers = data.customers.length;
          this.stats.newCustomers = Math.floor(data.customers.length * 0.05);

          // Medicines
          this.stats.totalMedicines = data.medicines.length;

          // Low stock items (quantity < 10)
          this.stats.lowStockCount = (data.inventory.content || []).filter(
            (item) => (item.stockQuantity || 0) < 10
          ).length;

          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load dashboard data:', err);
          this.loading = false;
        }
      });
  }

  getSeverity(
    status: string
  ): 'success' | 'secondary' | 'info' | 'warning' | 'danger' | 'contrast' | undefined {
    switch (status) {
      case 'Completed':
        return 'success';
      case 'Pending':
        return 'warning';
      case 'Cancelled':
        return 'danger';
      default:
        return 'info';
    }
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }
}
