import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { HttpClientModule } from '@angular/common/http';
import { FocusHighlightDirective } from '../directives/focus-highlight.directive';

const ANGULAR_MODULES = [
  FormsModule,
  CommonModule,
  RouterModule,
  ReactiveFormsModule,
  ScrollingModule,
  HttpClientModule
];

@NgModule({
  declarations: [FocusHighlightDirective],
  imports: ANGULAR_MODULES,
  exports: [...ANGULAR_MODULES, FocusHighlightDirective]
})
export class AngularModule {}
