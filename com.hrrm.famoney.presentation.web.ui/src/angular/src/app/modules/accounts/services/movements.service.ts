import { Injectable } from '@angular/core';
import { ServerEventsService, ServerEvent } from '@famoney-shared/services/server-events.service';
import { Configuration as AccountsApiConfiguration } from '@famoney-apis/accounts';

export interface MovementChangeEvent extends ServerEvent {}

@Injectable()
export class MovementsService {
  constructor(
    private _accountsApiConfiguration: AccountsApiConfiguration,
    private _serverEventsService: ServerEventsService,
  ) {}

  getMovementsChangeEvents(accountId: number) {
    return this._serverEventsService.connectToServer<MovementChangeEvent>(
      `${this._accountsApiConfiguration.basePath}/accounts/${accountId}/movements/events`,
    );
  }
}
