CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    token TEXT NOT NULL
);

CREATE TABLE boards (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE board_users (
    board_id INT REFERENCES boards(id),
    user_id INT REFERENCES users(id),
    PRIMARY KEY (board_id, user_id)
);

CREATE TABLE lists (
    id SERIAL PRIMARY KEY,
    board_id INT REFERENCES boards(id),
    name TEXT NOT NULL,
    position INT NOT NULL
);

CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    conclusion_date TIMESTAMP
);

CREATE TABLE list_cards (
    list_id INT REFERENCES lists(id),
    card_id INT REFERENCES cards(id),
    index INT NOT NULL,
    PRIMARY KEY (list_id, card_id)
);

CREATE OR REPLACE PROCEDURE move_card_to_index(
    card_id INT,
    list_id INT,
    new_index INT
)
LANGUAGE plpgsql
AS $$
DECLARE
old_index INT;
BEGIN
    -- Get the current index of the card in the list
SELECT index INTO old_index FROM list_cards WHERE card_id = card_id AND list_id = list_id;

-- If the new index is greater than the old index, shift all cards between them down by 1
IF new_index > old_index THEN
UPDATE list_cards SET index = index - 1 WHERE list_id = list_id AND index > old_index AND index <= new_index;
-- If the new index is less than the old index, shift all cards between them up by 1
ELSIF new_index < old_index THEN
UPDATE list_cards SET index = index + 1 WHERE list_id = list_id AND index >= new_index AND index < old_index;
END IF;

    -- Update the index of the card to the new index
UPDATE list_cards SET index = new_index WHERE card_id = card_id AND list_id = list_id;
END;
$$;