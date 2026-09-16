DROP TABLE IF EXISTS pickup_requests;
DROP TABLE IF EXISTS waste_types;
DROP TABLE IF EXISTS collection_points;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    user_id SERIAL not null,
    full_name varchar(150) not null,
    cpf varchar(14) unique,
    email varchar(150) not null unique,
    phone varchar(20),
    street varchar(150),
    number varchar(20),
    neighborhood varchar(100),
    city varchar(100) default 'Santa Rita do Sapucaí',
    state varchar(2) default 'MG',
    password varchar(100) not null,
    user_type varchar(20) default 'CITIZEN',
    active boolean default true,
    created_at timestamp default current_timestamp,
    PRIMARY KEY (user_id)
);

CREATE TABLE waste_types
(
    waste_type_id SERIAL not null,
    name varchar(100) not null unique,
    category varchar(100),
    description text,
    active boolean default true,
    PRIMARY KEY (waste_type_id)
);

CREATE TABLE collection_points
(
    point_id SERIAL not null,
    name varchar(150) not null,
    street varchar(150) not null,
    number varchar(20),
    neighborhood varchar(100),
    city varchar(100) default 'Santa Rita do Sapucaí',
    state varchar(2) default 'MG',
    cep varchar(10),
    phone varchar(20),
    opening_hours varchar(100),
    accepted_waste_types text,
    latitude double precision,
    longitude double precision,
    active boolean default true,
    PRIMARY KEY (point_id)
);

CREATE TABLE pickup_requests
(
    pickup_id SERIAL not null,
    user_id int not null,
    waste_type_id int not null,
    collector_id int,
    street varchar(150) not null,
    number varchar(20),
    neighborhood varchar(100) not null,
    city varchar(100) default 'Santa Rita do Sapucaí',
    state varchar(2) default 'MG',
    estimated_quantity varchar(100),
    desired_date date,
    status varchar(30) default 'PENDING',
    notes text,
    requested_at timestamp default current_timestamp,
    updated_at timestamp,
    PRIMARY KEY (pickup_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (waste_type_id) REFERENCES waste_types(waste_type_id),
    FOREIGN KEY (collector_id) REFERENCES users(user_id)
);

CREATE TABLE notifications
(
    notification_id SERIAL not null,
    user_id int not null,
    title varchar(100) not null,
    message text not null,
    notification_type varchar(30) default 'INFO',
    is_read boolean default false,
    sent_at timestamp default current_timestamp,
    PRIMARY KEY (notification_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);