import { NgModule } from '@angular/core';
import { ApiModule as AccountsApiModule, Configuration as AccountsApiConfiguration } from '@famoney-apis/accounts';
import {
  ApiModule as DataDirectoryApiModule,
  Configuration as DataDirectoryApiConfiguration,
} from '@famoney-apis/data-directory';
import { HttpClientModule } from '@angular/common/http';

export function accountsApiConfigFactory(): AccountsApiConfiguration {
  return new AccountsApiConfiguration({
    basePath: '/famoney/api/accounts',
  });
}

export function dataDirectoryApiConfigFactory(): DataDirectoryApiConfiguration {
  return new DataDirectoryApiConfiguration({
    basePath: '/famoney/api/data-directory',
  });
}

@NgModule({
  imports: [
    AccountsApiModule.forRoot(accountsApiConfigFactory),
    DataDirectoryApiModule.forRoot(dataDirectoryApiConfigFactory),
    HttpClientModule,
  ],
})
export class ApisModule {}
