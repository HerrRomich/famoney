import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AccountDto } from '@famoney-apis/accounts';
import { AccountsService } from './accounts.service';
import { AccountTagsPopupComponent } from './components/account-tags-popup.component';
import { ComponentPortal } from '@angular/cdk/portal';
import { Overlay, CdkOverlayOrigin } from '@angular/cdk/overlay';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit, AfterViewInit {
  public accounts: Observable<Array<AccountDto>>;
  public accountTags: Observable<string[]>;

  @ViewChild('accountTagsPopupButton', {static: true}) accountTagsPopupButton: CdkOverlayOrigin;

  accountTagsPopupPortal: ComponentPortal<AccountTagsPopupComponent>;

  constructor(private acountsService: AccountsService, private overlay: Overlay) {}

  ngOnInit() {
    this.accounts = this.acountsService.getAccounts();
    this.accountTags = this.acountsService.getTags();
  }

  ngAfterViewInit() {
    this.accountTagsPopupPortal = new ComponentPortal(AccountTagsPopupComponent);
  }

  openAccountTagsPopup() {
    const position = this.overlay
      .position()
      .flexibleConnectedTo(this.accountTagsPopupButton.elementRef)
      .withPositions([{ originX: 'start', originY: 'center', overlayX: 'start', overlayY: 'top' }])
      .withFlexibleDimensions(true)
      .withGrowAfterOpen(true);
    const accountTagsPopup = this.overlay.create({
      disposeOnNavigation: true,
      positionStrategy: position,
      hasBackdrop: true,
      backdropClass: 'cdk-overlay-dark-backdrop'
    });
    accountTagsPopup.attach(this.accountTagsPopupPortal);
    accountTagsPopup.backdropClick().subscribe(_ => accountTagsPopup.detach());
  }

  getAccountTagsCount() {
    return this.acountsService.selectedAccountTags.value.size;
  }
}
