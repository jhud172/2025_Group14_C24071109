drop table if exists record_condition;
drop table if exists physical_conditions;
drop table if exists health_records;

create table if not exists physical_conditions
(
    id   bigint primary key auto_increment not null,
    name varchar(180)
) engine = InnoDB;

create table if not exists health_records
(
    id             bigint primary key auto_increment not null,
    baseline_date  datetime                          not null,
    systolic_blood_pressure bigint,
    diastolic_blood_pressure bigint,
    cholesterol bigint,
    weight_kg      bigint                            not null,
    height_cm      bigint                            not null,
    bmi            bigint,
    activity_level varchar(180)
) engine = InnoDB;

create table if not exists record_condition
(
    health_record_id      bigint not null,
    physical_condition_id bigint not null,
    primary key (health_record_id, physical_condition_id),
    foreign key (health_record_id) references health_records (id),
    foreign key (physical_condition_id) references physical_conditions (id)
) engine = InnoDB;