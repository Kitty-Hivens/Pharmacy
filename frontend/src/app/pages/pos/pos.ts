import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin, Subject, takeUntil} from 'rxjs';

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
import { DialogModule } from 'primeng/dialog';

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
    TranslateModule,
    DialogModule
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

  showReceiptDialog = false;
  lastSale: any = null;

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
    this.loadCartFromStorage();
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
    forkJoin({
      medicines: this.medicineService.getAllMedicines(),
      customers: this.customerService.getAllCustomers()
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.allMedicines = res.medicines;
          this.allCustomers = res.customers;
          this.loading = false;
        },
        error: (err) => {
          this.showError('POS.ERRORS.LOAD_DATA');
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

  // --- Persistence ---

  saveCartToStorage() {
    localStorage.setItem('pos_cart', JSON.stringify(this.cart));
  }

  loadCartFromStorage() {
    const saved = localStorage.getItem('pos_cart');
    if (saved) {
      this.cart = JSON.parse(saved);
    }
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
   * Performs a LIVE stock check against the server to prevent race conditions.
   */
  addToCart() {
    if (!this.selectedMedicine?.id) return;

    // Block UI to prevent multiple clicks
    this.loading = true;

    // 1. Request fresh stock data from server
    this.medicineService.getMedicine(this.selectedMedicine.id)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (response: any) => {
          // Extract body from response (handle ResponseEntity wrapper if present)
          const freshMedicine: MedicineResponseDto = response.body || response;
          const currentRealStock = freshMedicine.quantity || 0;

          // 2. Find item in local cart
          const existingItem = this.cart.find(i => i.medicine.id === freshMedicine.id);
          const quantityInCart = existingItem ? existingItem.quantity : 0;

          // 3. Check: (cart qty + 1) <= real stock
          if (quantityInCart + 1 <= currentRealStock) {

            // If item not in cart - add new
            if (!existingItem) {
              this.cart.push({
                medicine: freshMedicine, // Use fresh object
                quantity: 1,
                total: freshMedicine.price || 0
              });
            } else {
              // If exists - increment
              existingItem.quantity++;
              // Update price just in case
              existingItem.medicine.price = freshMedicine.price;
              this.recalculateItemTotal(existingItem);
            }

            // Reset selection
            this.selectedMedicine = null;
            this.saveCartToStorage();
          } else {
            // Error: requested quantity exceeds server stock
            // Uses existing i18n key: "Max stock available is {{max}}"
            this.showError('POS.ERRORS.STOCK_LIMIT_REACHED', {
              max: currentRealStock
            });

            // Update UI stock to reflect reality
            if (this.selectedMedicine) {
              this.selectedMedicine.quantity = currentRealStock;
            }
          }

          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to validate stock', err);
          this.showError('POS.ERRORS.LOAD_MEDICINES');
          this.loading = false;
        }
      });
  }

  /**
   * Removes an item from the cart.
   * @param item - The cart item to remove.
   */
  removeFromCart(item: CartItem) {
    const index = this.cart.indexOf(item);
    if (index > -1) {
      this.cart.splice(index, 1);
      this.saveCartToStorage();
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
    this.saveCartToStorage();
  }

  /**
   * Recalculates the total price for a single line item.
   */
  private recalculateItemTotal(item: CartItem) {
    const price = item.medicine.price || 0;
    item.total = item.quantity * price;
  }

  /**
   * Calculates the subtotal (before discount).
   */
  get subtotal(): number {
    return this.cart.reduce((acc, item) => acc + item.total, 0);
  }

  /**
   * Calculates the discount amount based on selected customer.
   */
  get discountAmount(): number {
    if (!this.selectedCustomer?.discountRate) return 0;
    return this.subtotal * (this.selectedCustomer.discountRate / 100);
  }

  /**
   * Calculates the grand total (subtotal - discount).
   */
  get grandTotal(): number {
    return this.subtotal - this.discountAmount;
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

          this.lastSale = {
            items: [...this.cart],
            total: this.grandTotal,
            date: new Date(),
            customer: this.selectedCustomer
          };

          this.resetForm();
          this.showReceiptDialog = true;
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
    this.saveCartToStorage();
    this.selectedCustomer = null;
    this.selectedMedicine = null;
    this.loading = false;
    // Refresh data to reflect updated stock levels
    this.loadData();
  }

  printReceipt() {
    window.print();
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
