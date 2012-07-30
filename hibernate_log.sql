    alter table JPAArtist_JPASong 
        drop 
        foreign key FKCB6CB13E1734144

    alter table JPAArtist_JPASong 
        drop 
        foreign key FKCB6CB13C2E2FDA0

    alter table JPADisc 
        drop 
        foreign key FKD6C54BF0CAE2ED0D

    alter table JPADisc_JPAArtist 
        drop 
        foreign key FK588F0D537794EEA0

    alter table JPADisc_JPAArtist 
        drop 
        foreign key FK588F0D534CC8B5C1

    alter table JPADisc_JPASong 
        drop 
        foreign key FKB1CE3621F1156D5D

    alter table JPADisc_JPASong 
        drop 
        foreign key FKB1CE36217794EEA0

    alter table JPASong 
        drop 
        foreign key FKD6CC3370B133506

    drop table if exists JPAArtist

    drop table if exists JPAArtist_JPASong

    drop table if exists JPADisc

    drop table if exists JPADisc_JPAArtist

    drop table if exists JPADisc_JPASong

    drop table if exists JPASong

    create table JPAArtist (
        DTYPE varchar(31) not null,
        id bigint not null auto_increment,
        birthday datetime,
        name varchar(255),
        primary key (id)
    ) ENGINE=InnoDB

    create table JPAArtist_JPASong (
        knownArtists_id bigint not null,
        knownSongs_id bigint not null,
        primary key (knownArtists_id, knownSongs_id)
    ) ENGINE=InnoDB

    create table JPADisc (
        id bigint not null auto_increment,
        name varchar(255),
        releaseDate datetime,
        mainArtist_id bigint,
        primary key (id)
    ) ENGINE=InnoDB

    create table JPADisc_JPAArtist (
        knownDiscs_id bigint not null,
        artists_id bigint not null,
        primary key (knownDiscs_id, artists_id)
    ) ENGINE=InnoDB

    create table JPADisc_JPASong (
        knownDiscs_id bigint not null,
        songs_id bigint not null
    ) ENGINE=InnoDB

    create table JPASong (
        id bigint not null auto_increment,
        firstReleaseDate datetime,
        name varchar(255),
        composer_id bigint,
        primary key (id)
    ) ENGINE=InnoDB

    alter table JPAArtist_JPASong 
        add index FKCB6CB13E1734144 (knownArtists_id), 
        add constraint FKCB6CB13E1734144 
        foreign key (knownArtists_id) 
        references JPAArtist (id)

    alter table JPAArtist_JPASong 
        add index FKCB6CB13C2E2FDA0 (knownSongs_id), 
        add constraint FKCB6CB13C2E2FDA0 
        foreign key (knownSongs_id) 
        references JPASong (id)

    alter table JPADisc 
        add index FKD6C54BF0CAE2ED0D (mainArtist_id), 
        add constraint FKD6C54BF0CAE2ED0D 
        foreign key (mainArtist_id) 
        references JPAArtist (id)

    alter table JPADisc_JPAArtist 
        add index FK588F0D537794EEA0 (knownDiscs_id), 
        add constraint FK588F0D537794EEA0 
        foreign key (knownDiscs_id) 
        references JPADisc (id)

    alter table JPADisc_JPAArtist 
        add index FK588F0D534CC8B5C1 (artists_id), 
        add constraint FK588F0D534CC8B5C1 
        foreign key (artists_id) 
        references JPAArtist (id)

    alter table JPADisc_JPASong 
        add index FKB1CE3621F1156D5D (songs_id), 
        add constraint FKB1CE3621F1156D5D 
        foreign key (songs_id) 
        references JPASong (id)

    alter table JPADisc_JPASong 
        add index FKB1CE36217794EEA0 (knownDiscs_id), 
        add constraint FKB1CE36217794EEA0 
        foreign key (knownDiscs_id) 
        references JPADisc (id)

    alter table JPASong 
        add index FKD6CC3370B133506 (composer_id), 
        add constraint FKD6CC3370B133506 
        foreign key (composer_id) 
        references JPAArtist (id)