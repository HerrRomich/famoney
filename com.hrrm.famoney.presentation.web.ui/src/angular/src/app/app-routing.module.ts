import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/accounts/',
    pathMatch: 'full',
  },
  {
    path: 'accounts',
    loadChildren: () => import('./modules/accounts/accounts.module').then(m => m.AccountsModule),
  },
  {
    path: 'budget',
    loadChildren: () => import('./modules/budget/budget.module').then(m => m.BudgetModule),
  },
  {
    path: 'calendar',
    loadChildren: () => import('./modules/calendar/calendar.module').then(m => m.CalendarModule),
  },
  {
    path: 'reports',
    loadChildren: () => import('./modules/reports/reports.module').then(m => m.ReportsModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true, enableTracing: false })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
