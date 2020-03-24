import { Component, ViewEncapsulation, ViewChild, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { EntryCategoryService } from '@famoney-shared/services/entry-category.service';
import { AUTOCOMPLETE_OPTION_HEIGHT, MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { EntryCategoriesDto, ExpenseCategoryDto } from '@famoney-apis/data-directory';
import { FormControl } from '@angular/forms';
import { debounceTime, switchMap, map, startWith, tap, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-entry-item',
  templateUrl: 'entry-item.component.html',
  styleUrls: ['entry-item.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EntryItemComponent {
  entryCategoryCtrl: FormControl = new FormControl();
  entryCategories$: Observable<EntryCategoriesDto>;

  @ViewChild(MatAutocomplete)
  categoryAutocomplete: MatAutocomplete;

  constructor(public entryCategoriesService: EntryCategoryService) {
    this.entryCategories$ = this.entryCategoryCtrl.valueChanges.pipe(debounceTime(350)).pipe(
      startWith(''),
      switchMap(filterValue =>
        entryCategoriesService.entryCategories$.pipe(
          map(entryCategories => {
            const filter = new RegExp(filterValue, 'i');
            return {
              expenses: this.filterCategories(entryCategories.expenses, filter),
              incomes: this.filterCategories(entryCategories.incomes, filter)
            } as EntryCategoriesDto;
          })
        )
      ),
      tap(() => {
        this.categoryAutocomplete._setVisibility();
      })
    );
  }

  filterCategories(entryCategories: ExpenseCategoryDto[], filter: RegExp) {
    return entryCategories.reduce((filteredCategories, entryCategory) => {
      const subCategories = this.filterCategories(entryCategory.children, filter);
      if (filter.test(entryCategory.name) || subCategories.length > 0) {
        filteredCategories.push({
          ...entryCategory,
          name: entryCategory.name.replace(filter, subString => `<mark>${subString}</mark>`),
          children: subCategories
        });
      }
      return filteredCategories;
    }, new Array<ExpenseCategoryDto>());
  }
}
