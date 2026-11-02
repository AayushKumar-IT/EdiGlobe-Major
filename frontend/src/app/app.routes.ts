import { Component, inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-page-route',
  template: ''
})
export class PageRouteComponent {}

const authenticatedGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated ? true : router.createUrlTree(['/login']);
};

const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated ? router.createUrlTree(['/overview']) : true;
};

const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.role === 'ADMIN' ? true : router.createUrlTree(['/overview']);
};

export const routes: Routes = [
  { path: 'login', component: PageRouteComponent, data: { page: 'login' }, canActivate: [guestGuard] },
  { path: 'overview', component: PageRouteComponent, data: { page: 'overview' }, canActivate: [authenticatedGuard] },
  { path: 'questions', component: PageRouteComponent, data: { page: 'questions' }, canActivate: [authenticatedGuard] },
  { path: 'subjects', component: PageRouteComponent, data: { page: 'subjects' }, canActivate: [authenticatedGuard] },
  { path: 'generate', component: PageRouteComponent, data: { page: 'generate' }, canActivate: [authenticatedGuard] },
  { path: 'papers', component: PageRouteComponent, data: { page: 'papers' }, canActivate: [authenticatedGuard] },
  { path: 'users', component: PageRouteComponent, data: { page: 'users' }, canActivate: [authenticatedGuard, adminGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'overview' },
  { path: '**', redirectTo: 'overview' }
];
