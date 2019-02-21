import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDTO, AccountsApiService } from '@famoney-apis/accounts';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit {

  public accounts: Observable<AccountDTO[]>;

  constructor(private acountsApiService: AccountsApiService) { }

  ngOnInit() {
    this.accounts = this.acountsApiService.getAllAccounts();
  }

}
