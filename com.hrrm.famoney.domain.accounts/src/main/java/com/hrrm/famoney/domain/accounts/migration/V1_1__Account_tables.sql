create or replace table account(
  id int not null auto_increment primary key,
  name varchar(250) not null comment 'Name of an account group'
);

create or replace table account_tag(
  account_id int not null,
  tag varchar(250) not null comment 'Tag for filtering accounts',
  constraint account_tag_pk primary key (account_id, tag)
);
