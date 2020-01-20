import { Component, OnInit, Optional, Inject } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import * as moment from 'moment';
import { MAT_DATE_LOCALE } from '@angular/material';

@Component({
  selector: 'app-account-entry-dialog',
  templateUrl: 'account-entry-dialog.component.html',
  styleUrls: ['account-entry-dialog.component.scss']
})
export class AccountEntryDialogComponent implements OnInit {
  entry = new FormGroup({
    entryDate: new FormControl(moment()),
    bookingDate: new FormControl(),
    budgetMonth: new FormControl()
  });

  entryCategories: any;

  constructor(@Optional() @Inject(MAT_DATE_LOCALE) private dateLocale: string) {}

  ngOnInit() {
  }

  getEntryDate(format: string) {
    const entryDate = this.entry.get('entryDate');
    if (entryDate && entryDate.value && moment.isMoment(entryDate.value)) {
      return entryDate.value.locale(this.dateLocale).format(format);
    }
  }
}
