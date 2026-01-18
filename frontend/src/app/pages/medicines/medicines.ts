import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Imports
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TextareaModule } from 'primeng/textarea';
import { CheckboxModule } from 'primeng/checkbox';
import { InputNumberModule } from 'primeng/inputnumber';

// Services
import { MessageService, ConfirmationService } from 'primeng/api';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MedicineService, MedicineResponseDto, MedicineCreateDto, MedicineUpdateDto } from '../../api';

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
    TooltipModule,
    DialogModule,
    ToastModule,
    ConfirmDialogModule,
    TextareaModule,
    CheckboxModule,
    InputNumberModule,
    TranslateModule
  ],
  providers: [MessageService, ConfirmationService],
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
  // Dialog State
  medicineDialog = false;
  submitted = false;

  medicine: Partial<MedicineResponseDto & MedicineCreateDto> = {};

  constructor(
    private medicineService: MedicineService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private translate: TranslateService
  ) {}

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
      next: (data) => {
        this.medicines = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load medicines', err);
        this.loading = false;
      }
    });
  }

  // --- CRUD Actions ---

  openNew() {
    this.medicine = {
      prescriptionRequired: false // Default value
    };
    this.submitted = false;
    this.medicineDialog = true;
  }

  editMedicine(med: MedicineResponseDto) {
    this.medicine = { ...med };
    this.medicineDialog = true;
  }

  deleteMedicine(med: MedicineResponseDto) {
    this.confirmationService.confirm({
      message: this.translate.instant('MEDICINES.DELETE_CONFIRM', { name: med.name }),
      header: this.translate.instant('MEDICINES.DELETE_HEADER'),
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.medicineService.deleteMedicine(med.id!).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: this.translate.instant('MEDICINES.MESSAGES.DELETED')
            });
            this.loadMedicines(); // Refresh list
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete' });
          }
        });
      }
    });
  }

  saveMedicine() {
    this.submitted = true;

    if (!this.medicine.name?.trim() || !this.medicine.price) {
      return;
    }

    if (this.medicine.id) {
      // UPDATE
      const updateDto: MedicineUpdateDto = {
        name: this.medicine.name,
        manufacturer: this.medicine.manufacturer,
        description: this.medicine.description,
        price: this.medicine.price,
        prescriptionRequired: this.medicine.prescriptionRequired
      };

      this.medicineService.updateMedicine(this.medicine.id, updateDto).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: this.translate.instant('MEDICINES.MESSAGES.UPDATED') });
          this.hideDialog();
          this.loadMedicines();
        }
      });
    } else {
      // CREATE
      const createDto: MedicineCreateDto = {
        name: this.medicine.name!,
        manufacturer: this.medicine.manufacturer!,
        description: this.medicine.description,
        price: this.medicine.price!,
        prescriptionRequired: this.medicine.prescriptionRequired || false
      };

      this.medicineService.createMedicine(createDto).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: this.translate.instant('MEDICINES.MESSAGES.CREATED') });
          this.hideDialog();
          this.loadMedicines();
        }
      });
    }
  }

  hideDialog() {
    this.medicineDialog = false;
    this.submitted = false;
  }

  // --- Helpers ---

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
