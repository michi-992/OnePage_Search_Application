import { Routes } from '@angular/router';
import { BoardAdminComponent } from './components/board-admin/board-admin.component'
import { BoardUserComponent } from './components/board-user/board-user.component'
import { HomeComponent } from './components/home/home.component'
import { LoginFormComponent } from './components/login-form/login-form.component'
import { RegisterFormComponent } from './components/register-form/register-form.component'

export const routes: Routes = [
    {path: 'admin', component: BoardAdminComponent},
    {path: 'user', component: BoardUserComponent},
    {path: 'login', component: LoginFormComponent},
    {path: 'register', component: RegisterFormComponent},
    {path: '', component: HomeComponent},
];


