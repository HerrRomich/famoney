import { Component, OnInit, ViewChild, OnDestroy, Inject } from '@angular/core';
import { Observable, Subscription, iif, of, concat } from 'rxjs';
import { MovementDto, AccountsApiService, AccountDto } from '@famoney-apis/accounts';
import { CollectionViewer, DataSource, ListRange } from '@angular/cdk/collections';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, tap, mergeMap, take } from 'rxjs/operators';
import { CdkVirtualScrollViewport, VIRTUAL_SCROLL_STRATEGY } from '@angular/cdk/scrolling';
import { EcoFabSpeedDialActionsComponent, EcoFabSpeedDialComponent } from '@ecodev/fab-speed-dial';
import { interval } from 'rxjs';
import { AccountEntryDialogComponent } from './account-entry-dialog.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AccountMovementsViertualScrollStrategy } from './account-movements.virtual-scroller-strategy';
import { MovementDataSource } from './movement-data-source';

const fabSpeedDialDelayOnHover = 350;

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

  @ViewChild(CdkVirtualScrollViewport, { static: false })
  viewPort: CdkVirtualScrollViewport;

  private speedDialTriggerSubscription: Subscription;

  private fabSpeedDialOpenChangeSubbscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private accountsApiService: AccountsApiService,
    private accountEntryDialogComponent: MatDialog,
    @Inject(VIRTUAL_SCROLL_STRATEGY)
    private accountMovementsViertualScrollStrategy: AccountMovementsViertualScrollStrategy
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
      switchMap(accountId => this.accountsApiService.getAccount(accountId)),
      tap(accountDTO => this.accountMovementsViertualScrollStrategy.switchAccount(accountDTO.movementCount))
    );
    this.movementDataSource = new MovementDataSource(this.accountsApiService, account$);
  }

  ngOnDestroy() {
    this.fabSpeedDialOpenChangeSubbscription.unsubscribe();
  }

  get inverseOfTranslation(): string {
    if (!this.viewPort || !this.viewPort['_renderedContentOffset']) {
      return '-0px';
    }
    const offset = this.viewPort['_renderedContentOffset'];
    return `-${offset + 1}px`;
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
    const accountEntryDialogRef: MatDialogRef<AccountEntryDialogComponent, MovementDto> = this.accountEntryDialogComponent.open(
      AccountEntryDialogComponent,
      {
        width: '520px',
        minWidth: '520px',
        maxWidth: '520px',
        panelClass: 'account-entry-dialog',
        disableClose: true,
        hasBackdrop: true,
        data: {
          date: new Date()
        } as MovementDto
      }
    );
    accountEntryDialogRef.afterClosed().subscribe(accountEntry => {});
  }

  addTransfer() {
    console.log('Add transfer.');
  }

  addRefund() {
    console.log('Add refund.');
  }
}
