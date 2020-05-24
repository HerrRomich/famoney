import { DataSource, CollectionViewer, ListRange } from '@angular/cdk/collections';
import { MovementDto, AccountsApiService, AccountDto } from '@famoney-apis/accounts';
import { Observable, concat, of } from 'rxjs';
import { tap, switchMap, map, mergeMap, finalize, startWith } from 'rxjs/operators';
import { MultiRange, multirange } from 'multi-integer-range';

const PAGE_SIZE = 150;
const PAGE_BUFFER = 50;

export interface Movement {}

export class MovementDataSource extends DataSource<MovementDto> {
  private _data: MovementDto[] = [];
  private _dataPages: MultiRange = new MultiRange();

  constructor(private accountsApiService: AccountsApiService, private account$: Observable<AccountDto>) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<MovementDto[]> {
    return this.account$.pipe(
      tap(account => {
        this._data = new Array<MovementDto>(account.movementCount ?? 0);
        this._dataPages = multirange();
      }),
      switchMap(account =>
        collectionViewer.viewChange.pipe(
          map(range => {
            const pageStart = Math.floor((range.start - PAGE_BUFFER) / PAGE_SIZE);
            const pageEnd = Math.floor((range.end + PAGE_BUFFER) / PAGE_SIZE);
            return multirange([[pageStart, pageEnd]])
              .intersect([[0, Math.floor(this._data.length / PAGE_SIZE)]])
              .subtract(this._dataPages);
          }),
          mergeMap(requieredPages => {
            if (requieredPages.length() > 0) {
              const min = requieredPages.min() ?? 0;
              const max = requieredPages.max() ?? 0;
              const rangeStart = min * PAGE_SIZE;
              const rangeEnd = (max + 1) * PAGE_SIZE;
              return this.accountsApiService.getMovements(account.id, rangeStart, rangeEnd - rangeStart).pipe(
                map(movements => {
                  this._data.splice(rangeStart, movements.length, ...movements);
                  this._dataPages = this._dataPages.append([[min, max]]);
                  return this._data;
                }),
              );
            } else {
              return of(this._data);
            }
          }, 3),
          startWith(this._data),
        ),
      ),
    );
  }

  disconnect(collectionViewer: CollectionViewer): void {}
}
