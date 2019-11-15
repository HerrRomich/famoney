import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { AccountDto, AccountsApiService } from '@famoney-apis/accounts';
import { map, flatMap } from 'rxjs/operators';

@Injectable()
export class AccountsService {
  private _selectedAccountTags: BehaviorSubject<Set<string>>;
  private transformStringSet = (input: BehaviorSubject<Set<string>>, transformation: (input: Set<string>) => Set<string>): void => {
    input.next(transformation(input.value));
  }

  get selectedAccountTags() {
    return this._selectedAccountTags;
  }

  constructor(private accountsApiService: AccountsApiService) {
    this._selectedAccountTags = new BehaviorSubject(new Set<string>());
  }

  public getAccounts(): Observable<AccountDto[]> {
    return this._selectedAccountTags.pipe(
      flatMap(tags => this.accountsApiService.getAllAccounts(Array.from(tags)))
    );
  }

  getTags(): Observable<string[]> {
    return combineLatest([this.accountsApiService.getAllAccountTags(), this._selectedAccountTags]).pipe(
      map(([original, selected]) => Array.from(new Set(original.filter(tag => !selected.has(tag)))))
    );
  }

  addTag(tag: string): any {
    this.transformStringSet(this._selectedAccountTags, tagsSet => tagsSet.add(tag));
  }

  removeTag(tag: string): any {
    this.transformStringSet(this._selectedAccountTags, tagsSet => {
      tagsSet.delete(tag);
      return tagsSet;
    });
  }

  clearTags() {
    this.transformStringSet(this._selectedAccountTags, tagsSet => {
      tagsSet.clear();
      return tagsSet;
    });
  }
}
