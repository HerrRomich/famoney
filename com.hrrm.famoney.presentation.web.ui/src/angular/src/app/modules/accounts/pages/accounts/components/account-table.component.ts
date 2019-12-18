import { Component, OnInit } from "@angular/core";
import {
  Observable,
  combineLatest,
  Subscription,
  iif,
  of,
  Subject
} from "rxjs";
import { MovementDto, AccountsApiService } from "@famoney-apis/accounts";
import {
  CollectionViewer,
  DataSource,
  ListRange
} from "@angular/cdk/collections";
import { ActivatedRoute, Router } from "@angular/router";
import { map, switchMap, tap, mergeMap } from "rxjs/operators";
import { isDefined } from "@angular/compiler/src/util";

class MovementDataSource extends DataSource<MovementDto> {
  private _data: MovementDto[];
  private data$: Subject<MovementDto[]> = new Subject();
  private dataSubscription: Subscription;

  constructor(
    private accountsApiService: AccountsApiService,
    private accountId$: Observable<number>
  ) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<MovementDto[]> {
    const account$ = this.accountId$.pipe(
      switchMap(accountId => this.accountsApiService.getAccount(accountId)),
      tap(account => {
        this._data = new Array<MovementDto>(account.movementCount);
        this.data$.next(this._data);
      })
    );
    this.dataSubscription = combineLatest([
      account$,
      collectionViewer.viewChange
    ])
      .pipe(
        mergeMap(([account, range]) =>
          iif(
            () => {
              const slice = this._data.slice(range.start, range.end);
              return slice.length === slice.filter(Boolean).length;
            },
            of(this._data),
            this.accountsApiService
              .getMovements(account.id, range.start, range.end - range.start)
              .pipe(
                map(movements =>
                  this._data.splice(range.start, movements.length, ...movements)
                )
              )
          )
        ),
        tap(movements => this.data$.next(this._data))
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
