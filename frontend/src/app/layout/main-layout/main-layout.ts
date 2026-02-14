import { Component, HostListener, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

// PrimeNG Imports
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { AvatarModule } from 'primeng/avatar';
import { BadgeModule } from 'primeng/badge';
import { TooltipModule } from 'primeng/tooltip';
import { MenuItem } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

// i18n
import { TranslateModule, TranslateService } from '@ngx-translate/core';

// API
import { InventoryService } from '../../api';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterModule,
    ButtonModule,
    MenuModule,
    AvatarModule,
    BadgeModule,
    TooltipModule,
    ToastModule,
    TranslateModule
  ],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss'
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  sidebarVisible = true;
  isMobileScreen = false;

  menuItems: MenuItem[] | undefined;
  userMenuItems: MenuItem[] | undefined;

  lowStockCount: string = '0';

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    public translate: TranslateService,
    private inventoryService: InventoryService
  ) {}

  ngOnInit() {
    this.checkScreenSize();
    this.checkLowStock();
    this.initMenu();
    this.translate.onLangChange
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.initMenu();
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
  }

  checkScreenSize() {
    this.isMobileScreen = window.innerWidth <= 991;
    this.sidebarVisible = !this.isMobileScreen;
  }

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }

  switchLanguage() {
    const currentLang = this.translate.getCurrentLang();
    const newLang = currentLang === 'en' ? 'ru' : 'en';
    this.translate.use(newLang);
    localStorage.setItem('app-lang', newLang);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  checkLowStock() {
    this.inventoryService.getInventory({ page: 0, size: 1000 }).subscribe({
      next: (res) => {
        const count = (res.content || []).filter(i => (i.stockQuantity || 0) < 10).length;
        this.lowStockCount = count.toString();
        this.initMenu();
      },
      error: () => {
        this.initMenu();
      }
    });
  }

  initMenu() {
    this.menuItems = [
      {
        label: this.translate.instant('MENU.PHARMACY'),
        items: [
          {
            label: this.translate.instant('MENU.DASHBOARD'),
            icon: 'pi pi-home',
            routerLink: '/dashboard'
          },
          {
            label: this.translate.instant('MENU.POS_TERMINAL'),
            icon: 'pi pi-calculator',
            routerLink: '/pos',
            styleClass: 'text-primary font-bold'
          }
        ]
      },
      {
        label: this.translate.instant('MENU.INVENTORY'),
        items: [
          {
            label: this.translate.instant('MENU.MEDICINES'),
            icon: 'pi pi-box',
            routerLink: '/medicines'
          },
          {
            label: this.translate.instant('MENU.STOCK_ALERT'),
            icon: 'pi pi-exclamation-circle',
            routerLink: '/inventory',
            badge: this.lowStockCount !== '0' ? this.lowStockCount : undefined,
            badgeStyleClass: 'p-badge-danger'
          }
        ]
      },
      {
        label: this.translate.instant('MENU.BUSINESS'),
        items: [
          {
            label: this.translate.instant('MENU.SALES_HISTORY'),
            icon: 'pi pi-history',
            routerLink: '/sales'
          },
          {
            label: this.translate.instant('MENU.CUSTOMERS'),
            icon: 'pi pi-users',
            routerLink: '/customers'
          },
          {
            label: this.translate.instant('MENU.SUPPLIERS'),
            icon: 'pi pi-truck',
            routerLink: '/suppliers'
          }
        ]
      },
      {
        label: this.translate.instant('MENU.ADMIN'),
        items: [
          {
            label: this.translate.instant('MENU.EMPLOYEES'),
            icon: 'pi pi-id-card',
            routerLink: '/users'
          }
        ]
      }
    ];

    this.userMenuItems = [
      {
        label: this.translate.instant('MENU.LOGOUT'),
        icon: 'pi pi-sign-out',
        command: () => this.logout()
      }
    ];
  }
}
