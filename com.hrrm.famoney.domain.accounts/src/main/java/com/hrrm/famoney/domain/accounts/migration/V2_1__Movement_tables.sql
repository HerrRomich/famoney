create or replace table movement(
  id int not null auto_increment primary key,
  account_id int not null comment 'Account ID.',
  type varchar(50)  not null comment 'Movement type.',
  date timestamp not null comment 'Date of movement.',
  booking_date timestamp not null comment 'Date of booking.',
  entry_category_id int comment 'Reference to entry category.',
  amount decimal(13, 2) comment 'Amount of movement.',
  constraint unique movement_date_uq (account_id, date),
  constraint unique booking_date_uq (account_id, booking_date)
);

create or replace table movement_slice(
  id int not null auto_increment primary key,
  account_id int not null comment 'Account ID.',
  date date not null comment 'Date of slice.',
  movement_count int not null comment 'Count of movements over movement date previous to slice point.',
  movement_sum decimal(13, 2) not null comment 'Sum of movements over movement date previous to slice point.',
  booking_count int not null comment 'Count of movements over booking date previous to slice point.',
  booking_sum decimal(13, 2) not null comment 'Sum of movements over booking date previous to slice point.',
  constraint unique slice_date_uq (account_id, date)
);
