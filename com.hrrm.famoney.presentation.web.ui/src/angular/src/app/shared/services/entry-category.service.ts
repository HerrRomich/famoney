import { Injectable } from '@angular/core';
import { DataDirectoryApiService, EntryCategoriesDto } from '@famoney-apis/data-directory';
import { Observable, BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap, shareReplay, catchError, map } from 'rxjs/operators';
import { NotificationsService } from 'angular2-notifications';
import { EntryCategoryDto } from '@famoney-apis/data-directory/model/entry-category.dto';

export interface EntryCategory {
  id: number;
  type: 'income' | 'expense';
  name: string;
}

export interface HierarchicalEntryCategory {
  id: number;
  type: 'income' | 'expense';
  name: string;
  children: HierarchicalEntryCategory[];
}

export interface FlatEntryCategory extends EntryCategory {
  path: string;
  level: number;
}

interface EntryCategories {
  flatEntryCategories: Map<number, FlatEntryCategory>;
  expenses: EntryCategory[];
  incomes: EntryCategory[];
}

@Injectable({
  providedIn: 'root',
})
export class EntryCategoryService {
  readonly entryCategories$: Observable<EntryCategoriesDto>;
  readonly entryCategoriesForVisualisation$: Observable<EntryCategories>;
  private _entryCategoriesRefreshSubject = new BehaviorSubject<void>(undefined);

  constructor(
    private notificationsService: NotificationsService,
    private dataDirectoryApiService: DataDirectoryApiService,
  ) {
    this.entryCategories$ = this._entryCategoriesRefreshSubject.pipe(
      switchMap(() => this.dataDirectoryApiService.getEntryCategories()),
      shareReplay(1),
      catchError(() => {
        this.notificationsService.error('Error', 'Couldn\t load entry categories.');
        return EMPTY;
      }),
    );
    this.entryCategoriesForVisualisation$ = this.entryCategories$.pipe(
      map(entryCategories => {
        const flatEntryCategories = new Map<number, FlatEntryCategory>();
        this.flattenEntryCategories(flatEntryCategories, entryCategories.expenses);
        this.flattenEntryCategories(flatEntryCategories, entryCategories.incomes);
        return {
          flatEntryCategories: flatEntryCategories,
          expenses: entryCategories.expenses,
          incomes: entryCategories.incomes
        };
      }),
      shareReplay(1),
    );
  }

  private flattenEntryCategories(
    result: Map<number, FlatEntryCategory>,
    entryCategories?: EntryCategoryDto[],
    level = 1,
    path = '',
  ) {
    entryCategories?.forEach(entryCategory => {
      result.set(entryCategory.id, {
        ...entryCategory,
        path: path.substr(4),
        level: level,
      });
      this.flattenEntryCategories(result, entryCategory.children, level + 1, path + ' -> ' + entryCategory.name);
    });
  }

  refreshEntryCategories() {
    this._entryCategoriesRefreshSubject.next();
  }
}
