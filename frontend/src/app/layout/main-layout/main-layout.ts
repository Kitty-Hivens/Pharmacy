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

// i18n
import { TranslateModule, TranslateService } from '@ngx-translate/core';

/**
 * Main application layout component.
 * Handles the sidebar, topbar, responsive behavior, and navigation menu.
 */
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

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    public translate: TranslateService
  ) {}

  ngOnInit() {
    this.checkScreenSize();
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
    if (this.isMobileScreen) {
      this.sidebarVisible = false;
    } else {
      this.sidebarVisible = true;
    }
  }

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }

  /**
   * Switches the global application language (EN <-> RU).
   * The actual text update in the menu is handled by the subscription in ngOnInit.
   */
  switchLanguage() {
    const current = this.translate.getCurrentLang();
    const next = current === 'en' ? 'ru' : 'en';
    this.translate.use(next);
    localStorage.setItem('lang', next);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  /**
   * Generates the menu structure using current translations.
   * Uses translate.instant() to get synchronous translation values.
   */
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
            badge: '12',
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
        label: this.translate.instant('MENU.PROFILE'),
        icon: 'pi pi-user'
      },
      {
        label: this.translate.instant('MENU.SETTINGS'),
        icon: 'pi pi-cog'
      },
      {
        separator: true
      },
      {
        label: this.translate.instant('MENU.LOGOUT'),
        icon: 'pi pi-sign-out',
        command: () => this.logout()
      }
    ];
  }
}
