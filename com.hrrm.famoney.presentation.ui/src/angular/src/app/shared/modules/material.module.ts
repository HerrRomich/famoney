import { NgModule } from '@angular/core';
import { MatToolbarModule, MatIconModule, MatButtonModule, MatTabsModule, MatMenuModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';

const MATERIAL_MODULES = [MatToolbarModule, MatTabsModule, MatIconModule, MatMenuModule, MatButtonModule, FlexLayoutModule];

@NgModule({
  imports: MATERIAL_MODULES,
  exports: MATERIAL_MODULES
})
export class MaterailModule {}
