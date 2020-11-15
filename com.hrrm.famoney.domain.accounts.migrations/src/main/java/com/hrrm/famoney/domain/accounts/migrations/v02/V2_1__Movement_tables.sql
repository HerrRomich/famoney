create or replace table movement(
  id int not null auto_increment primary key,
  account_id int not null comment 'Account ID.',
  type varchar(50) not null comment 'Movement type. could be "entry", "refund", "transfer"',
  date datetime(6) not null comment 'Date of movement.',
  booking_date datetime(6) comment 'Date of booking. Could be null, then equals to date of movement',
  budget_period date comment 'Period in budget. Truncated to start of month. Could be null, then equals to month of movement date.',
  category_id int comment 'Reference to entry category for entry or refund.',
  comments varchar(1000) comment 'Comments to entry or refund.',
  opposit_account_id int comment 'Opposit account for transfer',
  amount decimal(13, 2) not null comment 'Amount of movement.',
  total decimal(13, 2) not null comment 'Total amount of movements including this.',
  constraint unique movement_date_uq (account_id, date),
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
