import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterModule } from '@angular/router';

// PrimeNG Imports
// Убрали SidebarModule, так как он не используется в HTML
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { AvatarModule } from 'primeng/avatar';
import { BadgeModule } from 'primeng/badge';
import { TooltipModule } from 'primeng/tooltip';
import { MenuItem } from 'primeng/api';

// i18n
import { TranslateModule, TranslateService } from '@ngx-translate/core';

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
export class MainLayoutComponent implements OnInit {
  sidebarVisible = true;
  isMobileScreen = false;

  menuItems: MenuItem[] | undefined;
  userMenuItems: MenuItem[] | undefined;

  constructor(
    private router: Router,
    public translate: TranslateService
  ) {}

  ngOnInit() {
    this.checkScreenSize();
    this.initMenu();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
  }

  checkScreenSize() {
    this.isMobileScreen = window.innerWidth <= 991;
    // На мобилках меню по умолчанию скрыто, на десктопе открыто
    if (this.isMobileScreen) {
      this.sidebarVisible = false;
    } else {
      this.sidebarVisible = true;
    }
  }

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }

  switchLanguage() {
    const current = this.translate.currentLang;
    const next = current === 'en' ? 'ru' : 'en';
    this.translate.use(next);
    localStorage.setItem('lang', next);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  initMenu() {
    // Структура меню на основе твоих контроллеров
    this.menuItems = [
      {
        label: 'Pharmacy',
        items: [
          {
            label: 'Dashboard',
            icon: 'pi pi-home',
            routerLink: '/dashboard'
          },
          {
            label: 'POS Terminal',
            icon: 'pi pi-calculator',
            routerLink: '/pos',
            styleClass: 'text-primary font-bold' // Выделим кассу цветом
          }
        ]
      },
      {
        label: 'Inventory',
        items: [
          {
            label: 'Medicines',
            icon: 'pi pi-box',
            routerLink: '/medicines'
          },
          {
            label: 'Stock Alert',
            icon: 'pi pi-exclamation-circle',
            routerLink: '/inventory',
            badge: '12', // Пример бейджика
            badgeStyleClass: 'p-badge-danger'
          }
        ]
      },
      {
        label: 'Business',
        items: [
          {
            label: 'Sales History',
            icon: 'pi pi-history',
            routerLink: '/sales'
          },
          {
            label: 'Customers',
            icon: 'pi pi-users',
            routerLink: '/customers'
          },
          {
            label: 'Suppliers',
            icon: 'pi pi-truck',
            routerLink: '/suppliers'
          }
        ]
      },
      {
        label: 'Admin',
        items: [
          {
            label: 'Employees',
            icon: 'pi pi-id-card',
            routerLink: '/users'
          }
        ]
      }
    ];

    this.userMenuItems = [
      {
        label: 'Profile',
        icon: 'pi pi-user'
      },
      {
        label: 'Settings',
        icon: 'pi pi-cog'
      },
      {
        separator: true
      },
      {
        label: 'Logout',
        icon: 'pi pi-sign-out',
        command: () => this.logout()
      }
    ];
  }
}
