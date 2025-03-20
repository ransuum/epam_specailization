create table users
(
    id         varchar(255) not null
        primary key,
    first_name varchar(255) not null,
    is_active  boolean      not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    username   varchar(255) not null
        constraint uk_dc4eq7plr20fdhq528twsak1b
            unique
);

alter table users
    owner to postgres;

create table trainee
(
    id            varchar(255) not null
        primary key
        constraint fkd2sx2ixfx78wig2al9w1ou8g4
            references users,
    address       varchar(255),
    date_of_birth date         not null
);

alter table trainee
    owner to postgres;

create table trainer
(
    id             varchar(255) not null
        primary key
        constraint fkc0ge3pidd3l026x10x6f1qkmg
            references users,
    specialization varchar(255)
);

alter table trainer
    owner to postgres;

create table training_type
(
    id            varchar(255) not null
        primary key,
    training_type varchar(255) not null
        constraint training_view_training_type_check
            check ((training_type)::text = ANY
                   ((ARRAY ['SELF_PLACING'::character varying, 'LABORATORY'::character varying, 'FUNDAMENTALS'::character varying])::text[]))
);

alter table training_type
    owner to postgres;

create table training
(
    id               varchar(255) not null
        primary key,
    duration         bigint,
    start_time        date,
    training_name    varchar(255) not null,
    trainee_id       varchar(255) not null
        constraint fkqetaw250jxb3witc7rewif68g
            references trainee,
    trainer_id       varchar(255) not null
        constraint fksv3ghh6nrfnidfno5mhxctr8b
            references trainer,
    training_view_id varchar(255) not null
        constraint fka1js7wvjlmdha696irgy6m37x
            references training_type
);

alter table training
    owner to postgres;

INSERT INTO users (id, first_name, last_name, username, password, is_active)
VALUES ('uuid1', 'John', 'Doe', 'johndoe', 'password123', TRUE),
       ('uuid2', 'Jane', 'Smith', 'janesmith', 'password456', TRUE),
       ('uuid3', 'Mike', 'Johnson', 'mikejohnson', 'password789', TRUE),
       ('uuid4', 'Emily', 'Davis', 'emilydavis', 'password987', TRUE);

INSERT INTO trainee (id, address, date_of_birth)
VALUES ('uuid1', '123 Elm Street', '1990-01-01'),
       ('uuid2', '456 Oak Avenue', '1995-06-15');

INSERT INTO trainer (id, specialization)
VALUES ('uuid3', 'Yoga'),
       ('uuid4', 'Strength Training');

INSERT INTO training_type (id, training_type)
VALUES ('view1', 'SELF_PLACING'),
       ('view2', 'LABORATORY'),
       ('view3', 'FUNDAMENTALS');

INSERT INTO training (id, duration, start_time, training_name, trainee_id, trainer_id, training_view_id)
VALUES ('training1', 60, '2025-04-01', 'Morning Yoga', 'uuid1', 'uuid3', 'view1'),
       ('training2', 90, '2025-04-02', 'Evening Strength', 'uuid2', 'uuid4', 'view2'),
       ('training3', 45, '2025-04-03', 'Beginner Fundamentals', 'uuid1', 'uuid4', 'view3');