import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideToastr } from 'ngx-toastr';
import { jwtInterceptor } from './infrastructure/auth/interceptor/jwt.interceptor';
import {AlertCircle, Calendar, CheckCircle, LucideAngularModule, Mail, XCircle} from 'lucide-angular';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimationsAsync(),
    provideToastr({ positionClass: 'toast-top-right' }),
    importProvidersFrom(LucideAngularModule.pick({ Mail, Calendar, CheckCircle, XCircle, AlertCircle })), provideAnimationsAsync(),
  ],
};
