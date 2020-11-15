import { Injectable } from '@angular/core';
import { Configuration as AccountsApiConfiguration } from '@famoney-apis/accounts';
import { ServerEvent, ServerEventsService } from '@famoney-shared/services/server-events.service';

export interface MovementChangeEvent extends ServerEvent {}

@Injectable({
  providedIn: 'root',
})
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
