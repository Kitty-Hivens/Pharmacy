import { Injectable } from '@angular/core';

/**
 * Service for reading the current user's role.
 * Role is stored in localStorage after login.
 */
@Injectable({ providedIn: 'root' })
export class RoleService {

  getRole(): string {
    return localStorage.getItem('role') || '';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  clearRole(): void {
    localStorage.removeItem('role');
  }
}
