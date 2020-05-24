import { NgModule } from '@angular/core';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { AngularModule } from '@famoney-shared/modules/angular.module';
import { AccountTableComponent } from './pages/accounts/components/account-table.component';
import { AccountsService } from './/services/accounts.service';
import { AccountTagsPopupComponent } from './pages/accounts/components/account-tags-popup.component';
import { RouterTabModule } from 'src/app/shared/router-tab/router-tab.module';
import { AccountEntryDialogComponent } from './pages/accounts/components/account-entry-dialog.component';
import { AccountsGuard } from './services/accounts.guard';
import { OverlayModule } from '@angular/cdk/overlay';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EcoFabSpeedDialModule } from '@ecodev/fab-speed-dial';
import { DomSanitizer } from '@angular/platform-browser';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MonthPickerModule } from '@famoney-shared/components/month-picker.module';
import { EntryItemComponent } from './pages/accounts/components/entry-item.component';
import { NgxMaskModule, IConfig } from 'ngx-mask';
import { SharedModule } from '@famoney-shared/modules/shared.module';

export const options: Partial<IConfig> | (() => Partial<IConfig>) = {
  decimalMarker: '.',
};

@NgModule({
  declarations: [
    AccountsComponent,
    AccountTableComponent,
    AccountTagsPopupComponent,
    AccountEntryDialogComponent,
    EntryItemComponent,
  ],
  entryComponents: [AccountTagsPopupComponent, AccountEntryDialogComponent, EntryItemComponent],
  providers: [AccountsService, AccountsGuard],
  imports: [
    AngularModule,
    SharedModule,
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
    MonthPickerModule,
    NgxMaskModule.forRoot(options),
  ],
  exports: [AccountsRoutingModule],
})
export class AccountsModule {
  constructor(private matIconRegistry: MatIconRegistry, private domSanitzer: DomSanitizer) {
    this.matIconRegistry.addSvgIcon(
      'menu-down',
      this.domSanitzer.bypassSecurityTrustResourceUrl('assets/menu-down.svg'),
    );
  }
}
