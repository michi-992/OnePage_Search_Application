import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from '../services/storage/storage.service';

export const authGuard = () => {
  const storageService = inject(StorageService);
  const router = inject(Router);

  if (storageService.isLoggedIn()) {
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }
};

export const authGuardRedirect = () => {
  const storageService = inject(StorageService);
  const router = inject(Router);

  if (storageService.isLoggedIn()) {
    router.navigate(['/']);
    return false;
  } else {
    return true;
  }
};
