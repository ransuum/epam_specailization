create table users
(
    id         varchar(255) not null
        primary key,
    first_name varchar(255) not null,
    is_active  boolean      not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    username   varchar(255) not null
        constraint ukr43af9ap4edm43mmtq01oddj6
            unique
);

alter table users
    owner to postgres;

create table training_type
(
    id                 varchar(255) not null
        primary key,
    training_type_name varchar(255) not null
        constraint ukqb5b0a5pxrluo3l487u4qi0ri
            unique
        constraint training_type_training_type_name_check
            check ((training_type_name)::text = ANY
                   ((ARRAY ['SELF_PLACING'::character varying, 'LABORATORY'::character varying, 'FUNDAMENTALS'::character varying])::text[]))
);

alter table training_type
    owner to postgres;

create table trainer
(
    id                varchar(255) not null
        primary key
        constraint fkd2hg6g09xsi5yyhtg5btyx13m
            references users,
    specialization_id varchar(255) not null
        constraint fk8kfknuu0rsxcbd8gykyw2ag65
            references training_type
);

alter table trainer
    owner to postgres;

create table trainee
(
    id            varchar(255) not null
        primary key
        constraint fkpx9kccl4t64wtsi8geile7vwo
            references users,
    address       varchar(255),
    date_of_birth date         not null
);

alter table trainee
    owner to postgres;

create table training
(
    id               varchar(255) not null
        primary key,
    duration         bigint       not null,
    start_time       date         not null,
    training_name    varchar(255) not null,
    trainee_id       varchar(255) not null
        constraint fki2dctw34e0xl50d8tsnrre7te
            references trainee,
    trainer_id       varchar(255) not null
        constraint fk7r3b25ygw5bdjamojskmpk0b9
            references trainer,
    training_type_id varchar(255) not null
        constraint fkosdbocw0x9ygfmna67s7vtewh
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

INSERT INTO training_type (id, training_type_name)
VALUES ('type1', 'SELF_PLACING'),
       ('type2', 'LABORATORY'),
       ('type3', 'FUNDAMENTALS');

INSERT INTO trainer (id, specialization_id)
VALUES ('uuid3', 'type1'),
       ('uuid4', 'type2');

INSERT INTO training (id, duration, start_time, training_name, trainee_id, trainer_id, training_type_id)
VALUES ('training1', 60, '2025-04-01', 'Morning Yoga', 'uuid1', 'uuid3', 'type1'),
       ('training2', 90, '2025-04-02', 'Evening Strength', 'uuid2', 'uuid4', 'type2'),
       ('training3', 45, '2025-04-03', 'Beginner Fundamentals', 'uuid1', 'uuid4', 'type3');