import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const token = localStorage.getItem('token');
  const lang = localStorage.getItem('app-lang') || 'en';

  let newReq = req.clone({
    setHeaders: {
      'Accept-Language': lang
    }
  });

  if (token) {
    newReq = newReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(newReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        localStorage.removeItem('token');
        void router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
