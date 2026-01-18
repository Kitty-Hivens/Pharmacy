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
import { MedicineService } from '../../api';
import { MedicineResponseDto } from '../../api';

/**
 * Component responsible for managing and displaying the medicine inventory.
 * Provides functionality for listing, searching, and visualizing stock status.
 *
 * @see MedicineService
 */
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
  /** List of medicines retrieved from the backend. */
  medicines: MedicineResponseDto[] = [];

  /** Loading state indicator for UI spinners or skeletons. */
  loading = true;

  /** Current value of the global search filter. */
  searchValue: string | undefined;

  constructor(private medicineService: MedicineService) {}

  /**
   * Lifecycle hook that is called after data-bound properties of a directive are initialized.
   * Triggers the initial loading of medicine data.
   */
  ngOnInit() {
    this.loadMedicines();
  }

  /**
   * Fetches the complete list of medicines from the backend API.
   * Updates the {@link medicines} array and handles the {@link loading} state.
   *
   * @remarks
   * Uses the generated OpenAPI client {@link MedicineService}.
   */
  loadMedicines() {
    this.loading = true;
    this.medicineService.getAllMedicines().subscribe({
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
   * Used primarily by PrimeNG Tag component.
   *
   * @param quantity - The current stock level of the medicine.
   * @returns The PrimeNG severity string:
   * - 'success' for > 50 units
   * - 'warn' for > 10 units
   * - 'danger' for <= 10 units or undefined
   */
  getSeverity(quantity?: number): "success" | "warn" | "danger" | "info" | "secondary" | "contrast" | undefined {
    if (!quantity) return 'danger';
    if (quantity > 50) return 'success';
    if (quantity > 10) return 'warn';
    return 'danger';
  }

  /**
   * Returns a localization key for the stock status.
   *
   * @param quantity - The current stock level.
   * @returns A translation key string (e.g., 'INSTOCK', 'LOWSTOCK').
   */
  getStatus(quantity?: number): string {
    if (!quantity) return 'OUTOFSTOCK';
    if (quantity > 50) return 'INSTOCK';
    if (quantity > 10) return 'LOWSTOCK';
    return 'OUTOFSTOCK';
  }
}
