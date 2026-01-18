import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { TooltipModule } from 'primeng/tooltip';
import { MedicineControllerService } from '../../api';
import { MedicineResponseDto } from '../../api';

@Component({
  selector: 'app-medicines',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ToolbarModule,
    TagModule,
    IconFieldModule,
    InputIconModule,
    TooltipModule
  ],
  templateUrl: './medicines.html',
  styleUrl: './medicines.scss'
})
export class MedicinesComponent implements OnInit {
  medicines: MedicineResponseDto[] = [];
  loading = true;
  searchValue: string | undefined;

  constructor(private medicineService: MedicineControllerService) {}

  ngOnInit() {
    this.loadMedicines();
  }

  /**
   * Fetches the complete list of medicines from the backend.
   * Uses the generated API client.
   */
  loadMedicines() {
    this.loading = true;
    this.medicineService.getAll3().subscribe({
      next: (data: MedicineResponseDto[]) => {
        this.medicines = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Failed to load medicines', err);
        this.loading = false;
      }
    });
  }

  /**
   * Determines the severity color for the status tag based on stock quantity.
   */
  getSeverity(quantity?: number): "success" | "warn" | "danger" | "info" | "secondary" | "contrast" | undefined {
    if (!quantity) return 'danger';
    if (quantity > 50) return 'success';
    if (quantity > 10) return 'warn';
    return 'danger';
  }

  /**
   * Returns a localized status string based on stock quantity.
   */
  getStatus(quantity?: number): string {
    if (!quantity) return 'OUTOFSTOCK';
    if (quantity > 50) return 'INSTOCK';
    if (quantity > 10) return 'LOWSTOCK';
    return 'OUTOFSTOCK';
  }
}
