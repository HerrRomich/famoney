import { NgModule } from '@angular/core';
import {
  ApiModule as AccountsApiModule,
  Configuration as AccountsApiConfiguration,
  ConfigurationParameters as AccountsApiConfigurationParameters
} from '@famoney-apis/accounts';
import { HttpClientModule } from '@angular/common/http';

export function accountsApiConfigFactory(): AccountsApiConfiguration {
  const params: AccountsApiConfigurationParameters = {
    basePath: '/api/accounts'
  };
  return new AccountsApiConfiguration(params);
}

const API_MODULES = [AccountsApiModule.forRoot(accountsApiConfigFactory)];

@NgModule({
  imports: [...API_MODULES, HttpClientModule]
})
export class ApisModule {}
