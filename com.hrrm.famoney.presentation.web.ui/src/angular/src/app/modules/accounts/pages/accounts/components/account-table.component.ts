import { Component, OnInit } from "@angular/core";
import {
  Observable,
  combineLatest,
  forkJoin,
  Subject,
  Subscription
} from "rxjs";
import { MovementDto, AccountsApiService } from "@famoney-apis/accounts";
import { CollectionViewer, DataSource } from "@angular/cdk/collections";
import { ActivatedRoute, Router } from "@angular/router";
import { map, switchMap, tap } from "rxjs/operators";

interface MovementWithSum extends MovementDto {
  sum: number;
}

class MovementDataSource extends DataSource<MovementWithSum> {
  private _data: MovementWithSum[];
  private data$ = new Subject<MovementWithSum[]>();
  dataSubscription: Subscription;

  constructor(
    private accountsApiService: AccountsApiService,
    private accountId$: Observable<number>
  ) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<MovementWithSum[]> {
    const movementSlicesInfo$ = this.accountId$.pipe(
      switchMap(accountId =>
        this.accountsApiService.getMovementSlicesByAccountId(accountId).pipe(
          tap(movementSlicesInfo => {
            this._data = new Array<MovementWithSum>(
              movementSlicesInfo.movementCount
            );
            this.data$.next(this._data);
          })
        )
      )
    );
    this.dataSubscription = combineLatest([
      movementSlicesInfo$,
      collectionViewer.viewChange
    ])
      .pipe(
        map(([movementSlicesInfo, range]) => {
          return movementSlicesInfo.movementSlices.reduce(
            (prev, curr) => {
              const bottom = prev.count;
              const top = prev.count + curr.movementCount;
              if (bottom <= range.end && top >= range.start) {
                prev.movementSliceIds.push(curr.id);
              }
              prev.start = Math.min(prev.start, bottom);
              prev.end = Math.max(prev.end, top);
              return prev;
            },
            Object.assign(
              {
                accountId: movementSlicesInfo.accountId,
                count: 0,
                movementSliceIds: [] as number[]
              },
              range
            )
          );
        }),
        switchMap(movementSlices =>
          forkJoin(
            movementSlices.movementSliceIds.map(movementSliceId =>
              this.accountsApiService.getMovementsBySliceId(
                movementSlices.accountId,
                movementSliceId
              )
            )
          ).pipe(
            map(movementSlicesWithMovements =>
              movementSlicesWithMovements.reduce((prev, curr) => {
                const movementsWithSum = curr.movements.map(
                  movement =>
                    Object.assign(
                      {
                        sum: curr.bookingSum + movement.amount
                      } as MovementWithSum,
                      movement
                    ) as MovementWithSum
                );
                prev.push(...movementsWithSum);
                return prev;
              }, new Array<MovementWithSum>())
            ),
            tap(movementsWithSum => {
              this._data.splice(movementSlices.start, movementsWithSum.length, ...movementsWithSum);
              this.data$.next(this._data);
            })
          )
        )
      )
      .subscribe();
    return this.data$;
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.dataSubscription.unsubscribe();
  }
}

@Component({
  selector: "app-account-table",
  templateUrl: "account-table.component.html",
  styleUrls: ["account-table.component.scss"]
})
export class AccountTableComponent implements OnInit {
  movementSlicesDataSource: MovementDataSource;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountsApiService: AccountsApiService
  ) {}

  ngOnInit() {
    const accountId$ = this.route.paramMap.pipe(
      map(params => Number.parseInt(params.get("accountId"), 10))
    );
    this.movementSlicesDataSource = new MovementDataSource(
      this.accountsApiService,
      accountId$
    );
  }
}
