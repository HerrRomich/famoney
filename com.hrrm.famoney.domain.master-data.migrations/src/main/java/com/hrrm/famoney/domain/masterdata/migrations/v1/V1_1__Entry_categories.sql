create table entry_category(
  id serial not null primary key,
  category_type varchar(50) not null,
  budget_id int not null,
  name varchar(250) not null,
  parent_id int,
  constraint entry_category_budget_name_uq unique (budget_id, parent_id, name)
);
comment on table entry_category is 'Categories of movement entries.';
comment on column entry_category.category_type is 'Category discrimantor: expense, income.';
comment on column entry_category.budget_id is 'Budget ID. Groups users.';
comment on column entry_category.name is 'Name of an entry category.';
comment on column entry_category.parent_id is 'Reference to a parent category.';

alter table entry_category add constraint parent_fk foreign key (parent_id) references entry_category (id);


