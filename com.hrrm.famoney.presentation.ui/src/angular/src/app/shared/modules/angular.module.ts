import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ScrollingModule } from '@angular/cdk/scrolling';

const ANGULAR_MODULES = [
  FormsModule,
  CommonModule,
  RouterModule,
  ReactiveFormsModule,
  ScrollingModule
];

@NgModule({
  imports: ANGULAR_MODULES,
  exports: ANGULAR_MODULES
})
export class AngularModule {}
