import { NgModule } from '@angular/core';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { MaterailModule } from '@famoney-shared/material.module';
import { AngularModule } from '@famoney-shared/angular.module';
import { AccountTableComponent } from './pages/accounts/components/account-table.component';
import { AccountsService } from './pages/accounts/accounts.service';
import { AccountTagsPopupComponent } from './pages/accounts/components/account-tags-popup.component';
import { RouterTabModule } from 'src/app/shared/router-tab/router-tab.module';
import { AccountEntryDialogComponent } from './pages/accounts/components/account-entry-dialog.component';
import { AccountsGuard } from './pages/accounts/accounts.guard';
import { NgxMaskModule, IConfig } from 'ngx-mask';

export const options: Partial<IConfig> | (() => Partial<IConfig>) = {};

@NgModule({
  declarations: [AccountsComponent, AccountTableComponent, AccountTagsPopupComponent, AccountEntryDialogComponent],
  entryComponents: [AccountTagsPopupComponent, AccountEntryDialogComponent],
  providers: [AccountsService, AccountsGuard],
  imports: [AngularModule, MaterailModule, RouterTabModule,
    NgxMaskModule.forRoot(options)],
  exports: [AccountsRoutingModule],
})
export class AccountsModule {}
