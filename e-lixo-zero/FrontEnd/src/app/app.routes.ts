import { Routes } from '@angular/router';

import { HomeComponent } from './pages/home/home';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';

import { Dashboard } from './pages/dashboard/dashboard';
import { CollectionPointsComponent } from './pages/collection-points/collection-points';
import { RequestPickup } from './pages/request-pickup/request-pickup';
import { MyPickups } from './pages/my-pickups/my-pickups';
import { WasteTypes } from './pages/waste-types/waste-types';
import { Notifications } from './pages/notifications/notifications';
import { Profile } from './pages/profile/profile';
import { MainLayout } from './layouts/main-layout/main-layout';
import { About } from './pages/about/about';
import { HowItWorks } from './pages/how-it-works/how-it-works';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  
    {
  path: '',
  component: MainLayout,
  children: [
    { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
    { path: 'collection-points', component: CollectionPointsComponent },
    { path: 'request-pickup', component: RequestPickup, canActivate: [authGuard] },
    { path: 'my-pickups', component: MyPickups, canActivate: [authGuard] },
    { path: 'waste-types', component: WasteTypes},
    { path: 'notifications', component: Notifications, canActivate: [authGuard] },
    { path: 'profile', component: Profile, canActivate: [authGuard] },
    { path: 'about', component: About },
{ path: 'how-it-works', component: HowItWorks },
  ],
},

  { path: '**', redirectTo: '' },
];