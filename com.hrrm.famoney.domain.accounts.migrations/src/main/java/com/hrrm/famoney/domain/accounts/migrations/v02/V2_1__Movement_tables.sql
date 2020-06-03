create or replace table movement(
  id int not null auto_increment primary key,
  account_id int not null comment 'Account ID.',
  type varchar(50)  not null comment 'Movement type. could be "entry", "refund", "transfer"',
  date timestamp not null comment 'Date of movement.',
  booking_date timestamp comment 'Date of booking. Could be null, then equals to date of movement',
  budget_period date comment 'Period in budget. Truncated to start of month. Could be null, then equals to month of movement date.',
  category_id int comment 'Reference to entry category for entry or refund.',
  comments varchar(1000) comment 'Comments to entry or refund.',
  opposit_account_id int comment 'Opposit account for transfer',
  amount decimal(13, 2) comment 'Amount of movement.',
  constraint unique movement_date_uq (account_id, date),
  constraint unique movement_booking_date_uq (account_id, booking_date),
  constraint foreign key movement_account_fk (account_id) references account(id)
) comment 'Table with all movements.';

create or replace table entry_item(
  entry_id int not null comment 'Reference to entry.',
  category_id int comment 'Reference to entry category.',
  comments varchar(1000) comment 'comments to entry item.',
  amount decimal(13, 2) comment 'Amount of entry item.',
  constraint primary key entry_item_pk (entry_id, category_id),
  constraint foreign key entry_item_movement_fk (entry_id) references movement (id)
) comment 'Entry items for multiple scheck positions.';

create or replace table movement_slice(
  id int not null auto_increment primary key,
  account_id int not null comment 'Account ID.',
  date timestamp not null comment 'Timestamp of slice.',
  count int not null comment 'Count of movements over movement date previous to slice point.',
  sum decimal(13, 2) not null comment 'Sum of movements over movement date previous to slice point.',
  constraint unique slice_date_uq (account_id, date),
  constraint foreign key movement_slice_account_fk (account_id) references account(id)
) comment 'Movement slices for speeding up a calculation of summery.';
