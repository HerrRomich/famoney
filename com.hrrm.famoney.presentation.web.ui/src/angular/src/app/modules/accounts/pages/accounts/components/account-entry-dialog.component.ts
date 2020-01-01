import { Component, OnInit, ViewChild } from '@angular/core';
import { MatFormField } from '@angular/material';
import { QueryBindingType } from '@angular/compiler/src/core';

@Component({
  selector: 'app-account-entry-dialog',
  templateUrl: 'account-entry-dialog.component.html',
  styleUrls: ['account-entry-dialog.component.scss']
})
export class AccountEntryDialogComponent implements OnInit {

  @ViewChild('entryDateField', {static: true}) entryDateField: MatFormField;

  entryDatePlaceholder: Date;

  bookingDatePlaceholder: Date;

  entryCategories: any;

  ngOnInit() {
    this.entryDatePlaceholder = new Date();
    this.bookingDatePlaceholder = this.entryDatePlaceholder;
  }
}
