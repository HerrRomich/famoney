create or replace table entry_category(
  id int not null auto_increment primary key,
  category_type varchar(50) not null comment 'Category discrimantor: expense, income).',
  budget_id int not null comment 'Budget ID. Groups users.',
  name varchar(250) not null comment 'Name of an entry category.',
  parent_id int comment 'Reference to a parent category',
  constraint unique entry_category_budget_name_uq (budget_id, parent_id, name)
);

alter table entry_category add constraint parent_fk foreign key (parent_id) references entry_category (id);

create or replace index entry_category_children on entry_category(budget_id, category_type, parent_id);


