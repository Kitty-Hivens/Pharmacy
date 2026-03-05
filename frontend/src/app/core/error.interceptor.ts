import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { MessageService } from 'primeng/api';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const messageService = inject(MessageService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMsg = 'An unexpected error occurred';

      if (error.error && error.error.message) {
        errorMsg = error.error.message;
      } else if (error.message) {
        errorMsg = error.message;
      }

      messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: errorMsg,
        life: 5000
      });

      return throwError(() => error);
    })
  );
};
