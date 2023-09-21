create table client
(
    id bigserial primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    email      varchar(255) not null unique,
    password   varchar(255) not null,
    is_enabled boolean default (false),
    cart_id bigint,
    key (cart_id) references cart(id)
);

create table role
(
    id        serial primary key,
    role_name varchar(25) not null unique
);

create table address
(
    id bigserial primary key,
    phone_number varchar(20)  not null,
    region       varchar(255) not null,
    district     varchar(255) not null,
    locality     varchar(255) not null,
    street       varchar(255) not null,
    premise      varchar(255) not null,
    zipcode      varchar(5)   not null
);

create table client_role
(
    client_id bigint not null,
    role_id   int    not null,
    foreign key (client_id) references client (id),
    foreign key (role_id) references role (id)
);

create table client_address
(
    client_id  bigint not null,
    address_id bigint not null,
    foreign key (client_id) references client (id),
    foreign key (address_id) references address (id)
);

create table category
(
    id                 serial primary key,
    parent_category_id int,
    category_name      varchar(255) unique not null,
    foreign key (parent_category_id) references category (id)
);

create table product
(
    id bigserial primary key,
    category_id  int                 not null,
    product_name varchar(255) unique not null,
    sku          varchar(255)        not null unique,
    quantity     int                 not null,
    price        numeric(10, 2)      not null,
    foreign key (category_id) references category (id)
);

create table image
(
    id bigserial primary key,
    image_data bytea not null,
    product_id bigint not null,
    foreign key (product_id) references product (id)
);

insert into role(role_name)
values ('USER'),
       ('MANAGER'),
       ('ADMIN');

insert into client_role(client_id, role_id)
values ((select id from client where email = 'john.doe@example.com'),
        (select id from role where role_name = 'MANAGER'));

insert into client_role(client_id, role_id)
values ((select id from client where email = 'john.doe@example.com'),
        (select id from role where role_name = 'USER'));

delete
from client_role
where role_id = (select id from role where role_name = 'USER')
  and client_id = (select id from client where email = 'john.doe@example.com');

create table cart
(
    id bigserial primary key,
    client_id bigint not null,
    foreign key (client_id) references client (id)
);

create table cart_item
(
    id bigserial primary key,
    cart_id    bigint         not null,
    quantity   int            not null,
    price      numeric(10, 2) not null,
    product_id bigint         not null,
    foreign key (cart_id) references category (id),
    foreign key (product_id) references product (id)
);