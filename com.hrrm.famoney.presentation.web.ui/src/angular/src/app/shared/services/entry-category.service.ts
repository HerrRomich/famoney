import { Injectable } from '@angular/core';
import { MasterDataApiService, EntryCategoriesDto } from '@famoney-apis/master-data';
import { Observable, BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap, shareReplay, catchError, map } from 'rxjs/operators';
import { NotificationsService } from 'angular2-notifications';
import { EntryCategoryDto } from '@famoney-apis/master-data/model/entry-category.dto';

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
  fullPath: string;
  level: number;
}

interface EntryCategories {
  flatEntryCategories: Map<number, FlatEntryCategory>;
  expenses: EntryCategory[];
  incomes: EntryCategory[];
}

export const pathSeparator = ' ' + String.fromCharCode(8594) + ' ';
@Injectable({
  providedIn: 'root',
})
export class EntryCategoryService {
  readonly entryCategories$: Observable<EntryCategoriesDto>;
  readonly entryCategoriesForVisualisation$: Observable<EntryCategories>;
  private _entryCategoriesRefreshSubject = new BehaviorSubject<void>(undefined);

  constructor(
    private notificationsService: NotificationsService,
    private masterDataApiService: MasterDataApiService,
  ) {
    this.entryCategories$ = this._entryCategoriesRefreshSubject.pipe(
      switchMap(() => this.masterDataApiService.getEntryCategories()),
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
      const fullPath = (path ? path + pathSeparator : '') + entryCategory.name;
      result.set(entryCategory.id, {
        ...entryCategory,
        path: path,
        fullPath: fullPath,
        level: level,
      });
      this.flattenEntryCategories(result, entryCategory.children, level + 1, fullPath);
    });
  }

  refreshEntryCategories() {
    this._entryCategoriesRefreshSubject.next();
  }
}
