import { Component, OnInit, ViewChild, OnDestroy, Inject } from '@angular/core';
import { Subscription, empty } from 'rxjs';
import { AccountsApiService, MovementDataDto, EntryDataDto, AccountDto, MovementDto } from '@famoney-apis/accounts';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, tap, take, filter } from 'rxjs/operators';
import { CdkVirtualScrollViewport, VIRTUAL_SCROLL_STRATEGY } from '@angular/cdk/scrolling';
import { EcoFabSpeedDialActionsComponent, EcoFabSpeedDialComponent } from '@ecodev/fab-speed-dial';
import { interval } from 'rxjs';
import { AccountEntryDialogComponent } from './account-entry-dialog.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AccountMovementsViertualScrollStrategy } from './account-movements.virtual-scroller-strategy';
import { MovementDataSource } from './movement-data-source';
import { NotificationsService } from 'angular2-notifications';
import { TranslateService } from '@ngx-translate/core';

const fabSpeedDialDelayOnHover = 350;

@Component({
  selector: 'fm-account-table',
  templateUrl: 'account-table.component.html',
  styleUrls: ['account-table.component.scss'],
  providers: [
    {
      provide: VIRTUAL_SCROLL_STRATEGY,
      useClass: AccountMovementsViertualScrollStrategy,
    },
  ],
})
export class AccountTableComponent implements OnInit, OnDestroy {
  movementDataSource: MovementDataSource;

  @ViewChild('fabSpeedDial', { static: true })
  fabSpeedDial!: EcoFabSpeedDialComponent;

  @ViewChild('fabSpeedDialActions', { static: true })
  fabSpeedDialActions!: EcoFabSpeedDialActionsComponent;

  @ViewChild(CdkVirtualScrollViewport)
  viewPort!: CdkVirtualScrollViewport;

  private speedDialTriggerSubscription?: Subscription;

  private fabSpeedDialOpenChangeSubbscription?: Subscription;
  private accountDTO?: AccountDto;

  constructor(
    private route: ActivatedRoute,
    private accountsApiService: AccountsApiService,
    private accountEntryDialogComponent: MatDialog,
    private notificationsService: NotificationsService,
    private translateService: TranslateService,
    @Inject(VIRTUAL_SCROLL_STRATEGY)
    private accountMovementsViertualScrollStrategy: AccountMovementsViertualScrollStrategy,
  ) {
    const account$ = this.route.paramMap.pipe(
      map(params => Number.parseInt(params.get('accountId') ?? '', 10)),
      tap(() => (this.accountDTO = undefined)),
      filter(accountId => accountId !== NaN),
      switchMap(accountId => this.accountsApiService.getAccount(accountId)),
      tap(accountDTO => {
        this.accountDTO = accountDTO;
        this.accountMovementsViertualScrollStrategy.switchAccount(accountDTO.movementCount ?? 0);
      }),
    );
    this.movementDataSource = new MovementDataSource(this.accountsApiService, account$);
  }

  ngOnInit() {
    this.fabSpeedDialOpenChangeSubbscription = this.fabSpeedDial.openChange
      .pipe(
        tap(opened => {
          this.fabSpeedDial.fixed = !opened;
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.fabSpeedDialOpenChangeSubbscription?.unsubscribe();
  }

  get inverseTranslation(): string {
    if (!this.viewPort || !this.viewPort['_renderedContentTransform']) {
      return '-0px';
    }
    return `-${this.viewPort['_renderedContentOffset']}px`;
  }

  triggerSpeedDial() {
    if (!this.speedDialTriggerSubscription || this.speedDialTriggerSubscription.closed) {
      this.speedDialTriggerSubscription = interval(fabSpeedDialDelayOnHover)
        .pipe(
          tap(() => {
            this.fabSpeedDial.open = true;
          }),
          take(1),
        )
        .subscribe();
    }
  }

  stopSpeedDial() {
    if (this.speedDialTriggerSubscription?.closed) {
      this.speedDialTriggerSubscription.unsubscribe();
    }
    this.speedDialTriggerSubscription = undefined;
  }

  addEntry() {
    this.stopSpeedDial();
    if (this.accountDTO === undefined) {
      this.translateService
        .get(['notifications.title.error', 'accounts.table.errors.noAccount'])
        .pipe(
          tap((errorMesages: { [key: string]: string }) =>
            this.notificationsService.error(
              errorMesages['notifications.title.error'],
              errorMesages['accounts.table.errors.noAccount'],
            ),
          ),
        )
        .subscribe();
      return;
    }
    const data: [AccountDto, MovementDto | null] = [this.accountDTO, null];
    const accountEntryDialogRef: MatDialogRef<
      AccountEntryDialogComponent,
      [MovementDto]
    > = this.accountEntryDialogComponent.open(AccountEntryDialogComponent, {
      width: '520px',
      minWidth: '520px',
      maxWidth: '520px',
      panelClass: 'account-entry-dialog',
      disableClose: true,
      hasBackdrop: true,
      data: data,
    });
    accountEntryDialogRef
      .afterClosed()
      .pipe(tap())
      .subscribe();
  }

  addTransfer() {
    console.log('Add transfer.');
  }

  addRefund() {
    console.log('Add refund.');
  }
}
