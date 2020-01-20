import { NgModule } from '@angular/core';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { AngularModule } from '@famoney-shared/angular.module';
import { AccountTableComponent } from './pages/accounts/components/account-table.component';
import { AccountsService } from './pages/accounts/accounts.service';
import { AccountTagsPopupComponent } from './pages/accounts/components/account-tags-popup.component';
import { RouterTabModule } from 'src/app/shared/router-tab/router-tab.module';
import { AccountEntryDialogComponent } from './pages/accounts/components/account-entry-dialog.component';
import { AccountsGuard } from './pages/accounts/accounts.guard';
import { AccountsApiModule } from '@famoney-apis/accounts-api.module';
import { OverlayModule } from '@angular/cdk/overlay';
import {
  MatIconModule,
  MatBadgeModule,
  MatMenuModule,
  MatTooltipModule,
  MatListModule,
  MatChipsModule,
  MatAutocompleteModule,
  MatFormFieldModule,
  MatDatepickerModule,
  MatIconRegistry,
  MatButtonModule,
  MatDialogModule,
  MatInputModule,
  MatButtonToggleModule
} from '@angular/material';
import { EcoFabSpeedDialModule } from '@ecodev/fab-speed-dial';
import { DomSanitizer } from '@angular/platform-browser';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MonthPickerModule } from '@famoney-components/month-picker.module';
import { EntryItemComponent } from './pages/accounts/components/entry-item.component';

@NgModule({
  declarations: [
    AccountsComponent,
    AccountTableComponent,
    AccountTagsPopupComponent,
    AccountEntryDialogComponent,
    EntryItemComponent
  ],
  entryComponents: [AccountTagsPopupComponent, AccountEntryDialogComponent, EntryItemComponent],
  providers: [AccountsService, AccountsGuard],
  imports: [
    AngularModule,
    MatIconModule,
    MatBadgeModule,
    MatMenuModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatTooltipModule,
    MatListModule,
    MatChipsModule,
    MatAutocompleteModule,
    MatInputModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatDialogModule,
    EcoFabSpeedDialModule,
    OverlayModule,
    FlexLayoutModule,
    RouterTabModule,
    AccountsApiModule,
    MonthPickerModule
  ],
  exports: [AccountsRoutingModule]
})
export class AccountsModule {
  constructor(private matIconRegistry: MatIconRegistry, private domSanitzer: DomSanitizer) {
    this.matIconRegistry.addSvgIcon(
      'menu-down',
      this.domSanitzer.bypassSecurityTrustResourceUrl('assets/menu-down.svg')
    );
  }
}
