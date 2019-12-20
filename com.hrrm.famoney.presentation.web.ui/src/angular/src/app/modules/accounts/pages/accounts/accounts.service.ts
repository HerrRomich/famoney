import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { AccountDto, AccountsApiService } from '@famoney-apis/accounts';
import { map, flatMap } from 'rxjs/operators';

const ACCOUNT_TAGS_STORAGE = 'ACCOUNT_TAGS_STORAGE';

@Injectable()
export class AccountsService {
    private selectedAccountTags$: BehaviorSubject<Set<string>>;

    get selectedAccountTags(): BehaviorSubject<Set<string>> {
        return this.selectedAccountTags$;
    }

    constructor(private accountsApiService: AccountsApiService) {
        let tags: string[] = [];
        if (window.localStorage) {
            tags = JSON.parse(window.localStorage.getItem(ACCOUNT_TAGS_STORAGE));
        }
        this.selectedAccountTags$ = new BehaviorSubject(new Set<string>(tags));
    }

    public getAccounts(): Observable<AccountDto[]> {
        return this.selectedAccountTags$.pipe(
            flatMap(tags => this.accountsApiService.getAllAccounts(Array.from(tags)))
        );
    }

    getTags(): Observable<string[]> {
        return combineLatest([this.accountsApiService.getAllAccountTags(), this.selectedAccountTags$]).pipe(
            map(([original, selected]) => Array.from(new Set(original.filter(tag => !selected.has(tag)))))
        );
    }

    addTag(tag: string): void {
        this.transformStringSet(this.selectedAccountTags$, tagsSet => tagsSet.add(tag));
    }

    removeTag(tag: string): void {
        this.transformStringSet(this.selectedAccountTags$, tagsSet => {
            tagsSet.delete(tag);
            return tagsSet;
        });
    }

    clearTags(): void {
        this.transformStringSet(this.selectedAccountTags$, tagsSet => {
            tagsSet.clear();
            return tagsSet;
        });
    }

    private transformStringSet = (input: BehaviorSubject<Set<string>>, transformation: (input: Set<string>) => Set<string>): void => {
        const newValue = transformation(input.value);
        if (window.localStorage) {
            window.localStorage.setItem(ACCOUNT_TAGS_STORAGE, JSON.stringify(Array.from(newValue)));
        }
        input.next(newValue);
    };

}
