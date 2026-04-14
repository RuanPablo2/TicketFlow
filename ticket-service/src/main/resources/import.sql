-- Inserting Users (Clients and Support)
INSERT INTO tb_users (name, email, password, user_role) VALUES ('John Doe', 'john@client.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'CLIENT');
INSERT INTO tb_users (name, email, password, user_role) VALUES ('Jane Smith', 'jane@client.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'CLIENT');
INSERT INTO tb_users (name, email, password, user_role) VALUES ('Tech Guru', 'guru@support.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'SUPPORT');

-- Inserting initial Tickets
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, created_at, updated_at) VALUES ('System crashing on login', 'Every time I try to login, the screen goes blank.', 'OPEN', 'HIGH', 'BUG', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at, updated_at) VALUES ('Invoice calculation error', 'My monthly invoice is showing double the amount.', 'IN_PROGRESS', 'MEDIUM', 'FINANCE', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserting some Messages (Chat history)
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, created_at, updated_at) VALUES ('System crashing on login', 'Every time I try to login...', 'OPEN', 'HIGH', 'BUG', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('Please fix this ASAP', false, 2, 2, CURRENT_TIMESTAMP);