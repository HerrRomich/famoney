import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/accounts',
    pathMatch: 'full'
  },
  {
    path: 'accounts',
    loadChildren: './modules/accounts/accounts.module#AccountsModule'
  },
  {
    path: 'budget',
    loadChildren: './modules/budget/budget.module#BudgetModule'
  },
  {
    path: 'calendar',
    loadChildren: './modules/calendar/calendar.module#CalendarModule'
  },
  {
    path: 'reports',
    loadChildren: './modules/reports/reports.module#ReportsModule'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true, enableTracing: false })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
