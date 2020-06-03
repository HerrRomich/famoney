import * as moment from 'moment';

export interface AccountEntry {
  entryDate?: moment.Moment;
  bookingDate?: moment.Moment;
  budgetPeriod?: moment.Moment;
  entryItems: EntryItem[];
}

export interface EntryItem {
  categoryId: number;
  amount: number;
  comments?: string;
}

