import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountTableComponent } from './pages/accounts/components/account-table.component';

const routes: Routes = [
  {
    path: '',
    component: AccountsComponent,
    children: [
      {
        path: ':accountId',
        component: AccountTableComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)]
})
export class AccountsRoutingModule {}
