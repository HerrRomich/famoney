import { Component, OnInit, ViewChild, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import {
  Observable,
  combineLatest,
  Subscription,
  iif,
  of,
  Subject,
  concat,
  interval
} from 'rxjs';
import { MovementDto, AccountsApiService, AccountDto } from '@famoney-apis/accounts';
import {
  CollectionViewer,
  DataSource,
  ListRange} from '@angular/cdk/collections';
import { ActivatedRoute} from '@angular/router';
import { map, switchMap, tap, mergeMap, delay, debounceTime} from 'rxjs/operators';
import { CdkVirtualScrollViewport, VIRTUAL_SCROLL_STRATEGY, FixedSizeVirtualScrollStrategy } from '@angular/cdk/scrolling';
import { ɵb as EcoFabSpeedDialComponent} from '@ecodev/fab-speed-dial';
import { log } from 'util';

class MovementDataSource extends DataSource<MovementDto> {
  private _data: MovementDto[];
  private data$: Subject<MovementDto[]> = new Subject();
  private dataSubscription: Subscription;

  constructor(
    private accountsApiService: AccountsApiService,
    private account$: Observable<AccountDto>
  ) {
    super();
  }

  connect(collectionViewer: CollectionViewer): Observable<MovementDto[]> {
    const account$ = this.account$.pipe(
      tap(account => {
        this._data = new Array<MovementDto>(account.movementCount);
      })
    );
    this.dataSubscription = combineLatest([
      account$,
      concat(of({start: 0, end: 0} as ListRange), collectionViewer.viewChange.pipe(debounceTime(250)))
    ]).pipe(
      map(([account, range]) => {
        let start = range.end;
        for (let index = range.start; index < range.end; index++) {
          if (!(this._data[index])) {
            start = index;
            break;
          }
        }
        let end = start;
        for (let index = range.end - 1; index >= start; index--) {
          if (!(this._data[index])) {
            end = index + 1;
            break;
          }
        }
        return [account, {start: start, end: end}] as [AccountDto, ListRange];
      }),
      mergeMap(([account, range]) =>
        iif(
          () => range.end > range.start,
          this.accountsApiService
            .getMovements(account.id, range.start, range.end - range.start)
            .pipe(
              map(movements =>
                this._data.splice(range.start, movements.length, ...movements)
              )
            ),
          of(this._data)
        )
      ),
      tap(() => this.data$.next(this._data))
    ).subscribe();
    return this.data$;
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.dataSubscription.unsubscribe();
  }
}

const fabSpeedDialDelayOnHover = 350;

export class AccountMovementsViertualScrollStrategy extends FixedSizeVirtualScrollStrategy {

  private viewport: CdkVirtualScrollViewport;
  private scrollSubscription: Subscription;

  constructor() {
    super(40, 500, 1000);
  }

  attach(viewport: CdkVirtualScrollViewport) {
    super.attach(viewport);
    this.viewport = viewport;
  }

  onDataLengthChanged() {
    super.onDataLengthChanged();
    this.scrollSubscription = interval(50).pipe(
      tap(() => {
        this.scrollToIndex(this.viewport.getDataLength() , 'auto');
        console.log(this.viewport.getDataLength());
      })
    ).subscribe();
  }

  onContentScrolled() {
    if (this.scrollSubscription && !this.scrollSubscription.closed) {
      this.scrollSubscription.unsubscribe();
    }
    super.onContentScrolled();
  }

}

@Component({
  selector: 'app-account-table',
  templateUrl: 'account-table.component.html',
  styleUrls: ['account-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{provide: VIRTUAL_SCROLL_STRATEGY, useClass: AccountMovementsViertualScrollStrategy}]
})
export class AccountTableComponent implements OnInit, OnDestroy {
  movementSlicesDataSource: MovementDataSource;

  private speedDialTriggerSubscription: Subscription;

  private fabSpeedDialOpenChangeSubbscription: Subscription;

  @ViewChild('virtualScrollViewport', { static: true })
  private virtualScrollViewport: CdkVirtualScrollViewport;

  @ViewChild('fabSpeedDial', { static: true })
  private fabSpeedDial: EcoFabSpeedDialComponent;

  constructor(
    private route: ActivatedRoute,
    private accountsApiService: AccountsApiService
  ) { }

  ngOnInit() {
    this.fabSpeedDialOpenChangeSubbscription = this.fabSpeedDial.openChange
      .pipe(
        tap(opened => this.fabSpeedDial.fixed = !opened)
      ).subscribe();

    const account$ = this.route.paramMap.pipe(
      map(params => Number.parseInt(params.get('accountId'), 10)),
      switchMap(accountId => this.accountsApiService.getAccount(accountId))
    );
    this.movementSlicesDataSource = new MovementDataSource(
      this.accountsApiService,
      account$
    );
  }

  ngOnDestroy() {
    this.fabSpeedDialOpenChangeSubbscription.unsubscribe();
  }

  triggerSpeedDial() {
    if (!this.speedDialTriggerSubscription || this.speedDialTriggerSubscription.closed) {
      this.speedDialTriggerSubscription = of(1).pipe(
        delay(fabSpeedDialDelayOnHover),
        tap(() => this.fabSpeedDial.open = true)
      ).subscribe();
    }
  }

  stopSpeedDial() {
    if (this.speedDialTriggerSubscription && !this.speedDialTriggerSubscription.closed) {
      this.speedDialTriggerSubscription.unsubscribe();
      this.speedDialTriggerSubscription = null;
    }
  }

  addEntry(): void {
    console.log('clicked');
  }

}
