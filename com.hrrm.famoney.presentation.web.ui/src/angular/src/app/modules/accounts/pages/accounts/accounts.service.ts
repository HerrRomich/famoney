import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { AccountDto, AccountsApiService } from '@famoney-apis/accounts';
import { map, switchMap, shareReplay } from 'rxjs/operators';

const ACCOUNT_TAGS_STORAGE = 'ACCOUNT_TAGS_STORAGE';
const ACCOUNT_ID_STORAGE = 'ACCOUNT_ID_STORAGE';

@Injectable()
export class AccountsService {

  private selectedAccountTags$: BehaviorSubject<Set<string>>;

  private accounts$: Observable<AccountDto[]>;

  private selectedAccountId: number;

  get selectedAccountTags(): BehaviorSubject<Set<string>> {
    return this.selectedAccountTags$;
  }

  constructor(private accountsApiService: AccountsApiService) {
    let tags: string[] = [];
    if (window.localStorage) {
      tags = JSON.parse(window.localStorage.getItem(ACCOUNT_TAGS_STORAGE));
      this.selectedAccountId = parseInt(window.localStorage.getItem(ACCOUNT_ID_STORAGE), 10);
    }
    this.selectedAccountTags$ = new BehaviorSubject(new Set<string>(tags));
  }

  getSelectedAccountId() {
    return this.selectedAccountId;
  }

  setSelectedAccountId(accountId: number) {
    this.selectedAccountId = accountId;
    window.localStorage.setItem(ACCOUNT_ID_STORAGE, this.selectedAccountId.toString(10));
  }

  getAccounts() {
    if (!this.accounts$) {
      this.accounts$ = this.selectedAccountTags$.pipe(
        switchMap(tags => this.accountsApiService.getAllAccounts(Array.from(tags))),
        shareReplay(1)
      );
    }
    return this.accounts$;
  }

  getTags() {
    return combineLatest([this.accountsApiService.getAllAccountTags(), this.selectedAccountTags$]).pipe(
      map(([original, selected]) => Array.from(new Set(original.filter(tag => !selected.has(tag)))))
    );
  }

  addTag(tag: string) {
    this.transformStringSet(this.selectedAccountTags$, tagsSet => tagsSet.add(tag));
  }

  removeTag(tag: string) {
    this.transformStringSet(this.selectedAccountTags$, tagsSet => {
      tagsSet.delete(tag);
      return tagsSet;
    });
  }

  clearTags() {
    this.transformStringSet(this.selectedAccountTags$, tagsSet => {
      tagsSet.clear();
      return tagsSet;
    });
  }

  private transformStringSet = (input: BehaviorSubject<Set<string>>, transformation: (input: Set<string>) => Set<string>) => {
    const newValue = transformation(input.value);
    if (window.localStorage) {
      window.localStorage.setItem(ACCOUNT_TAGS_STORAGE, JSON.stringify(Array.from(newValue)));
    }
    input.next(newValue);
  };

}
