import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { MaterailModule } from 'src/app/shared/modules/material.module';
import { ApiModule as AccountsApiModule } from '@famoney-apis/accounts';

@NgModule({
  declarations: [AccountsComponent],
  imports: [CommonModule, MaterailModule],
  exports: [AccountsRoutingModule]
})
export class AccountsModule {}
