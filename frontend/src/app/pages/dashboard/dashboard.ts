import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, forkJoin, takeUntil } from 'rxjs';

// PrimeNG Modules
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { SkeletonModule } from 'primeng/skeleton';
import { CardModule } from 'primeng/card';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';

// i18n
import { TranslateModule } from '@ngx-translate/core';

// API Services
import {
  SaleService,
  CustomerService,
  MedicineService,
  InventoryService,
  SaleResponseDto
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
    MenuModule,
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
  menuItems: MenuItem[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private saleService: SaleService,
    private customerService: CustomerService,
    private medicineService: MedicineService,
    private inventoryService: InventoryService,
    private router: Router
  ) {}

  ngOnInit() {
    this.initMenu();
    this.loadDashboardData();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initMenu() {
    this.menuItems = [
      {
        label: 'Refresh Data',
        icon: 'pi pi-refresh',
        command: () => this.loadDashboardData()
      },
      {
        label: 'Export Report',
        icon: 'pi pi-download',
        command: () => console.log('Export clicked')
      }
    ];
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  loadDashboardData() {
    this.loading = true;

    forkJoin({
      sales: this.saleService.getAllSales({ page: 0, size: 1000, sort: ['saleDateTime,desc'] }),
      customers: this.customerService.getAllCustomers(),
      medicines: this.medicineService.getAllMedicines(),
      inventory: this.inventoryService.getInventory({ page: 0, size: 1000 })
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          // Recent sales
          const allSales = data.sales.content || [];
          this.recentSales = allSales.slice(0, 10);

          // Calculate total sales amount
          this.stats.totalSales = allSales.reduce(
            (sum, sale) => sum + (sale.totalAmount || 0),
            0
          );

          // Mock growth
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

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }
}
