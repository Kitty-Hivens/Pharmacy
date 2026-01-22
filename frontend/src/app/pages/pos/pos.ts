import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

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

// i18n
import { TranslateModule, TranslateService } from '@ngx-translate/core';

// API Services & Models
import {
  MedicineService,
  CustomerService,
  SaleService,
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

/**
 * POS (Point of Sale) Component.
 * Handles the main checkout process: adding medicines to cart, selecting customers,
 * calculating totals, and processing sales transactions.
 */
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
    AvatarModule,
    TranslateModule
  ],
  providers: [MessageService],
  templateUrl: './pos.html',
  styleUrl: './pos.scss'
})
export class PosComponent implements OnInit, OnDestroy {
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

  private destroy$ = new Subject<void>();

  constructor(
    private medicineService: MedicineService,
    private customerService: CustomerService,
    private saleService: SaleService,
    private messageService: MessageService,
    private translate: TranslateService
  ) {}

  /**
   * Initializes the component and preloads necessary data.
   */
  ngOnInit() {
    this.loadData();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Preloads medicines and customers.
   * @remarks In a real high-load production env, this should be replaced by server-side filtering.
   */
  loadData() {
    this.loading = true;

    // --- Medicines ---
    this.medicineService.getAllMedicines()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: any) => {
          // Handle potential wrapper from ResponseEntity or direct array
          let data = response;
          if (!Array.isArray(response) && response.body) {
            data = response.body;
          }
          this.allMedicines = Array.isArray(data) ? data : [];
          this.loading = false;
        },
        error: (err) => {
          console.error();
          this.showError('POS.ERRORS.LOAD_MEDICINES');
          this.allMedicines = [];
          this.loading = false;
        }
      });

    // --- Customers ---
    this.customerService.getAllCustomers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: any) => {
          let data = response;
          if (!Array.isArray(response) && response.body) {
            data = response.body;
          }
          this.allCustomers = Array.isArray(data) ? data : [];
        },
        error: (err) => {
          console.error();
          this.showError('POS.ERRORS.LOAD_CUSTOMERS');
        }
      });
  }

  // --- Search Logic ---

  /**
   * Filters the medicine list based on user input.
   * Only shows items with positive stock.
   * @param event - The PrimeNG AutoComplete event.
   */
  filterMedicines(event: any) {
    const query = event.query.toLowerCase();
    this.filteredMedicines = this.allMedicines.filter(m =>
      (m.name?.toLowerCase().includes(query)) && (m.quantity && m.quantity > 0)
    );
  }

  /**
   * Filters the customer list based on name or phone.
   * @param event - The PrimeNG AutoComplete event.
   */
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
        this.showError('POS.ERRORS.NOT_ENOUGH_STOCK');
      }
    } else {
      this.cart.push({
        medicine: this.selectedMedicine,
        quantity: 1,
        total: this.selectedMedicine.price || 0
      });
    }

    // Reset selection for rapid entry
    this.selectedMedicine = null;
  }

  /**
   * Removes an item from the cart.
   * @param item - The cart item to remove.
   */
  removeFromCart(item: CartItem) {
    const index = this.cart.indexOf(item);
    if (index > -1) {
      this.cart.splice(index, 1);
    }
  }

  /**
   * Validates manual quantity input against available stock.
   * Called on input change in the UI.
   * @param item - The cart item being modified.
   */
  onQuantityChange(item: CartItem) {
    const maxStock = item.medicine.quantity || 0;
    if (item.quantity > maxStock) {
      item.quantity = maxStock;
      this.showError('POS.ERRORS.STOCK_LIMIT_REACHED', { max: maxStock });
    }
    this.recalculateItemTotal(item);
  }

  /**
   * Recalculates the total price for a single line item.
   */
  private recalculateItemTotal(item: CartItem) {
    const price = item.medicine.price || 0;
    item.total = item.quantity * price;
  }

  /**
   * Calculates the grand total of the cart.
   */
  get grandTotal(): number {
    return this.cart.reduce((acc, item) => acc + item.total, 0);
  }

  // --- Checkout Process ---

  /**
   * Submits the sale transaction to the backend.
   * Validates cart content before submission.
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

    this.saleService.createSale(saleDto)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: this.translate.instant('POS.SUCCESS_TITLE'),
            detail: this.translate.instant('POS.SUCCESS_DETAIL')
          });
          this.resetForm();
        },
        error: (err: any) => {
          console.error();
          this.showError('POS.ERRORS.TRANSACTION_FAILED');
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

  /**
   * Helper to show translated error messages.
   * @param key - Translation key.
   * @param params - Optional parameters for translation.
   */
  private showError(key: string, params?: Object) {
    this.translate.get(key, params).subscribe(msg => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
    });
  }
}
