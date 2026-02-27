import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { RoleService } from './role.service';

/**
 * Guard that restricts route access to ADMIN role only.
 * Redirects to /dashboard if the user is not an admin.
 */
export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const roleService = inject(RoleService);

  if (roleService.isAdmin()) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
