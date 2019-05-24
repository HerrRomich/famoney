create or replace table account(
  id int not null auto_increment primary key,
  budget_id int not null comment 'Budget ID. Groups users.',
  name varchar(250) not null comment 'Name of an account group.',
  constraint unique account_budget_name_uq (budget_id, name)
);

create or replace table account_tag(
  account_id int not null,
  tag varchar(250) not null comment 'Tag for filtering accounts.',
  constraint account_tag_pk primary key (account_id, tag)
);
