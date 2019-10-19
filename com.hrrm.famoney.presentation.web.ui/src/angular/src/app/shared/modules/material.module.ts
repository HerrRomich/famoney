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
  MatListModule
} from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DomSanitizer } from '@angular/platform-browser';
import { OverlayModule } from '@angular/cdk/overlay';
import { PortalModule } from '@angular/cdk/portal';

const MATERIAL_MODULES = [
  MatToolbarModule,
  MatTabsModule,
  MatIconModule,
  MatMenuModule,
  MatButtonModule,
  FlexLayoutModule,
  MatBadgeModule,
  MatDialogModule,
  MatAutocompleteModule,
  MatFormFieldModule,
  MatChipsModule,
  MatTooltipModule,
  MatListModule,
  OverlayModule,
  PortalModule
];

@NgModule({
  imports: MATERIAL_MODULES,
  exports: MATERIAL_MODULES
})
export class MaterailModule {
  constructor(private matIconRegistry: MatIconRegistry, private domSanitzer: DomSanitizer) {
    this.matIconRegistry.addSvgIcon('menu-down', this.domSanitzer.bypassSecurityTrustResourceUrl('/assets/menu-down.svg'));
  }
}
