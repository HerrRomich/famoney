import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AccountDto } from '@famoney-apis/accounts';
import { AccountTagsPopupComponent } from './components/account-tags-popup.component';
import { ComponentPortal } from '@angular/cdk/portal';
import { Overlay, CdkOverlayOrigin } from '@angular/cdk/overlay';
import { ActivatedRoute } from '@angular/router';
import { AccountsService } from '@famoney-modules/accounts/services/accounts.service';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit, AfterViewInit {
  public accounts$: Observable<Array<AccountDto>>;
  public accountTags$: Observable<string[]>;

  @ViewChild('accountTagsPopupButton', { static: true }) accountTagsPopupButton: CdkOverlayOrigin;

  accountTagsPopupPortal: ComponentPortal<AccountTagsPopupComponent>;

  constructor(private acountsService: AccountsService, private overlay: Overlay, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.accounts$ = this.acountsService.getAccounts().pipe(tap(accounts => {
      accounts.findIndex(accountDto => accountDto.id === this.route.snapshot.params['accountId']);
    }));
    this.accountTags$ = this.acountsService.getTags();
  }

  ngAfterViewInit(): void {
    this.accountTagsPopupPortal = new ComponentPortal(AccountTagsPopupComponent);
  }

  openAccountTagsPopup(): void {
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
      backdropClass: 'cdk-overlay-dark-backdrop'
    });
    accountTagsPopup.attach(this.accountTagsPopupPortal);
    accountTagsPopup.backdropClick().subscribe(() => accountTagsPopup.detach());
  }

  getAccountTagsCount(): number {
    return this.acountsService.selectedAccountTags.value.size;
  }

  getAccountTags(): string {
    return Array.from(this.acountsService.selectedAccountTags.value)
      .map(tag => '- ' + tag)
      .join('\n');
  }
}
