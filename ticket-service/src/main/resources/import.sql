-- Inserting Users (Clients and Support)
INSERT INTO tb_users (name, email, password, role) VALUES ('John Doe', 'john@client.com', '123456', 'CLIENT');
INSERT INTO tb_users (name, email, password, role) VALUES ('Jane Smith', 'jane@client.com', '123456', 'CLIENT');
INSERT INTO tb_users (name, email, password, role) VALUES ('Tech Guru', 'guru@support.com', 'admin123', 'SUPPORT');

-- Inserting initial Tickets
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, created_at, updated_at)
VALUES ('System crashing on login', 'Every time I try to login, the screen goes blank.', 'OPEN', 'HIGH', 'BUG', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at, updated_at)
VALUES ('Invoice calculation error', 'My monthly invoice is showing double the amount.', 'IN_PROGRESS', 'MEDIUM', 'FINANCE', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserting some Messages (Chat history)
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at)
VALUES ('Please fix this ASAP, I need to pay it today.', false, 2, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at)
VALUES ('Checked the database, it is a duplicate entry issue. Working on a script to clean it up.', true, 2, 3, CURRENT_TIMESTAMP);