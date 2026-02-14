import 'zone.js';
import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

// PrimeNG
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';

// i18n
import { TranslateModule } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { routes } from './app.routes';
import { authInterceptor } from './core/auth.interceptor';
import { Configuration } from './api';

export class FixBlobConfiguration extends Configuration {
  override isJsonMime(mime: string): boolean {
    if (!mime || mime === '*/*') {
      return true;
    }
    return super.isJsonMime(mime);
  }
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimationsAsync(),

    { provide: Configuration, useClass: FixBlobConfiguration },
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: false
        }
      }
    }),

    importProvidersFrom(
      TranslateModule.forRoot()
    ),

    provideTranslateHttpLoader({
      prefix: '/i18n/',
      suffix: '.json'
    })
  ]
};
