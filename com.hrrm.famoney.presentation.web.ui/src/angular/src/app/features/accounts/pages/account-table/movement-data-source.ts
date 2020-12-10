import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { AccountDto, MovementDto } from '@famoney-apis/accounts';
import { MovementsService } from '@famoney-features/accounts/services/movements.service';
import { MultiRange, multirange } from 'multi-integer-range';
import { EMPTY, Observable, of } from 'rxjs';
import { map, mergeMap, startWith, switchMap, tap } from 'rxjs/operators';

const PAGE_SIZE = 150;
const PAGE_BUFFER = 50;

export interface Movement {}

export class MovementDataSource extends DataSource<MovementDto | undefined> {
  private _timestamp: moment.Moment | undefined;
  private _data: (MovementDto | undefined)[] = [];
  private _dataPages: MultiRange = new MultiRange();

  constructor(private _movementsService: MovementsService, private _account$: Observable<readonly [moment.Moment, AccountDto]>) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<(MovementDto | undefined)[]> {
    return this._account$.pipe(
      tap(([timestamp, account]) => {
        this._timestamp = timestamp;
        this._data = new Array<MovementDto>(account.movementCount);
        this._dataPages = multirange();
      }),
      switchMap(([,account]) =>
        collectionViewer.viewChange.pipe(
          startWith({
            start: 0,
            end: PAGE_SIZE + PAGE_BUFFER,
          }),
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
              return account?.id
                ? this._movementsService.getMovements(account?.id, rangeStart, rangeEnd - rangeStart).pipe(
                    map(([,movements]) => {
                      this._data.splice(rangeStart, movements.length, ...movements);
                      this._dataPages = this._dataPages.append([[min, max]]);
                      return this._data;
                    }),
                  )
                : EMPTY;
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

  added(position: number) {
    this._data.push(undefined);
    this._dataPages.subtract([position, Infinity]);
  }

  changed(position: number, positionAfter: number) {
    this._dataPages.subtract([Math.min(position, positionAfter), Infinity]);
  }

  deleted(position: number) {
    this._data.pop();
    this._dataPages.subtract([position, Infinity]);
  }

}
