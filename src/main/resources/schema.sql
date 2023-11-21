CREATE TABLE IF NOT EXISTS users(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(100),
    email varchar(320),
    UNIQUE(email),
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(100),
    description varchar(500),
    is_available boolean,
    owner_id INTEGER,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    INTEGER,
    booker_id  INTEGER,
    status     VARCHAR(10),
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_booking_item FOREIGN KEY(item_id) REFERENCES  items(id)
        on delete cascade on update cascade,
    CONSTRAINT fk_booking_booker FOREIGN KEY(booker_id) REFERENCES  users(id)
        on delete cascade on update cascade
);

CREATE TABLE IF NOT EXISTS comments
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(1000),
    created TIMESTAMP WITHOUT TIME ZONE,
    item_id INTEGER,
    author_id INTEGER,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES items(id)
        on delete cascade on update cascade,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id)
        on delete cascade on update cascade
);