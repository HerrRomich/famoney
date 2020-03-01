import { Component, OnInit, Optional, Inject } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import * as moment from 'moment';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MovementDto } from '@famoney-apis/accounts';

@Component({
  selector: 'app-account-entry-dialog',
  templateUrl: 'account-entry-dialog.component.html',
  styleUrls: ['account-entry-dialog.component.scss']
})
export class AccountEntryDialogComponent implements OnInit {
  entry: FormGroup;

  entryCategories: any;

  constructor(
    @Optional() @Inject(MAT_DATE_LOCALE) private dateLocale: string,
    @Inject(MAT_DIALOG_DATA) accountEntry: MovementDto
  ) {
    this.entry = new FormGroup({
      entryDate: new FormControl(accountEntry.date),
      bookingDate: new FormControl(),
      budgetMonth: new FormControl()
    });
  }

  ngOnInit() {}

  getEntryDate(format: string) {
    const entryDate = this.entry.get('entryDate');
    if (entryDate && entryDate.value && moment.isMoment(entryDate.value)) {
      return entryDate.value.locale(this.dateLocale).format(format);
    }
  }
}
