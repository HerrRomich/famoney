import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountTableComponent } from './pages/accounts/components/account-table.component';
import { AccountsGuard } from './services/accounts.guard';

const routes: Routes = [
  {
    path: '',
    component: AccountsComponent,
    children: [
      {
        path: '',
        redirectTo: '0',
        pathMatch: 'full',
      },
      {
        path: ':accountId',
        component: AccountTableComponent,
        canActivate: [AccountsGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
})
export class AccountsRoutingModule {}
