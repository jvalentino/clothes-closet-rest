-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE phone_type (
    label VARCHAR(256),
    PRIMARY KEY (label)
);

CREATE TABLE guardian (
    email VARCHAR(256),
    first_name VARCHAR(256),
    last_name VARCHAR(256),
    phone_number VARCHAR(256),
    phone_type_label VARCHAR(256),
    PRIMARY KEY (email),
    FOREIGN KEY (phone_type_label) REFERENCES phone_type (label) ON UPDATE CASCADE
);

INSERT INTO phone_type(label) VALUES ('mobile');
INSERT INTO phone_type(label) VALUES ('home');
INSERT INTO phone_type(label) VALUES ('work');
INSERT INTO phone_type(label) VALUES ('other');

CREATE TABLE school (
    label VARCHAR(256),
    PRIMARY KEY (label)
);
INSERT INTO school(label) VALUES ('Not Listed/Unknown');

CREATE TABLE gender (
    label VARCHAR(256),
    PRIMARY KEY (label)
);
INSERT INTO gender(label) VALUES ('Male');
INSERT INTO gender(label) VALUES ('Female');

CREATE TABLE grade (
    label VARCHAR(256),
    PRIMARY KEY (label)
);
INSERT INTO grade(label) VALUES ('Pre-K');
INSERT INTO grade(label) VALUES ('K');
INSERT INTO grade(label) VALUES ('1');
INSERT INTO grade(label) VALUES ('2');
INSERT INTO grade(label) VALUES ('3');
INSERT INTO grade(label) VALUES ('4');
INSERT INTO grade(label) VALUES ('5');
INSERT INTO grade(label) VALUES ('6');
INSERT INTO grade(label) VALUES ('7');
INSERT INTO grade(label) VALUES ('8');
INSERT INTO grade(label) VALUES ('9');
INSERT INTO grade(label) VALUES ('10');
INSERT INTO grade(label) VALUES ('11');
INSERT INTO grade(label) VALUES ('12');

CREATE TABLE student (
    student_id VARCHAR(256),
    school VARCHAR(256),
    gender VARCHAR(256),
    grade VARCHAR(256),
    PRIMARY KEY (student_id),
    FOREIGN KEY (school) REFERENCES school (label) ON UPDATE CASCADE,
    FOREIGN KEY (gender) REFERENCES gender (label) ON UPDATE CASCADE,
    FOREIGN KEY (grade) REFERENCES grade (label) ON UPDATE CASCADE
);

CREATE TABLE semester (
    label VARCHAR(256),
    PRIMARY KEY (label)
);
INSERT INTO semester(label) VALUES ('Spring');
INSERT INTO semester(label) VALUES ('Fall');

CREATE TABLE appointment (
    appointment_id SERIAL,
    guardian_email VARCHAR(256),
    datetime TIMESTAMPTZ,
    year int,
    semester VARCHAR(256),
    PRIMARY KEY (appointment_id),
    FOREIGN KEY (guardian_email) REFERENCES guardian (email) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (semester) REFERENCES semester (label) ON UPDATE CASCADE
);

CREATE TABLE person (
    person_id SERIAL,
    relation VARCHAR(256),
    PRIMARY KEY (person_id)
);

CREATE TABLE visit (
    visit_id SERIAL,
    appointment_id int,
    student_id VARCHAR(256),
    person_id int,
    socks int,
    underwear int,
    shoes int,
    coats int,
    backpacks int,
    misc int,
    happened boolean,
    PRIMARY KEY (visit_id),
    FOREIGN KEY (appointment_id) REFERENCES appointment (appointment_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (person_id) REFERENCES person (person_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE settings (
    settings_id SERIAL,
    gender VARCHAR(256),
    label VARCHAR(256),
    quantity int,
    PRIMARY KEY (settings_id),
    FOREIGN KEY (gender) REFERENCES gender (label) ON UPDATE CASCADE
);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Tops', 5);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Pairs Pants', 3);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Pairs Socks', 7);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Pairs Underwear', 7);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Undershirts', 3);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Coats', 1);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'Pair Shoes', 1);
INSERT INTO settings(gender, label, quantity) VALUES ('Male', 'School Supplies and Backpack', 1);

INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Tops', 5);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Bottoms', 3);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Pairs of Socks', 7);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Pairs of Underwear', 7);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Bras', 3);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Pairs Shoes', 1);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Camis', 3);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Tanks', 3);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Tights', 2);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Shrugs', 2);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Sweaters', 2);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'Dress', 1);
INSERT INTO settings(gender, label, quantity) VALUES ('Female', 'School Supplies and Backpack', 1);

CREATE TABLE accepted_id (
    student_id VARCHAR(256),
    PRIMARY KEY (student_id)
);

-- changeset liquibase:2

CREATE TABLE SPRING_SESSION (
	PRIMARY_ID CHAR(36) NOT NULL,
	SESSION_ID CHAR(36) NOT NULL,
	CREATION_TIME BIGINT NOT NULL,
	LAST_ACCESS_TIME BIGINT NOT NULL,
	MAX_INACTIVE_INTERVAL INT NOT NULL,
	EXPIRY_TIME BIGINT NOT NULL,
	PRINCIPAL_NAME VARCHAR(100),
	CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
	SESSION_PRIMARY_ID CHAR(36) NOT NULL,
	ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
	ATTRIBUTE_BYTES BYTEA NOT NULL,
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);

-- changeset liquibase:3

CREATE TABLE auth_user (
    email VARCHAR(256),
    PRIMARY KEY (email)
);

INSERT INTO auth_user(email) VALUES ('jvalentino2@gmail.com');
INSERT INTO auth_user(email) VALUES ('smvalentino85@gmail.com');

-- changeset liquibase:4
ALTER TABLE appointment
  ADD happened boolean;

UPDATE appointment SET happened = false where happened IS NULL;

-- changeset liquibase:5
ALTER TABLE appointment
  ADD event_id  VARCHAR(256);

-- changeset liquibase:6
ALTER TABLE grade
  ADD order_position int;

UPDATE grade SET order_position = 0 WHERE label = 'Pre-K';
UPDATE grade SET order_position = 1 WHERE label = 'K';
UPDATE grade SET order_position = 2 WHERE label = '1';
UPDATE grade SET order_position = 3 WHERE label = '2';
UPDATE grade SET order_position = 4 WHERE label = '3';
UPDATE grade SET order_position = 5 WHERE label = '4';
UPDATE grade SET order_position = 6 WHERE label = '5';
UPDATE grade SET order_position = 7 WHERE label = '6';
UPDATE grade SET order_position = 8 WHERE label = '7';
UPDATE grade SET order_position = 9 WHERE label = '8';
UPDATE grade SET order_position = 10 WHERE label = '9';
UPDATE grade SET order_position = 11 WHERE label = '10';
UPDATE grade SET order_position = 12 WHERE label = '11';
UPDATE grade SET order_position = 13 WHERE label = '12';

ALTER TABLE phone_type
  ADD order_position int;

UPDATE phone_type SET order_position = 0 WHERE label = 'mobile';
UPDATE phone_type SET order_position = 1 WHERE label = 'home';
UPDATE phone_type SET order_position = 2 WHERE label = 'work';
UPDATE phone_type SET order_position = 3 WHERE label = 'other';

-- changeset liquibase:7
ALTER TABLE accepted_id
  ADD school VARCHAR(256);

ALTER TABLE accepted_id
  ADD grade VARCHAR(256);

ALTER TABLE accepted_id
  ADD status VARCHAR(256);

-- changeset liquibase:8
ALTER TABLE appointment
  ADD notified boolean;

ALTER TABLE appointment
  ADD created_datetime TIMESTAMPTZ;

ALTER TABLE appointment
  ADD ip_address VARCHAR(256);

ALTER TABLE appointment
  ADD locale VARCHAR(10);

UPDATE appointment SET notified = false where notified IS NULL;
UPDATE appointment SET locale = 'en' where locale IS NULL;

-- changeset liquibase:9

ALTER TABLE appointment
  ADD waitlist boolean;

UPDATE appointment SET waitlist = false where waitlist IS NULL;

-- changeset liquibase:10

ALTER TABLE appointment
  ADD noshow boolean;

UPDATE appointment SET noshow = false where noshow IS NULL;
