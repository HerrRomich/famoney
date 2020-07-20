import { MovementsService } from './../../../services/movements.service';
import { Component, OnInit, ViewChild, OnDestroy, Inject } from '@angular/core';
import { Subscription, EMPTY } from 'rxjs';
import { AccountsApiService, AccountDto, MovementDto } from '@famoney-apis/accounts';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, tap, take, filter, retryWhen, delay, shareReplay } from 'rxjs/operators';
import { CdkVirtualScrollViewport, VIRTUAL_SCROLL_STRATEGY } from '@angular/cdk/scrolling';
import { EcoFabSpeedDialActionsComponent, EcoFabSpeedDialComponent } from '@ecodev/fab-speed-dial';
import { interval } from 'rxjs';
import { AccountEntryDialogComponent } from './account-entry-dialog.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AccountMovementsViertualScrollStrategy } from './account-movements.virtual-scroller-strategy';
import { MovementDataSource } from './movement-data-source';
import { NotificationsService } from 'angular2-notifications';
import { TranslateService } from '@ngx-translate/core';
import { EntryCategoryService } from '@famoney-shared/services/entry-category.service';

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

  private _speedDialTriggerSubscription?: Subscription;
  private _movementsChangeEventsSubscription: Subscription;

  private _fabSpeedDialOpenChangeSubbscription?: Subscription;
  private _accountDTO?: AccountDto;

  constructor(
    private _route: ActivatedRoute,
    private _accountsApiService: AccountsApiService,
    private _movementsService: MovementsService,
    private _accountEntryDialogComponent: MatDialog,
    private _entryCategoriesService: EntryCategoryService,
    private _notificationsService: NotificationsService,
    private _translateService: TranslateService,
    @Inject(VIRTUAL_SCROLL_STRATEGY)
    private _accountMovementsViertualScrollStrategy: AccountMovementsViertualScrollStrategy,
  ) {
    const account$ = this._route.paramMap.pipe(
      map(params => Number.parseInt(params.get('accountId') ?? '', 10)),
      tap(() => (this._accountDTO = undefined)),
      filter(accountId => accountId !== NaN),
      switchMap(accountId => {
        return this._accountsApiService.getAccount(accountId);
      }),
      tap(accountDTO => {
        this._accountDTO = accountDTO;
        this._accountMovementsViertualScrollStrategy.switchAccount(accountDTO.movementCount ?? 0);
      }),
      shareReplay(1),
    );
    this.movementDataSource = new MovementDataSource(this._accountsApiService, account$);
    this._movementsChangeEventsSubscription = account$
      .pipe(
        switchMap(accountDTO => this._movementsService.getMovementsChangeEvents(accountDTO.id)),
        tap(() => {
          this._accountMovementsViertualScrollStrategy.onDataLengthChanged();
        }),
        retryWhen(errors => errors.pipe(delay(10000))),
      )
      .subscribe();
  }

  ngOnInit() {
    this._fabSpeedDialOpenChangeSubbscription = this.fabSpeedDial.openChange
      .pipe(
        tap(opened => {
          this.fabSpeedDial.fixed = !opened;
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this._fabSpeedDialOpenChangeSubbscription?.unsubscribe();
    this._movementsChangeEventsSubscription.unsubscribe();
  }

  get inverseTranslation(): string {
    if (!this.viewPort || !this.viewPort['_renderedContentTransform']) {
      return '-0px';
    }
    return `-${this.viewPort['_renderedContentOffset']}px`;
  }

  triggerSpeedDial() {
    if (!this._speedDialTriggerSubscription || this._speedDialTriggerSubscription.closed) {
      this._speedDialTriggerSubscription = interval(fabSpeedDialDelayOnHover)
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
    if (this._speedDialTriggerSubscription?.closed) {
      this._speedDialTriggerSubscription.unsubscribe();
    }
    this._speedDialTriggerSubscription = undefined;
  }

  getMovementComments(movement?: MovementDto) {
    switch (movement?.data?.type) {
      case 'entry':
        const entryItems = movement?.data?.entryItems ?? [];
        return entryItems.length === 1 ? entryItems[0].comments : undefined;
      case 'refund':
      case 'transfer':
        return movement?.data?.comments;
    }
  }

  getMovementCategoryPath$(movement?: MovementDto) {
    switch (movement?.data?.type) {
      case 'entry':
        const entryItems = movement?.data?.entryItems ?? [];
        return entryItems.length === 1 ? this.getCategoryPathById$(entryItems[0].categoryId) : EMPTY;
      case 'refund':
        return this.getCategoryPathById$(movement?.data?.categoryId);
      default:
        return EMPTY;
    }
  }

  private getCategoryPathById$(categoryId: number) {
    return this._entryCategoriesService.entryCategoriesForVisualisation$.pipe(
      map(entryCategories => entryCategories.flatEntryCategories.get(categoryId)?.fullPath),
    );
  }

  addEntry() {
    this.stopSpeedDial();
    if (this._accountDTO === undefined) {
      this._translateService
        .get(['notifications.title.error', 'accounts.table.errors.noAccount'])
        .pipe(
          tap((errorMesages: { [key: string]: string }) =>
            this._notificationsService.error(
              errorMesages['notifications.title.error'],
              errorMesages['accounts.table.errors.noAccount'],
            ),
          ),
        )
        .subscribe();
      return;
    }
    const data: [AccountDto, MovementDto | null] = [this._accountDTO, null];
    const accountEntryDialogRef: MatDialogRef<
      AccountEntryDialogComponent,
      [MovementDto]
    > = this._accountEntryDialogComponent.open(AccountEntryDialogComponent, {
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
