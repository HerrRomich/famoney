import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MonthPickerComponent } from './month-picker/month-picker.component';
import { MonthCalendarHeaderComponent } from './month-picker/month-calendar-header.component';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
    ReactiveFormsModule,
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
    MonthpickerInputDirective,
  ],
})
export class MonthPickerModule {}
