import { Routes } from '@angular/router';
import { LoginFormComponent } from './components/login-form/login-form.component'
import { RegisterFormComponent } from './components/register-form/register-form.component'
import { SearchPageComponent } from './components/search-page/search-page.component'
import { BoardUserComponent } from './components/board-user/board-user.component';
import { BoardAdminComponent } from './components/board-admin/board-admin.component';
import { FullSearchListComponent } from './components/full-search-list/full-search-list.component';
import { authGuard, authGuardRedirect } from './guards/auth.guard';

export const routes: Routes = [
    {path: 'login', component: LoginFormComponent, canActivate: [authGuardRedirect]},
    {path: 'register', component: RegisterFormComponent, canActivate: [authGuardRedirect]},
    {path: '', component: SearchPageComponent, canActivate: [authGuard]},
    {path: 'user', component: BoardUserComponent, canActivate: [authGuard]},
    {path: 'admin', component: BoardAdminComponent, canActivate: [authGuard]},
    {path: 'admin/search-history', component: FullSearchListComponent, canActivate: [authGuard]},
];


