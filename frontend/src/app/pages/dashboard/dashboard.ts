import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    TableModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent {
  recentSales = [
    { product: 'Amoxicillin 500mg', date: '2026-01-17', amount: 12.50, status: 'Completed' },
    { product: 'Vitamin C 1000mg', date: '2026-01-17', amount: 55.00, status: 'Pending' },
    { product: 'N95 Masks (Box)', date: '2026-01-16', amount: 15.00, status: 'Cancelled' },
    { product: 'Ibuprofen 400mg', date: '2026-01-16', amount: 8.50, status: 'Completed' },
    { product: 'Thermometer Digital', date: '2026-01-15', amount: 45.00, status: 'Completed' }
  ];

  getSeverity(status: string): "success" | "secondary" | "info" | "warning" | "danger" | "contrast" | undefined {
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
}
