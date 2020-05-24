import { Component, ViewEncapsulation, Input, OnInit } from '@angular/core';
import {
  EntryCategoryService,
  EntryCategory,
  FlatEntryCategory,
} from '@famoney-shared/services/entry-category.service';
import { Observable, EMPTY, forkJoin } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { debounceTime, switchMap, map, startWith, shareReplay } from 'rxjs/operators';
import { EntryCategoryDto } from '@famoney-apis/data-directory/model/entry-category.dto';
import { TranslateService } from '@ngx-translate/core';
import { getLocaleNumberSymbol, NumberSymbol } from '@angular/common';

interface EntryCategoryWithFilterOption extends FlatEntryCategory {
  optionName: string;
}

interface EntryCategoriesForVisualisation {
  flatEntryCategories: Map<number, FlatEntryCategory>;
  expenses: EntryCategoryWithFilterOption[];
  incomes: EntryCategoryWithFilterOption[];
}

@Component({
  selector: 'fm-entry-item',
  templateUrl: 'entry-item.component.html',
  styleUrls: ['entry-item.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class EntryItemComponent implements OnInit {
  @Input()
  formGroup?: FormGroup;

  entryCategories$: Observable<EntryCategoriesForVisualisation> = EMPTY;

  constructor(private entryCategoriesService: EntryCategoryService, private translateService: TranslateService) {}

  ngOnInit() {
    const entryCategoryValueChanges = this.formGroup?.get('category')?.valueChanges ?? EMPTY;
    this.entryCategories$ = entryCategoryValueChanges.pipe(debounceTime(350)).pipe(
      startWith(''),
      map(value => (typeof value === 'string' ? value : '')),
      switchMap(filterValue =>
        this.entryCategoriesService.entryCategoriesForVisualisation$.pipe(
          map(entryCategories => {
            const filter = new RegExp(filterValue, 'i');
            return {
              flatEntryCategories: entryCategories.flatEntryCategories,
              expenses: this.filterCategories(filter, entryCategories.expenses),
              incomes: this.filterCategories(filter, entryCategories.incomes),
            };
          }),
        ),
      ),
    );
  }

  filterCategories(
    filter: RegExp,
    entryCategories?: EntryCategoryDto[],
    level = 1,
    path = '',
  ): EntryCategoryWithFilterOption[] {
    const filteredEntryCategories = entryCategories?.reduce((filteredCategories, entryCategory) => {
      const subCategories = this.filterCategories(
        filter,
        entryCategory.children,
        level + 1,
        path + ' -> ' + entryCategory.name,
      );
      if (filter.test(entryCategory.name) || subCategories.length > 0) {
        filteredCategories.push({
          id: entryCategory.id,
          name: entryCategory.name,
          optionName:
            entryCategory.name.match(filter)?.join().length ?? 0 > 0
              ? entryCategory.name.replace(filter, subString => `<mark>${subString}</mark>`)
              : entryCategory.name,
          path: path.substr(4),
          level: level,
          type: entryCategory.type,
        });
        filteredCategories.push(...subCategories);
      }
      return filteredCategories;
    }, new Array<EntryCategoryWithFilterOption>());
    return filteredEntryCategories ?? [];
  }

  getCategoryName(categories: Map<number, EntryCategory>) {
    return (categoryId: number) => categories.get(categoryId)?.name;
  }

  getCategoryErrorMessage$() {
    const categoryControl = this.formGroup?.get('categoryId');
    if (categoryControl?.getError('required')) {
      return this.translateService.get('accounts.entryItem.fields.category.errors.required');
    }
  }

  getCategoryPath$() {
    const categoryId = this.formGroup?.get('categoryId')?.value as number;
    return this.entryCategories$.pipe(
      map(entryCategories => entryCategories.flatEntryCategories.get(categoryId)?.path),
    );
  }

  getAmountErrorMessage$() {
    const amountControl = this.formGroup?.get('amount');
    if (amountControl?.hasError('required')) {
      return this.translateService.get('accounts.entryItem.fields.amount.errors.required');
    } else if (amountControl?.hasError('wrongFormat')) {
      return this.translateService.get('accounts.entryItem.fields.amount.errors.wrongFormat');
    } else if (amountControl?.hasError('zeroValue')) {
      return this.translateService.get('accounts.entryItem.fields.amount.errors.zeroValue');
    }
  }

  getDecimalSeparator() {
    return getLocaleNumberSymbol(
      this.translateService.currentLang ?? this.translateService.defaultLang,
      NumberSymbol.Decimal,
    );
  }

  getEntryItemClass$() {
    const categoryId = this.formGroup?.get('categoryId')?.value as number;
    return this.entryCategories$.pipe(
      map(entryCategories => {
        const entryItemCategoryType = entryCategories.flatEntryCategories.get(categoryId)?.type;
        if (entryItemCategoryType === 'expense') {
          return 'fm-expense-category';
        } else if (entryItemCategoryType === 'income') {
          return 'fm-income-category';
        }
      }),
    );
  }
}
