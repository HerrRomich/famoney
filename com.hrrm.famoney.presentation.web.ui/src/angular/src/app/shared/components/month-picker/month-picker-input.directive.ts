import { Directive, Input, forwardRef } from '@angular/core';
import { DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';
import {
  MatDatepickerInput,
  MAT_DATEPICKER_VALUE_ACCESSOR,
  MAT_DATEPICKER_VALIDATORS,
  MatDatepicker,
} from '@angular/material/datepicker';
import { MAT_INPUT_VALUE_ACCESSOR } from '@angular/material/input';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';
import { NG_VALUE_ACCESSOR, NG_VALIDATORS } from '@angular/forms';

export const MY_FORMATS = {
  parse: {
    dateInput: 'MMM YYYY',
  },
  display: {
    dateInput: 'MMM YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

export const APP_MONTHPICKER_VALUE_ACCESSOR: any = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => MonthpickerInputDirective),
  multi: true,
};

export const APP_MONTHPICKER_VALIDATORS: any = {
  provide: NG_VALIDATORS,
  useExisting: forwardRef(() => MonthpickerInputDirective),
  multi: true,
};

@Directive({
  selector: 'input[appMonthpicker]',
  providers: [
    APP_MONTHPICKER_VALUE_ACCESSOR,
    APP_MONTHPICKER_VALIDATORS,
    { provide: MAT_INPUT_VALUE_ACCESSOR, useExisting: MonthpickerInputDirective },
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS],
    },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { strict: true } },
  ],
  host: {
    'aria-haspopup': 'dialog',
    '[attr.aria-owns]': '(_datepicker?.opened && _datepicker.id) || null',
    '[attr.min]': 'min ? _dateAdapter.toIso8601(min) : null',
    '[attr.max]': 'max ? _dateAdapter.toIso8601(max) : null',
    '[disabled]': 'disabled',
    '(input)': '_onInput($event.target.value)',
    '(change)': '_onChange()',
    '(blur)': '_onBlur()',
    '(keydown)': '_onKeydown($event)',
  },
  exportAs: 'appMonthpickerInput',
})
export class MonthpickerInputDirective<D> extends MatDatepickerInput<D> {
  @Input()
  set appMonthpicker(value: MatDatepicker<D>) {
    this.matDatepicker = value;
  }
}
