import { NgModule } from '@angular/core';
import { MatDatepickerModule, MatFormFieldModule, MatInputModule, MatButtonModule } from '@angular/material';
import { MonthPickerComponent } from './month-picker/month-picker.component';
import { MonthCalendarHeaderComponent } from './month-picker/month-calendar-header.component';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TextMaskModule } from 'angular2-text-mask';
import { MonthpickerInputDirective } from './month-picker/month-picker-input.directive';

@NgModule({
  entryComponents: [MonthPickerComponent, MonthCalendarHeaderComponent],
  declarations: [MonthPickerComponent, MonthCalendarHeaderComponent, MonthpickerInputDirective],
  imports: [
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FlexLayoutModule,
    FormsModule,
    TextMaskModule,
    ReactiveFormsModule
  ],
  exports: [
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    MonthPickerComponent,
    MonthpickerInputDirective
  ]
})
export class MonthPickerModule {}
