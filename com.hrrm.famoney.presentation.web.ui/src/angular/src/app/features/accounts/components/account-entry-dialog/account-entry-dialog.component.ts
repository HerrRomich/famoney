import { AfterViewInit, Component, Inject, OnInit, Optional } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AccountsApiService, ApiErrorDto, EntryDataDto, EntryItemDataDto, MovementDto } from '@famoney-apis/accounts';
import { EntryCategoryService, FlatEntryCategoryObject } from '@famoney-shared/services/entry-category.service';
import { LocaleService } from '@famoney-shared/services/locale.service';
import { ParseNumberService } from '@famoney-shared/services/parse-numbers.service';
import { TranslateService } from '@ngx-translate/core';
import { NotificationsService } from 'angular2-notifications';
import * as moment from 'moment';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { AccountEntry, EntryDialogData, EntryItem } from './../../models/account-entry.model';

@Component({
  selector: 'fm-account-entry-dialog',
  templateUrl: 'account-entry-dialog.component.html',
  styleUrls: ['account-entry-dialog.component.scss'],
})
export class AccountEntryDialogComponent implements OnInit {
  entryForm: FormGroup;
  accountEntry$: Observable<AccountEntry>;
  comulatedSum$: Observable<{ amount: number }> = EMPTY;
  extendedDate: string | undefined;
  extendedEntry: string | undefined;
  numberFormat: Intl.NumberFormat;

  constructor(
    private dialogRef: MatDialogRef<AccountEntryDialogComponent, MovementDto>,
    private formBuilder: FormBuilder,
    private accountsApiService: AccountsApiService,
    private entryCategoriesService: EntryCategoryService,
    @Optional() @Inject(MAT_DATE_LOCALE) private dateLocale: string,
    private translateService: TranslateService,
    private notificationsService: NotificationsService,
    @Inject(MAT_DIALOG_DATA) private data: EntryDialogData,
    localeService: LocaleService,
    private parseNumberService: ParseNumberService,
  ) {
    this.numberFormat = new Intl.NumberFormat(localeService.locale, {
      maximumFractionDigits: 2,
      useGrouping: false,
    });
    this.entryForm = this.formBuilder.group({
      entryDate: [moment(), [Validators.required]],
      bookingDate: undefined,
      budgetPeriod: undefined,
      entryItems: this.formBuilder.array([this.addEntryItemFormGroup()]),
    });
    this.accountEntry$ = of(this.data.entryData).pipe(
      switchMap(entryData =>
        this.entryCategoriesService.entryCategoriesForVisualisation$.pipe(
          map(entryCategories => [entryData, entryCategories] as const),
        ),
      ),
      tap(([entryData]) => {
        this.extendedDate = entryData?.bookingDate || entryData?.budgetPeriod ? 'extended-date' : undefined;
        this.extendedEntry = (entryData?.entryItems?.length ?? 0) > 1 ? 'extended-entry' : undefined;
      }),
      map(([entryData, entryCategories]) => {
        const accountEntry: AccountEntry = {
          entryDate: entryData?.date ? moment(entryData?.date, 'YYYY-MM-DD') : moment(),
          bookingDate: entryData?.bookingDate ? moment(entryData?.bookingDate, 'YYYY-MM-DD') : undefined,
          budgetPeriod: entryData?.budgetPeriod ? moment(entryData?.budgetPeriod, 'YYYY-MM') : undefined,
          entryItems: entryData
            ? entryData.entryItems.map(entryItem =>
                this.createEntryItem(entryItem, entryCategories.flatEntryCategories.get(entryItem.categoryId)),
              )
            : [],
        };
        return accountEntry;
      }),
      tap(accountEntry => {
        if (accountEntry.entryItems.length > 1) {
          this.entryForm.setControl(
            'entryItems',
            this.formBuilder.array(accountEntry.entryItems.map(() => this.addEntryItemFormGroup())),
          );
        }
        this.entryForm.patchValue(accountEntry);
      }),
    );
  }

  private addEntryItemFormGroup() {
    return this.formBuilder.group({
      categoryId: [undefined, Validators.required],
      amount: [undefined, [Validators.required, this.validateAmountNotZero]],
      comments: undefined,
    });
  }

