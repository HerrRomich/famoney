import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { Observable, combineLatest } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { AccountTagsPopupComponent } from './components/account-tags-popup.component';
import { ComponentPortal } from '@angular/cdk/portal';
import { Overlay, CdkOverlayOrigin } from '@angular/cdk/overlay';
import { ActivatedRoute } from '@angular/router';
import { AccountsService, Account } from '@famoney-modules/accounts/services/accounts.service';

@Component({
  selector: 'fm-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss'],
})
export class AccountsComponent implements AfterViewInit {
  public accounts$: Observable<Array<Account>>;
  public accountTags$: Observable<string[]>;

  @ViewChild('accountTagsPopupButton', { static: true }) accountTagsPopupButton!: CdkOverlayOrigin;

  private _accountTagsPopupPortal?: ComponentPortal<AccountTagsPopupComponent>;

  constructor(private acountsService: AccountsService, private overlay: Overlay, private route: ActivatedRoute) {
    this.accounts$ = combineLatest([this.acountsService.accounts$, this.acountsService.selectedAccountTags$]).pipe(
      map(([accounts, tags]) => {
        return accounts.filter(
          account => tags.size === 0 || account.tags?.reduce((result: boolean, tag) => result || tags.has(tag), false),
        );
      }),
    );
    this.accountTags$ = this.acountsService.getTags();
  }

  ngAfterViewInit() {
    this._accountTagsPopupPortal = new ComponentPortal(AccountTagsPopupComponent);
  }

  openAccountTagsPopup() {
    if (!this.accountTagsPopupButton) {
      return;
    }
    const position = this.overlay
      .position()
      .flexibleConnectedTo(this.accountTagsPopupButton.elementRef)
      .withPositions([{ originX: 'start', originY: 'bottom', overlayX: 'start', overlayY: 'top' }])
      .withFlexibleDimensions(true)
      .withGrowAfterOpen(true);
    const accountTagsPopup = this.overlay.create({
      disposeOnNavigation: true,
      positionStrategy: position,
      hasBackdrop: true,
      panelClass: 'fm-tags-panel',
      backdropClass: 'cdk-overlay-dark-backdrop',
    });
    accountTagsPopup.attach(this._accountTagsPopupPortal);
    accountTagsPopup.backdropClick().subscribe(() => accountTagsPopup.detach());
  }

  getAccountTagsCount() {
    return this.acountsService.selectedAccountTags$.value.size;
  }

  getAccountTags() {
    return Array.from(this.acountsService.selectedAccountTags$.value)
      .map(tag => '- ' + tag)
      .join('\n');
  }
}
