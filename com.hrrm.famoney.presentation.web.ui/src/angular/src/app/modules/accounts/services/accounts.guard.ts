import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AccountsService } from './accounts.service';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Injectable()
export class AccountsGuard implements CanActivate {
  constructor(private router: Router, private accountsService: AccountsService) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const accountId = parseInt(route.params.accountId, 10) || 0;
    return this.accountsService.getAccounts().pipe(
      map(accounts => {
        let result = accounts.find(value => value.id === accountId);
        if (!result) {
          result = accounts.find(value => value.id === this.accountsService.getSelectedAccountId());
        }
        if (!result) {
          return accounts[0].id ?? 0;
        }
        return result.id;
      }),
      tap(value => this.accountsService.setSelectedAccountId(value)),
      tap(value => {
        if (value !== accountId) {
          this.router.navigate([
            route.parent.pathFromRoot
              .map(ars => ars.url.map(segment => segment.path).join('/'))
              .filter(part => part.length !== 0)
              .join('/'),
            value,
          ]);
        }
      }),
      map(() => true),
    );
  }
}