  private createEntryItem(entryItem: EntryItemDataDto, flatEntryCategory?: FlatEntryCategoryObject): EntryItem {
    const entryItemAmount = entryItem?.amount;
    const sign = flatEntryCategory?.getCategorySign();
    const amount = entryItemAmount && sign ? entryItemAmount * sign : undefined;
    return {
      categoryId: entryItem.categoryId,
      amount: amount ? this.numberFormat.format(amount) : '',
      comments: entryItem.comments,
    };
  }

  ngOnInit(): void {
    this.dialogRef.keydownEvents().subscribe(event => {
      if (event.key === 'Escape') {
        this.onCancel();
      }
    });
    this.dialogRef.backdropClick().subscribe(() => {
      this.onCancel();
    });
  }

  onCancel() {
    this.dialogRef.close();
  }

  getEntryDate(format: string) {
    const entryDateControl = this.entryForm?.get('entryDate');
    const entryDate = this.getEntryDateOrDefault(entryDateControl?.value);
    return entryDate.locale(this.dateLocale).format(format);
  }

  private getEntryDateOrDefault(entryDate?: moment.Moment) {
    return entryDate ?? moment();
  }

  getEntryItems() {
    const entryItems = this.entryForm?.get('entryItems');
    if (entryItems instanceof FormArray) {
      return entryItems;
    }
  }

  addEntryItem() {
    this.getEntryItems()?.push(this.addEntryItemFormGroup());
  }

  deleteEntryItem(entryItemIndex: number) {
    this.getEntryItems()?.removeAt(entryItemIndex);
  }

  validateAmountNotZero = (control: AbstractControl): ValidationErrors => {
    const amount = this.parseNumberService.parse(control.value);
    if (Number.isNaN(amount)) {
      return { wrongFormat: 'Should be number!' };
    } else if (amount === 0) {
      return { zeroValue: 'Should be not Zero!' };
    } else {
      return {};
    }
  };

  getEntryDateError$() {
    const entryDateControl = this.entryForm?.get('entryDate');
    if (entryDateControl?.hasError('matDatepickerParse')) {
      return this.translateService.get('accounts.entryDialog.fields.entryDate.errors.invalid');
    } else if (entryDateControl?.getError('required')) {
      return this.translateService.get('accounts.entryDialog.fields.entryDate.errors.required');
    }
  }

  submit() {
    const { accountId, movementId } = this.data;
    let storeOperator =
      typeof movementId === 'undefined'
        ? (entryData: EntryDataDto) => this.accountsApiService.addMovement(accountId, entryData, 'body')
        : (entryData: EntryDataDto) => this.accountsApiService.changeMovement(accountId, movementId, entryData, 'body');
    of(this.entryForm?.value as AccountEntry)
      .pipe(
        switchMap(accountEntry =>
          this.entryCategoriesService.entryCategoriesForVisualisation$.pipe(
            map(entryCategories => [accountEntry, entryCategories] as const),
          ),
        ),
        map(([accountEntry, entryCategories]) => {
          const entryItems = accountEntry.entryItems.map(entryItem => {
            const entryCategory = entryCategories.flatEntryCategories.get(entryItem.categoryId);
            return {
              categoryId: entryItem.categoryId,
              amount: (entryCategory?.getCategorySign() ?? 0) * this.parseNumberService.parse(entryItem.amount),
              comments: entryItem.comments,
            };
          });
          const entry: EntryDataDto = {
            type: 'entry',
            date: this.getEntryDateOrDefault(accountEntry.entryDate).format('YYYY-MM-DD'),
            bookingDate: accountEntry.bookingDate?.format('YYYY-MM-DD'),
            budgetPeriod: accountEntry.budgetPeriod?.format('YYYY-MM'),
            entryItems: entryItems,
            amount: entryItems.reduce((amount, entryItem) => amount + entryItem.amount, 0),
          };
          return entry;
        }),
        switchMap(storeOperator),
        tap(movement => this.dialogRef.close(movement)),
        catchError((err: ApiErrorDto) => {
          this.notificationsService.error('Error', err.description);
          return EMPTY;
        }),
      )
      .subscribe();
  }
}
