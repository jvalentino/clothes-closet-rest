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
    id VARCHAR(256),
    school VARCHAR(256),
    gender VARCHAR(256),
    grade VARCHAR(256),
    PRIMARY KEY (id),
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
    id SERIAL,
    guardian_email VARCHAR(256),
    datetime TIMESTAMPTZ,
    year int,
    semester VARCHAR(256),
    PRIMARY KEY (id),
    FOREIGN KEY (guardian_email) REFERENCES guardian (email) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (semester) REFERENCES semester (label) ON UPDATE CASCADE
);

CREATE TABLE person (
    id SERIAL,
    relation VARCHAR(256),
    PRIMARY KEY (id)
);

CREATE TABLE visit (
    id SERIAL,
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
    PRIMARY KEY (id),
    FOREIGN KEY (appointment_id) REFERENCES appointment (id) ON DELETE CASCADE ON UPDATE CASCADE, 
    FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE ON UPDATE CASCADE      
);

CREATE TABLE settings (
    id SERIAL,
    gender VARCHAR(256),
    label VARCHAR(256),
    quantity int,
    PRIMARY KEY (id),
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
    id VARCHAR(256),
    PRIMARY KEY (id)
);
