import { Injectable } from '@angular/core';
import { FixedSizeVirtualScrollStrategy, CdkVirtualScrollViewport } from '@angular/cdk/scrolling';
import { Subject, Subscription, zip, timer } from 'rxjs';
import { tap, mergeMap, skipWhile, takeWhile } from 'rxjs/operators';

@Injectable()
export class AccountMovementsViertualScrollStrategy extends FixedSizeVirtualScrollStrategy {
  private viewport?: CdkVirtualScrollViewport;
  private dataChanged: Subject<void>;
  private accountSwitched: Subject<number>;
  private dataLengeChangedProcessorSubscription: Subscription;

  constructor() {
    super(40, 600, 800);
    this.dataChanged = new Subject();
    this.accountSwitched = new Subject();
    this.dataLengeChangedProcessorSubscription = zip(this.dataChanged, this.accountSwitched)
      .pipe(
        tap(() => this.viewport?.scrollToIndex(0)),
        mergeMap(() =>
          timer(0, 50).pipe(
            skipWhile(() => this.viewport?.getRenderedRange().start !== 0),
            tap(() => this.viewport?.scrollToIndex(this.viewport?.getDataLength())),
            takeWhile(() => this.viewport?.getRenderedRange().end !== this.viewport?.getDataLength()),
          ),
        ),
      )
      .subscribe();
  }

  attach(viewport: CdkVirtualScrollViewport) {
    this.viewport = viewport;
    super.attach(viewport);
  }

  onDataLengthChanged() {
    super.onDataLengthChanged();
    this.dataChanged.next();
  }

  switchAccount(movementCount: number) {
    this.accountSwitched.next(movementCount);
  }

  detach() {
    this.dataLengeChangedProcessorSubscription.unsubscribe();
    super.detach();
  }
}
