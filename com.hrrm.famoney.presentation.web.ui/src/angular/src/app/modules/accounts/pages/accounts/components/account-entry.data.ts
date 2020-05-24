import * as moment from 'moment';

export interface AccountEntry {
  entryDate?: moment.Moment;
  bookingDate?: moment.Moment;
  budgetMonth?: moment.Moment;
  entryItems: EntryItem[];
}

export interface EntryItem {
  categoryId: number;
  amount: number;
  comments?: string;
}

