create table client
(
    id bigserial primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    email      varchar(255) not null unique,
    password   varchar(255) not null,
    is_enabled boolean default (false)
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
    foreign key (category_id) references category (id)
);

create table image
(
    id bigserial primary key,
    image_data bytea not null,
    product_id bigint not null,
    foreign key (product_id) references product (id)
);

insert into role(role_name) values('USER'),('MANAGER'),('ADMIN');