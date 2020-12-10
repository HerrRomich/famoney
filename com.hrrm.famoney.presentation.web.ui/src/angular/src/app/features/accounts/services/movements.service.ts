import { NotificationsService } from 'angular2-notifications';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MovementChangeEventDto } from '@famoney-apis/account-events';
import { AccountsApiService, ApiErrorDto, Configuration as AccountsApiConfiguration } from '@famoney-apis/accounts';
import { ServerEventsService } from '@famoney-shared/services/server-events.service';
import * as moment from 'moment';
import { catchError, map } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MovementsService {
  constructor(
    private _accountsApiService: AccountsApiService,
    private _accountsApiConfiguration: AccountsApiConfiguration,
    private _serverEventsService: ServerEventsService,
    private notificationsService: NotificationsService,
  ) {}

  getMovements(accountId: number, offset: number, limit: number) {
    return this._accountsApiService.getMovements(accountId, offset, limit, 'response').pipe(
      map(response => {
        if (response.ok && response.body) {
          const operationTimestamp = moment(response.headers.get('fm-operation-timestamp'));
          return [operationTimestamp, response.body] as const;
        } else {
          throw new HttpErrorResponse({
            ...response,
            error: response.body,
            url: response.url || undefined,
          });
        }
      }),
      catchError(err => {
        const {error: ApiErrorDto} = err;
        this.notificationsService.error('Error', error.description);
        return EMPTY;
      }),
    );
  }

  getMovementsChangeEvents(accountId: number) {
    return this._serverEventsService.connectToServer<MovementChangeEventDto>(
      `${this._accountsApiConfiguration.basePath}/accounts/${accountId}/movements/events`,
    );
  }
}
