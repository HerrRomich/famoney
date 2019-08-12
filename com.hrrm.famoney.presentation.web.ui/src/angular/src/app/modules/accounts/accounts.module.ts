import { NgModule } from '@angular/core';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { MaterailModule } from '@famoney-shared/material.module';
import { ApiModule as AccountsApiModule } from '@famoney-apis/accounts';
import { AngularModule } from '@famoney-shared/angular.module';
import { AccountTableComponent } from './pages/accounts/components/account-table.component';
import { AccountsService } from './pages/accounts/accounts.service';
import { AccountTagsPopupComponent } from './pages/accounts/components/account-tags-popup.component';
import { RouterTabModule } from 'src/app/shared/router-tab/router-tab.module';

@NgModule({
  declarations: [AccountsComponent, AccountTableComponent, AccountTagsPopupComponent],
  entryComponents: [AccountTagsPopupComponent],
  providers: [AccountsService],
  imports: [AngularModule, MaterailModule, RouterTabModule],
  exports: [AccountsRoutingModule]
})
export class AccountsModule {}
