import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';

/**
 * Decodes the JWT payload and checks whether the token has expired.
 * Does NOT verify the signature — that is the backend's responsibility.
 * This is purely a UX guard to avoid sending the user into the app
 * with a token that will immediately fail on the first API call.
 */
function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    // exp is in seconds, Date.now() is in milliseconds
    return payload.exp * 1000 < Date.now();
  } catch {
    // Malformed token — treat as expired
    return true;
  }
}

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  // previously only checked token existence, not validity.
  // An expired token would pass the guard, the user would enter the app,
  // and then hit a 401 on the first API request which redirected to /login anyway.
  // Now we check expiry upfront for a cleaner UX.
  if (token && !isTokenExpired(token)) {
    return true;
  }

  // Clean up stale token if present
  if (token) {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  router.navigate(['/login']);
  return false;
};
