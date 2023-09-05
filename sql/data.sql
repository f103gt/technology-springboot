create table client(id bigint auto_increment primary key,
                    first_name varchar(255) not null,
                    last_name varchar(255) not null,
                    patronymic varchar(255) not null,
                    email varchar(255) not null unique,
                    phone_number varchar(25) not null,
                    password varchar(255) not null unique,
                    is_enabled bool);


create table role(id int auto_increment primary key,
                  role_name varchar(25) not null unique);

create table address(id bigint auto_increment primary key,
                     region varchar(255) not null,
                     district varchar(255) not null,
                     locality varchar(255) not null,
                     street varchar(255) not null,
                     premise varchar(255) not null,
                     zipcode varchar(5) not null);

create table client_role(
                            id bigint not null,
                            client_id bigint not null,
                            role_id int not null,
                            foreign key (client_id) references client(id),
                            foreign key (role_id) references role(id));

create table client_address(
                               id bigint auto_increment primary key,
                               client_id bigint not null,
                               address_id bigint not null,
                               foreign key (client_id) references client(id),
                               foreign key (address_id) references address(id));

create table category
(
    id                 bigint auto_increment primary key,
    category_name      varchar(255) unique not null
);

create table product
(
    id            bigint auto_increment primary key,
    category_name varchar(255)        not null,
    product_name  varchar(255) unique not null,
    foreign key (category_name) references category (category_name)
);

create table image
(
    id                   bigint auto_increment primary key,
    image_data           blob not null,
    product_name         varchar(255) not null,
    foreign key (product_name) references product (product_name)
);