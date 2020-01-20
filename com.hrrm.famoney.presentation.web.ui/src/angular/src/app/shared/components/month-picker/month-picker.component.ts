import { Component, OnInit } from '@angular/core';
import { MonthCalendarHeaderComponent } from './month-calendar-header.component';
import { DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS, MatDatepicker } from '@angular/material';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';

export const MY_FORMATS = {
  parse: {
    dateInput: 'MMM YYYY'
  },
  display: {
    dateInput: 'MMM YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY'
  }
};

@Component({
  selector: 'app-monthpicker',
  template: '',
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { strict: true } }
  ]
})
export class MonthPickerComponent<D> extends MatDatepicker<D> implements OnInit {

  ngOnInit() {
    this.startView = 'year';
    this.calendarHeaderComponent = MonthCalendarHeaderComponent;
  }

  _selectMonth(normalizedMonth: D) {
    super._selectMonth(normalizedMonth);
    this.select(normalizedMonth);
    this.close();
  }

}
