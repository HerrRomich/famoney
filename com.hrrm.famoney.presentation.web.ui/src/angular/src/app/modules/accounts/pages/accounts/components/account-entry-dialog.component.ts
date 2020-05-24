import { Component, Optional, Inject, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import * as moment from 'moment';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EntryDataDto, EntryItemDataDto } from '@famoney-apis/accounts';
import { EntryCategoryService, EntryCategory } from '@famoney-shared/services/entry-category.service';
import { Subscription, Observable, empty, EMPTY } from 'rxjs';
import { tap, map, startWith } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { AccountEntry } from './account-entry.data';

@Component({
  selector: 'fm-account-entry-dialog',
  templateUrl: 'account-entry-dialog.component.html',
  styleUrls: ['account-entry-dialog.component.scss'],
})
export class AccountEntryDialogComponent {
  entryForm: FormGroup;
  comulatedSum$: Observable<{ amount: number }> = EMPTY;

  constructor(
    private dialogRef: MatDialogRef<AccountEntryDialogComponent, EntryDataDto>,
    private formBuilder: FormBuilder,
    @Optional() @Inject(MAT_DATE_LOCALE) private dateLocale: string,
    private translateService: TranslateService,
    @Inject(MAT_DIALOG_DATA) entryData?: EntryDataDto,
  ) {
    this.entryForm = this.formBuilder.group({
      entryDate: entryData?.date,
      bookingDate: entryData?.bookingDate,
      budgetMonth: entryData?.date,
      entryItems: this.formBuilder.array(
        entryData?.entryItems.map(this.addEntryItemFromGroup) ?? [this.addEntryItemFromGroup()],
      ),
    });
  }

  private addEntryItemFromGroup(entryItem?: EntryItemDataDto) {
    return this.formBuilder.group({
      categoryId: [entryItem?.categoryId, Validators.required],
      amount: [entryItem?.amount, [Validators.required, this.validateAmountNotZero]],
      comments: entryItem?.comments,
    });
  }

  getEntryDate(format: string) {
    const entryDateControl = this.entryForm.get('entryDate')!;
    const entryDate = this.getEntryDateOrDefault(entryDateControl.value);
    return entryDate.locale(this.dateLocale).format(format);
  }

  private getEntryDateOrDefault(entryDate? : moment.Moment) {
    return entryDate ?? moment();
  }

  addEntryItem() {
    const entryItems = this.entryForm.get('entryItems') as FormArray;
    entryItems.push(this.addEntryItemFromGroup());
  }

  deleteEntryItem(entryItemIndex: number) {
    const entryItems = this.entryForm.get('entryItems') as FormArray;
    entryItems.removeAt(entryItemIndex);
  }

  validateAmountNotZero(control: AbstractControl): ValidationErrors {
    const amount: number | undefined = control.value;
    if (Number.isNaN(amount ?? NaN)) {
      return { wrongFormat: 'Should be number!' };
    } else if (amount === 0) {
      return { zeroValue: 'Should be not Zero!' };
    } else {
      return {};
    }
  }

  getEntryDateError$() {
    const entryDateControl = this.entryForm.get('entryDate');
    if (entryDateControl?.hasError('matDatepickerParse')) {
      return this.translateService.get('accounts.entryDialog.fields.entryDate.errors.invalid');
    }
  }

  submit() {
    const accountEentry: AccountEntry = this.entryForm.value;
    this.dialogRef.close({
      type: 'entry',
      date: this.getEntryDateOrDefault(accountEentry.entryDate).format('YYYY-MM-DD'),
      bookingDate: accountEentry.bookingDate?.format('YYYY-MM-DD'),
      budgetPeriod: accountEentry.budgetPeriod?.format('YYYY-MM'),
      entryItems: accountEentry.entryItems.map(entryItem => ({
        categoryId: entryItem.categoryId,
        amount: entryItem.amount,
        comments: entryItem.comments,
      })),
      amount: accountEentry.entryItems.reduce((amount, entryItem) => amount + entryItem.amount, 0),
    });
  }
}
