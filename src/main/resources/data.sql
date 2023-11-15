drop table if exists activity cascade;
drop table if exists employee cascade;
drop table if exists shift cascade;
drop table if exists employee_shift cascade;
drop table if exists shop_order cascade;

create table activity
(
    actual_points    integer,
    employee_id      integer unique,
    id               integer generated by default as identity,
    potential_points integer,
    activity_status  varchar(255) check (activity_status in ('PRESENT', 'LATE', 'ABSENT')),
    primary key (id)
);

create table employee
(
    id    integer generated by default as identity,
    email varchar(255),
    role  varchar(255) check (role in ('USER', 'STAFF', 'MANAGER', 'ADMIN')),
    primary key (id)
);
create table shift
(
    id         integer generated by default as identity,
    end_time   timestamp(6),
    start_time timestamp(6),
    primary key (id)
);
create table employee_shift
(
    employee_id integer not null,
    shift_id    integer not null
);

create table shop_order
(
    id                  bigserial primary key,
    order_status        varchar(50),
    delivery_method     varchar(50),
    payment_method      varchar(50),
    order_date          timestamp,
    total_price         numeric(10, 2),
    first_name          varchar(255),
    last_name           varchar(255),
    phone_number        varchar(15),
    delivery_address    varchar(255),
    employee_activity_id bigint,
    foreign key (employee_activity_id) references activity(id)
);

/*insert into employee(id, email, role)
values (1, 'employee1@email.com', 'STAFF');

insert into employee(id, email, role)
values (2, 'employee2@email.com', 'STAFF');*/

/*insert into employee(id,email,role)
values (1, 'employee1@email.com', 'MANAGER');*/

/*insert into activity(id, activity_status, employee_id,
                     potential_points, actual_points)
values (1, 'PRESENT', 1, 5, 0);

insert into activity(id, activity_status, employee_id,
                     potential_points, actual_points)
values (2, 'LATE', 2, 10, 5);*/

/*insert into shift (start_time, end_time)
values ('2023-11-15 00:00:00', '2023-11-15 13:00:00');
insert into shift (start_time, end_time)
values ('2023-11-15 13:30:00', '2023-11-15 23:59:59');

insert into employee_shift(employee_id, shift_id)
values (1, 1),
       (2, 1),
       (2,1),
       (2,2);*/


