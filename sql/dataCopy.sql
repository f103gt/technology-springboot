create table client
(
    id         bigserial primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    email      varchar(255) not null unique,
    password   varchar(255) not null,
    role       varchar(25),
    is_enabled boolean default (false),
);

create table address
(
    id           bigserial primary key,
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
    id           bigserial primary key,
    category_id  int                 not null,
    product_name varchar(255) unique not null,
    sku          varchar(255)        not null unique,
    quantity     int                 not null,
    price        numeric(10, 2)      not null,
    foreign key (category_id) references category (id)
);

create table image
(
    id         bigserial primary key,
    is_primary boolean false,
    image_data bytea  not null,
    product_id bigint not null,
    foreign key (product_id) references product (id)
);

create table new_employee
(
    id            serial primary key,
    email         varchar(255) unique not null,
    role          varchar(35),
    file_hash     varchar(255)        not null,
    is_registered boolean default ('false')
);

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
    id        bigserial primary key,
    client_id bigint not null,
    order_id  bigint,
    foreign key (client_id) references client (id),
    foreign key (order_id) references shop_order (id)
);

create table cart_item
(
    id         bigserial primary key,
    cart_id    bigint         not null,
    quantity   int            not null,
    price      numeric(10, 2) not null,
    product_id bigint         not null,
    foreign key (cart_id) references cart (id),
    foreign key (product_id) references product (id)
);

create table order_status
(
    id          int primary key,
    status_name varchar(15) not null
);

create table delivery_method
(
    id                   int primary key,
    delivery_method_name varchar(50) not null
);

create table payment_method
(
    id                  int primary key,
    payment_method_name varchar(50) not null
);

/*TODO MODIFY SHOP_ORDER TABLE IN THE REAL DATABASE*/
create table shop_order
(
    id                   bigserial primary key,
    client_id            bigint,
    cart_id              bigint,
    unique_identifier    varchar(20),
    order_status_id      varchar(50),
    delivery_method      varchar(50),
    payment_method       varchar(50),
    order_date           timestamp,
    total_price          numeric(10, 2),
    first_name           varchar(255),
    last_name            varchar(255),
    phone_number         varchar(15),
    delivery_address     varchar(255),
    employee_activity_id bigint,
    foreign key (client_id) references client (id),
    foreign key (cart_id) references cart (id),
    foreign key (employee_activity_id) references activity (id)
);

create table shift
(
    id         serial primary key,
    start_time time not null,
    end_time   time
);

create table client_shift
(
    shift_id   int    not null,
    client_id  bigint not null,
    shift_date date   not null,
    foreign key (shift_id) references shift (id),
    foreign key (client_id) references client (id)
);

create table activity
(
    id               bigserial primary key,
    client_id        bigint not null,
    potential_points integer default (0),
    actual_points    integer default (0),
    activity_status  varchar(50),
    foreign key (client_id) references client (id)
);

create table token
(
    id      bigserial primary key,
    token   varchar(500) not null unique,
    type    varchar(50)  not null,
    revoked boolean      not null,
    expired boolean      not null,
    user_id bigint       not null,
    foreign key (user_id) references client (id)
);
