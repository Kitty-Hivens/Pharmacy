import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Modules
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { DividerModule } from 'primeng/divider';
import { AvatarModule } from 'primeng/avatar';
import { MessageService } from 'primeng/api';

// API Services & Models
import {
  MedicineControllerService,
  CustomerControllerService,
  SaleControllerService,
  MedicineResponseDto,
  CustomerResponseDto,
  SaleCreateDto
} from '../../api';

/**
 * Interface representing a single line item in the shopping cart.
 */
interface CartItem {
  medicine: MedicineResponseDto;
  quantity: number;
  total: number;
}

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    TableModule,
    ButtonModule,
    AutoCompleteModule,
    InputNumberModule,
    ToastModule,
    DividerModule,
    AvatarModule
  ],
  providers: [MessageService],
  templateUrl: './pos.html',
  styleUrl: './pos.scss'
})
export class PosComponent implements OnInit {

  // Search State
  allMedicines: MedicineResponseDto[] = [];
  filteredMedicines: MedicineResponseDto[] = [];
  selectedMedicine: MedicineResponseDto | null = null;

  allCustomers: CustomerResponseDto[] = [];
  filteredCustomers: CustomerResponseDto[] = [];
  selectedCustomer: CustomerResponseDto | null = null;

  // Shopping Cart State
  cart: CartItem[] = [];
  loading = false;

  constructor(
    private medicineService: MedicineControllerService,
    private customerService: CustomerControllerService,
    private saleService: SaleControllerService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  /**
   * Preloads necessary data for the POS terminal.
   * In a production environment with large datasets, this should be replaced
   * by server-side filtering (lazy loading).
   */
  loadData() {
    this.loading = true;

    // --- Medicines ---
    this.medicineService.getAll3().subscribe({
      next: (response: any) => {
        console.log('Raw Medicines Response:', response);

        let data = response;
        if (!Array.isArray(response) && response.body) {
          data = response.body;
        }

        this.allMedicines = Array.isArray(data) ? data : [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading medicines', err);
        this.allMedicines = [];
        this.loading = false;
      }
    });

    // --- Customers ---
    this.customerService.getAll4().subscribe({
      next: (response: any) => {
        console.log('Raw Customers Response:', response);

        let data = response;
        if (!Array.isArray(response) && response.body) {
          data = response.body;
        }

        this.allCustomers = Array.isArray(data) ? data : [];
      },
      error: (err) => {
        console.error('Error loading customers', err);
        this.allCustomers = [];
      }
    });
  }

  // --- Search Logic ---

  filterMedicines(event: any) {
    const query = event.query.toLowerCase();
    this.filteredMedicines = this.allMedicines.filter(m =>
      (m.name?.toLowerCase().includes(query)) && (m.quantity && m.quantity > 0)
    );
  }

  filterCustomers(event: any) {
    const query = event.query.toLowerCase();
    this.filteredCustomers = this.allCustomers.filter(c =>
      (c.lastName?.toLowerCase().includes(query)) || (c.phone?.includes(query))
    );
  }

  // --- Cart Management ---

  /**
   * Adds the currently selected medicine to the cart.
   * If the item exists, increments quantity (respecting stock limits).
   */
  addToCart() {
    if (!this.selectedMedicine) return;

    const existingItem = this.cart.find(i => i.medicine.id === this.selectedMedicine?.id);
    const currentStock = this.selectedMedicine.quantity || 0;

    if (existingItem) {
      if (existingItem.quantity + 1 <= currentStock) {
        existingItem.quantity++;
        this.recalculateItemTotal(existingItem);
      } else {
        this.showError('Not enough stock available!');
      }
    } else {
      this.cart.push({
        medicine: this.selectedMedicine,
        quantity: 1,
        // FIX: Handle undefined price
        total: this.selectedMedicine.price || 0
      });
    }

    // Reset selection for rapid entry
    this.selectedMedicine = null;
  }

  removeFromCart(item: CartItem) {
    const index = this.cart.indexOf(item);
    if (index > -1) {
      this.cart.splice(index, 1);
    }
  }

  /**
   * Validates manual quantity input against available stock.
   */
  onQuantityChange(item: CartItem) {
    const maxStock = item.medicine.quantity || 0;
    if (item.quantity > maxStock) {
      item.quantity = maxStock;
      this.showError(`Max stock available is ${maxStock}`);
    }
    this.recalculateItemTotal(item);
  }

  private recalculateItemTotal(item: CartItem) {
    const price = item.medicine.price || 0;
    item.total = item.quantity * price;
  }

  get grandTotal(): number {
    return this.cart.reduce((acc, item) => acc + item.total, 0);
  }

  // --- Checkout Process ---

  /**
   * Submits the sale transaction to the backend.
   */
  checkout() {
    if (this.cart.length === 0) return;
    this.loading = true;

    const saleDto: SaleCreateDto = {
      customerId: this.selectedCustomer?.id,
      items: this.cart.map(i => ({
        medicineId: i.medicine.id!,
        quantity: i.quantity
      }))
    };

    this.saleService.create1(saleDto).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Sale processed successfully' });
        this.resetForm();
      },
      error: (err: any) => {
        console.error(err);
        this.showError('Transaction failed. Please check stock levels.');
        this.loading = false;
      }
    });
  }

  private resetForm() {
    this.cart = [];
    this.selectedCustomer = null;
    this.selectedMedicine = null;
    this.loading = false;
    // Refresh data to reflect updated stock levels
    this.loadData();
  }

  private showError(msg: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
  }
}
