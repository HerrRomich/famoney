import { EntryDataDto } from '@famoney-apis/accounts';
import * as moment from 'moment';

export interface EntryDialogData {
  readonly accountId: number;
  readonly movementId?: number;
  readonly entryData?: EntryDataDto;
}

export interface AccountEntry {
  entryDate?: moment.Moment;
  bookingDate?: moment.Moment;
  budgetPeriod?: moment.Moment;
  entryItems: EntryItem[];
}

export interface EntryItem {
  categoryId: number;
  amount: string;
  comments?: string;
}

