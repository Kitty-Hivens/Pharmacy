import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { MedicinesComponent } from './pages/medicines/medicines';
import { PosComponent } from './pages/pos/pos';
import { SalesHistoryComponent } from './pages/sales-history/sales-history';
import { PlaceholderComponent } from './pages/placeholder/placeholder';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'medicines',
        component: MedicinesComponent
      },
      {
        path: 'pos',
        component: PosComponent
      },
      {
        path: 'inventory', component: PlaceholderComponent
      },
      {
        path: 'sales', component: SalesHistoryComponent
      },
      {
        path: 'customers', component: PlaceholderComponent
      },
      {
        path: 'suppliers', component: PlaceholderComponent
      },
      {
        path: 'users', component: PlaceholderComponent
      }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
