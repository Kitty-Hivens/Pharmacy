import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  // 1. Получаем данные из LocalStorage
  const token = localStorage.getItem('token');
  // Язык берем из хранилища, или 'en' по умолчанию
  const lang = localStorage.getItem('app-lang') || 'en';

  // 2. Клонируем запрос, добавляя заголовки
  // Заголовки иммутабельны, поэтому мы создаем копию запроса
  let newReq = req.clone({
    setHeaders: {
      'Accept-Language': lang
    }
  });

  // Если есть токен, добавляем Authorization
  if (token) {
    newReq = newReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // 3. Передаем запрос дальше и слушаем ошибки
  return next(newReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Если сервер ответил 401 (Нет прав) или 403 (Запрещено)
      if (error.status === 401 || error.status === 403) {
        // Считаем токен невалидным -> чистим и на выход
        localStorage.removeItem('token');
        void router.navigate(['/login']);
      }
      // Пробрасываем ошибку дальше, чтобы компонент мог показать сообщение
      return throwError(() => error);
    })
  );
};
