import { Component, OnInit, ViewChild, OnDestroy, Injectable } from '@angular/core';
import { Observable, Subscription, iif, of, concat, timer } from 'rxjs';
import { MovementDto, AccountsApiService, AccountDto } from '@famoney-apis/accounts';
import { CollectionViewer, DataSource, ListRange } from '@angular/cdk/collections';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, tap, mergeMap, skipWhile, takeWhile, take } from 'rxjs/operators';
import {
  CdkVirtualScrollViewport,
  VIRTUAL_SCROLL_STRATEGY,
  FixedSizeVirtualScrollStrategy
} from '@angular/cdk/scrolling';
import { ɵa as EcoFabSpeedDialActionsComponent, ɵb as EcoFabSpeedDialComponent } from '@ecodev/fab-speed-dial';
import { interval } from 'rxjs';
import { AccountEntryDialogComponent } from './account-entry-dialog.component';
import { MatDialog } from '@angular/material';

class MovementDataSource extends DataSource<MovementDto> {
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
        // Range.start = Math.max(range.start - 40, 0);
        // Range.end = Math.min(range.end + 40, account.movementCount);
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

const fabSpeedDialDelayOnHover = 350;

@Injectable()
export class AccountMovementsViertualScrollStrategy extends FixedSizeVirtualScrollStrategy {
  private viewport: CdkVirtualScrollViewport;

  constructor() {
    super(40, 600, 800);
  }

  attach(viewport: CdkVirtualScrollViewport) {
    this.viewport = viewport;
    super.attach(viewport);
  }

  onDataLengthChanged() {
    super.onDataLengthChanged();
    this.viewport.scrollToIndex(0);
    timer(0, 50)
      .pipe(
        skipWhile(() => this.viewport.getRenderedRange().start !== 0),
        tap(() => this.viewport.scrollToIndex(this.viewport.getDataLength())),
        takeWhile(() => this.viewport.getRenderedRange().end !== this.viewport.getDataLength())
      )
      .subscribe();
  }
}

@Component({
  selector: 'app-account-table',
  templateUrl: 'account-table.component.html',
  styleUrls: ['account-table.component.scss'],
  providers: [
    {
      provide: VIRTUAL_SCROLL_STRATEGY,
      useClass: AccountMovementsViertualScrollStrategy
    }
  ]
})
export class AccountTableComponent implements OnInit, OnDestroy {
  movementDataSource: MovementDataSource;

  @ViewChild('fabSpeedDial', { static: true })
  fabSpeedDial: EcoFabSpeedDialComponent;

  @ViewChild('fabSpeedDialActions', { static: true })
  fabSpeedDialActions: EcoFabSpeedDialActionsComponent;

  private speedDialTriggerSubscription: Subscription;

  private fabSpeedDialOpenChangeSubbscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private accountsApiService: AccountsApiService,
    private accountEntryDialogComponent: MatDialog
  ) {}

  ngOnInit() {
    this.fabSpeedDialOpenChangeSubbscription = this.fabSpeedDial.openChange
      .pipe(
        tap(opened => {
          this.fabSpeedDial.fixed = !opened;
        })
      )
      .subscribe();

    const account$ = this.route.paramMap.pipe(
      map(params => Number.parseInt(params.get('accountId'), 10)),
      switchMap(accountId => this.accountsApiService.getAccount(accountId))
    );
    this.movementDataSource = new MovementDataSource(this.accountsApiService, account$);
  }

  ngOnDestroy() {
    this.fabSpeedDialOpenChangeSubbscription.unsubscribe();
  }

  triggerSpeedDial() {
    if (!this.speedDialTriggerSubscription || this.speedDialTriggerSubscription.closed) {
      this.speedDialTriggerSubscription = interval(fabSpeedDialDelayOnHover)
        .pipe(
          tap(() => {
            this.fabSpeedDial.open = true;
          }),
          take(1)
        )
        .subscribe();
    }
  }

  stopSpeedDial() {
    if (this.speedDialTriggerSubscription && !this.speedDialTriggerSubscription.closed) {
      this.speedDialTriggerSubscription.unsubscribe();
    }
    this.speedDialTriggerSubscription = null;
  }

  addEntry() {
    this.stopSpeedDial();
    this.accountEntryDialogComponent.open(AccountEntryDialogComponent, {
      width: '520px',
      minWidth: '520px',
      maxWidth: '520px',
      panelClass: 'account-entry-dialog',
      disableClose: true,
      hasBackdrop: true
    });
  }

  addTransfer() {
    console.log('Add transfer.');
  }

  addRefund() {
    console.log('Add refund.');
  }
}
