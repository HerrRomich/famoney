create or replace table account(
  id int not null auto_increment primary key,
  budget_id int not null comment 'Budget ID. Groups users.',
  name varchar(250) not null comment 'Name of an account group.',
  open_date date not null comment 'Date of account opening.',
  movement_count int not null comment 'Count of all account movements.',
  movement_sum decimal(13, 2) not null comment 'Sum of all movements in account.',
  constraint unique account_budget_name_uq (budget_id, name)
) comment 'Personal accounts.';

create or replace table account_tag(
  account_id int not null,
  tag varchar(250) not null comment 'Tag for filtering accounts.',
  constraint account_tag_pk primary key (account_id, tag),
  constraint account_tag_fk foreign key (account_id) references account(id)
) comment 'Elements for tagging accounts.';
