import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { NgxMaskModule } from 'ngx-mask';

const ANGULAR_MODULES = [
  FormsModule,
  CommonModule,
  RouterModule,
  ReactiveFormsModule,
  ScrollingModule,
  NgxMaskModule,
];

@NgModule({
  imports: ANGULAR_MODULES,
  exports: ANGULAR_MODULES
})
export class AngularModule {}
