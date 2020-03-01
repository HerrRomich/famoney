import { DataSource, CollectionViewer, ListRange } from '@angular/cdk/collections';
import { MovementDto, AccountsApiService, AccountDto } from '@famoney-apis/accounts';
import { Observable, concat, of, iif } from 'rxjs';
import { tap, switchMap, map, mergeMap } from 'rxjs/operators';

export class MovementDataSource extends DataSource<MovementDto> {
  private _data: MovementDto[];

  constructor(private accountsApiService: AccountsApiService, private account$: Observable<AccountDto>) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<MovementDto[]> {
    return this.account$.pipe(
      tap(account => {
        this._data = new Array<MovementDto>(account.movementCount);
      }),
      switchMap(account =>
        concat(of({ start: 0, end: 0 } as ListRange), collectionViewer.viewChange).pipe(
          map(range => [account, range] as [AccountDto, ListRange])
        )
      ),
      map(([account, range]) => {
        let start = range.end;
        for (let index = range.start; index < range.end; index++) {
          if (!this._data[index]) {
            start = index;
            break;
          }
        }
        let end = start;
        for (let index = range.end - 1; index >= start; index--) {
          if (!this._data[index]) {
            end = index + 1;
            break;
          }
        }
        return [account, { start: start, end: end }] as [AccountDto, ListRange];
      }),
      mergeMap(([account, range]) =>
        iif(
          () => range.end > range.start,
          this.accountsApiService.getMovements(account.id, range.start, range.end - range.start).pipe(
            map(movements => {
              this._data.splice(range.start, movements.length, ...movements);
              return this._data;
            })
          ),
          of(this._data)
        )
      )
    );
  }

  disconnect(collectionViewer: CollectionViewer): void {}
}

