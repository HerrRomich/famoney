import { NgModule } from '@angular/core';
import {
  MatToolbarModule,
  MatIconModule,
  MatButtonModule,
  MatTabsModule,
  MatMenuModule,
  MatIconRegistry,
  MatBadgeModule,
  MatDialogModule,
  MatAutocompleteModule,
  MatFormFieldModule,
  MatChipsModule,
  MatTooltipModule,
  MatListModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MatInputModule,
  MAT_DATE_LOCALE
} from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DomSanitizer } from '@angular/platform-browser';
import { OverlayModule } from '@angular/cdk/overlay';
import { PortalModule } from '@angular/cdk/portal';
import { EcoFabSpeedDialModule } from '@ecodev/fab-speed-dial';

const MATERIAL_MODULES = [
  MatToolbarModule,
  MatTabsModule,
  MatIconModule,
  MatMenuModule,
  MatButtonModule,
  MatDatepickerModule,
  MatFormFieldModule,
  FlexLayoutModule,
  MatBadgeModule,
  MatDialogModule,
  MatAutocompleteModule,
  MatFormFieldModule,
  MatChipsModule,
  MatTooltipModule,
  MatListModule,
  MatInputModule,
  MatNativeDateModule,
  MatAutocompleteModule,
  OverlayModule,
  PortalModule,
  EcoFabSpeedDialModule
];

@NgModule({
  imports: MATERIAL_MODULES,
  exports: MATERIAL_MODULES,
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'de-DE'}
  ]
})
export class MaterailModule {
  constructor(private matIconRegistry: MatIconRegistry, private domSanitzer: DomSanitizer) {
    this.matIconRegistry.addSvgIcon('menu-down', this.domSanitzer.bypassSecurityTrustResourceUrl('assets/menu-down.svg'));
  }
}
