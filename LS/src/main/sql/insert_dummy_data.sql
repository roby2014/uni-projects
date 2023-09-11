-- Insert users
INSERT INTO users (id, name, email, password, token)
VALUES
    (1, 'roby', 'roby@gmail.com', '12345', 'token123'),
    (2, 'joao', 'joao@hotmail.com', '12345', 'token123');

-- Insert boards
INSERT INTO boards (id, name, description)
VALUES
    (1, 'Project A', 'This is a project board'),
    (2, 'Project B', 'This is another project board');

-- Insert board users
INSERT INTO board_users (board_id, user_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 1);

-- Insert lists
INSERT INTO lists (id, board_id, name, position)
VALUES
    (1, 1, 'Todo', 1),
    (2, 1, 'In Progress', 2),
    (3, 2, 'Todo', 1);

-- Insert cards
INSERT INTO cards (id, name, description, creation_date, conclusion_date)
VALUES
    (1, 'Task 1', 'Do homework', '2023-06-02', null),
    (3, 'Task 3', 'Cook', '2023-01-11', null),
    (5, 'Task 5', 'Go Skate', '2023-01-30', null);

-- Insert list cards
INSERT INTO list_cards (list_id, card_id, index)
VALUES
    (1, 1, 0),
    (1, 3, 1),
    (2, 3, 0),
    (3, 5, 0);

-- https://hcmc.uvic.ca/blogs/index.php/how_to_fix_postgresql_error_duplicate_ke?blog=22
SELECT setval('public.users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('public.boards_id_seq', (SELECT MAX(id) FROM boards));
SELECT setval('public.lists_id_seq', (SELECT MAX(id) FROM lists));
SELECT setval('public.cards_id_seq', (SELECT MAX(id) FROM cards));