import { Injectable } from '@angular/core';
import { DataDirectoryApiService, EntryCategoriesDto } from '@famoney-apis/data-directory';
import { Observable, BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap, shareReplay, catchError } from 'rxjs/operators';
import { NotificationsService } from 'angular2-notifications';

@Injectable({
  providedIn: 'root',
})
export class EntryCategoryService {
  readonly entryCategories$: Observable<EntryCategoriesDto>;
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
  }

  public refreshEntryCategories() {
    this._entryCategoriesRefreshSubject.next();
  }
}
