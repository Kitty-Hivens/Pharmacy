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
import { DatePickerModule } from 'primeng/datepicker';

// Services & Models
import { MessageService, ConfirmationService } from 'primeng/api';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import {
  MedicineService,
  InventoryService,
  MedicineResponseDto,
  MedicineCreateDto,
  MedicineUpdateDto,
  InventoryAddDto
} from '../../api';

/**
 * Component responsible for managing the medicine catalog and inventory supply.
 * Provides functionality for CRUD operations on medicines and adding stock (supply).
 *
 * @see MedicineService
 * @see InventoryService
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
    DatePickerModule,
    TranslateModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './medicines.html',
  styleUrl: './medicines.scss'
})
export class MedicinesComponent implements OnInit {
  /** List of medicines retrieved from the backend. */
  medicines: MedicineResponseDto[] = [];

  /** Loading state indicator for UI spinners. */
  loading = true;

  /** Current value of the global search filter. */
  searchValue: string | undefined;

  // --- Medicine Dialog State ---
  /** Controls visibility of the creation/edit medicine dialog. */
  medicineDialog = false;
  /** Flag to indicate if the form has been submitted (for validation display). */
  submitted = false;
  /** The medicine object currently being created or edited. */
  medicine: Partial<MedicineResponseDto & MedicineCreateDto> = {};

  // --- Supply Dialog State ---
  /** Controls visibility of the add stock (supply) dialog. */
  supplyDialog = false;
  /** The supply data object being filled by the user. */
  supply: Partial<InventoryAddDto & { expirationDateObj?: Date }> = {};
  /** The medicine selected for stock replenishment. */
  selectedMedicineForSupply: MedicineResponseDto | null = null;

  constructor(
    private medicineService: MedicineService,
    private inventoryService: InventoryService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private translate: TranslateService
  ) {}

  /**
   * Lifecycle hook. Triggers initial data loading.
   */
  ngOnInit() {
    this.loadMedicines();
  }

  /**
   * Fetches the complete list of medicines from the backend.
   * Updates {@link medicines} and handles the {@link loading} state.
   */
  loadMedicines() {
    this.loading = true;
    this.medicineService.getAllMedicines().subscribe({
      next: (data) => {
        this.medicines = data;
        this.loading = false;
      },
      error: () => {
        console.error();
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load data' });
        this.loading = false;
      }
    });
  }

  // --- CRUD Actions ---

  /**
   * Opens the dialog to create a new medicine.
   * Resets the form state.
   */
  openNew() {
    this.medicine = {
      prescriptionRequired: false
    };
    this.submitted = false;
    this.medicineDialog = true;
  }

  /**
   * Opens the dialog to edit an existing medicine.
   * @param med - The medicine to edit.
   */
  editMedicine(med: MedicineResponseDto) {
    this.medicine = { ...med };
    this.medicineDialog = true;
  }

  /**
   * Deletes a medicine after user confirmation.
   * @param med - The medicine to delete.
   */
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
            this.loadMedicines();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete' });
          }
        });
      }
    });
  }

  /**
   * Saves the current medicine (Create or Update).
   * Validates required fields before sending request.
   */
  saveMedicine() {
    this.submitted = true;

    if (!this.medicine.name?.trim() || !this.medicine.price || !this.medicine.manufacturer?.trim()) {
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
        },
        error: () => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to update' });
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
        },
        error: () => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to create' });
        }
      });
    }
  }

  /**
   * Closes the medicine dialog and resets submission state.
   */
  hideDialog() {
    this.medicineDialog = false;
    this.submitted = false;
  }

  // --- Supply Actions (Inventory) ---

  /**
   * Opens the supply dialog for a specific medicine.
   * @param med - The medicine to add stock to.
   */
  openSupply(med: MedicineResponseDto) {
    this.selectedMedicineForSupply = med;
    this.supply = {
      medicineId: med.id,
      quantity: 10,
      batchNumber: '',
      expirationDateObj: undefined // Temporary field for DatePicker
    };
    this.submitted = false;
    this.supplyDialog = true;
  }

  /**
   * Saves the supply (Adds inventory).
   * Validates batch number, date, and quantity.
   */
  saveSupply() {
    this.submitted = true;

    if (!this.supply.quantity || !this.supply.expirationDateObj || !this.supply.batchNumber) {
      return;
    }

    // Convert Date object to YYYY-MM-DD string for backend
    const dateStr = this.supply.expirationDateObj.toISOString().split('T')[0];

    const inventoryDto: InventoryAddDto = {
      medicineId: this.selectedMedicineForSupply!.id!,
      quantity: this.supply.quantity,
      batchNumber: this.supply.batchNumber,
      expirationDate: dateStr
    };

    this.inventoryService.restockInventory(inventoryDto).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Stock Updated',
          detail: `Added ${this.supply.quantity} items to ${this.selectedMedicineForSupply?.name}`
        });
        this.supplyDialog = false;
        this.loadMedicines(); // Refresh table to show new quantity
      },
      error: () => {
        console.error();
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to add stock' });
      }
    });
  }

  // --- Helpers ---

  /**
   * Returns the severity color for the stock status tag.
   * @param quantity - Current stock level.
   */
  getSeverity(quantity?: number): "success" | "warn" | "danger" | "info" | "secondary" | "contrast" | undefined {
    if (!quantity) return 'danger';
    if (quantity > 50) return 'success';
    if (quantity > 10) return 'warn';
    return 'danger';
  }

  /**
   * Returns a localization key for the stock status.
   * @param quantity - Current stock level.
   */
  getStatus(quantity?: number): string {
    if (!quantity) return 'OUTOFSTOCK';
    if (quantity > 50) return 'INSTOCK';
    if (quantity > 10) return 'LOWSTOCK';
    return 'OUTOFSTOCK';
  }
}
