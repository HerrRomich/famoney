import { CdkVirtualScrollViewport, VIRTUAL_SCROLL_STRATEGY } from '@angular/cdk/scrolling';
import { Component, Inject, OnDestroy, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { EcoFabSpeedDialActionsComponent, EcoFabSpeedDialComponent } from '@ecodev/fab-speed-dial';
import { AccountDto, EntryDataDto, MovementDto } from '@famoney-apis/accounts';
import { AccountsService } from '@famoney-features/accounts/services/accounts.service';
import { EntryCategoryService } from '@famoney-shared/services/entry-category.service';
import { TranslateService } from '@ngx-translate/core';
import { NotificationsService } from 'angular2-notifications';
import { EMPTY, Subject, Subscription } from 'rxjs';
import { debounceTime, delay, filter, map, retryWhen, shareReplay, switchMap, tap } from 'rxjs/operators';
import { AccountEntryDialogComponent } from '../../components/account-entry-dialog';
import { MovementsService } from '../../services/movements.service';
import { EntryDialogData } from '../../models/account-entry.model';
import { AccountMovementsViertualScrollStrategy } from './account-movements.virtual-scroller-strategy';
import { MovementDataSource } from './movement-data-source';

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
export class AccountTableComponent implements AfterViewInit, OnDestroy {
  movementDataSource: MovementDataSource;

  @ViewChild('fabSpeedDial', { static: true })
  fabSpeedDial: EcoFabSpeedDialComponent | undefined;

  @ViewChild('fabSpeedDialActions', { static: true })
  fabSpeedDialActions: EcoFabSpeedDialActionsComponent | undefined;

  @ViewChild(CdkVirtualScrollViewport)
  viewPort!: CdkVirtualScrollViewport;

  private _speedDialHovered$ = new Subject<boolean>();

  private _speedDialTriggerSubscription?: Subscription;
  private _movementsChangeEventsSubscription: Subscription;

  private _fabSpeedDialOpenChangeSubbscription?: Subscription;
  private _accountDTO?: AccountDto;

  constructor(
    private _route: ActivatedRoute,
    private _accountsService: AccountsService,
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
      switchMap(accountId => this._accountsService.getAccount(accountId)),
      tap(([operationTimestamp, accountDTO]) => {
        this._accountDTO = accountDTO;
        this._accountMovementsViertualScrollStrategy.switchAccount(operationTimestamp, accountDTO.movementCount);
      }),
      shareReplay(1),
    );
    this.movementDataSource = new MovementDataSource(this._movementsService, account$);
    this._movementsChangeEventsSubscription = account$
      .pipe(
        switchMap(([,accountDTO]) => this._movementsService.getMovementsChangeEvents(accountDTO.id)),
        tap(() => {
          this._accountMovementsViertualScrollStrategy.onDataLengthChanged();
        }),
        retryWhen(errors => errors.pipe(delay(10000))),
      )
      .subscribe();
  }

  ngAfterViewInit() {
    this._fabSpeedDialOpenChangeSubbscription = this.fabSpeedDial!.openChange
      .pipe(
        tap(opened => {
          this.fabSpeedDial!.fixed = !opened;
        }),
      )
      .subscribe();

    this._speedDialTriggerSubscription = this._speedDialHovered$
      .pipe(
        debounceTime(fabSpeedDialDelayOnHover),
        tap(hovered => {
          this.fabSpeedDial!.open = hovered;
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this._fabSpeedDialOpenChangeSubbscription?.unsubscribe();
    this._movementsChangeEventsSubscription.unsubscribe();
    this._speedDialTriggerSubscription?.unsubscribe();
  }

  get inverseTranslation(): string {
    if (!this.viewPort || !this.viewPort['_renderedContentTransform']) {
      return '-0px';
    }
    return `-${this.viewPort['_renderedContentOffset']}px`;
  }

  triggerSpeedDial() {
    this._speedDialHovered$.next(true);
  }

  stopSpeedDial() {
    this._speedDialHovered$.next(false);
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
      this.showNoAccountErrorNotification();
      return;
    }
    this.openAccountEntryDialog({
      accountId: this._accountDTO.id,
    });
  }

  private showNoAccountErrorNotification() {
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
  }

  private openAccountEntryDialog(data: EntryDialogData) {
    const accountEntryDialogRef = this._accountEntryDialogComponent.open<AccountEntryDialogComponent, EntryDialogData, MovementDto>(AccountEntryDialogComponent, {
      width: '520px',
      minWidth: '520px',
      maxWidth: '520px',
      panelClass: 'account-entry-dialog',
      disableClose: true,
      hasBackdrop: true,
      data: data,
    });
    return accountEntryDialogRef
      .afterClosed();
  }

  addTransfer() {
    console.log('Add transfer.');
  }

  addRefund() {
    console.log('Add refund.');
  }

  edit(movement: MovementDto) {
    if (this._accountDTO === undefined) {
      this.showNoAccountErrorNotification();
      return;
    }
    if (movement.data?.type === 'entry') {
      this.openAccountEntryDialog({
        accountId: this._accountDTO.id,
        movementId: movement.id,
        entryData: movement.data,
      });
    }
  }
}
