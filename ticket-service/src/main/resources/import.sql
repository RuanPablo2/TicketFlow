-- USERS

-- SUPPORTS
INSERT INTO tb_users (name, email, password, user_role) VALUES ('Alice Support', 'alice@support.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'SUPPORT');
INSERT INTO tb_users (name, email, password, user_role) VALUES ('Bob Support', 'bob@support.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'SUPPORT');

-- CLIENTS
INSERT INTO tb_users (name, email, password, user_role) VALUES ('John Doe', 'john@client.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'CLIENT');
INSERT INTO tb_users (name, email, password, user_role) VALUES ('Jane Smith', 'jane@client.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'CLIENT');
INSERT INTO tb_users (name, email, password, user_role) VALUES ('Carlos Silva', 'carlos@client.com', '$2a$10$W7YMyzA/rkHo61lr6u0r8eal8QEsGdm5HC/9jNoOD3qi/20pEjItS', 'CLIENT');


-- TICKETS
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, created_at) VALUES ('Monitor wont turn on', 'The power led is blinking but no image appears.', 'OPEN', 'URGENT', 'FINANCE', 4, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, created_at) VALUES ('Need a new mouse', 'Scroll wheel is completely broken.', 'OPEN', 'LOW', 'ACCESS', 3, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at) VALUES ('Cannot install VPN client', 'Getting error code 404 during setup.', 'IN_PROGRESS', 'HIGH', 'QUESTION', 4, 1, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at) VALUES ('Cannot access ERP system', 'Invalid password error but I just changed it today.', 'IN_PROGRESS', 'MEDIUM', 'ACCESS', 4, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at) VALUES ('Wi-Fi dropping constantly', 'My connection drops every 10 minutes during meets.', 'WAITING_CUSTOMER', 'MEDIUM', 'QUESTION', 5, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at) VALUES ('Printer out of toner', 'The HP printer is empty.', 'WAITING_CUSTOMER', 'MEDIUM', 'BUG', 5, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at, closed_at) VALUES ('How to request vacation?', 'Where is the form for PTO?', 'RESOLVED', 'LOW', 'ACCESS', 4, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO tb_tickets (title, description, status, priority, category, client_id, assigned_support_id, created_at, closed_at) VALUES ('Blue screen after Windows update', 'Laptop crashed completely.', 'RESOLVED', 'HIGH', 'BUG', 5, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- MESSAGES
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('I tried running as administrator but it still fails.', false, 2, 5, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('User might have a corrupted registry. Need to check via AnyDesk later.', true, 2, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('Hi Jane, could you please send me the setup log file?', false, 2, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('It drops on both my phone and laptop.', false, 3, 4, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('Hello Carlos. Are you connected to the "Corp-5G" or "Corp-Guest" network?', false, 3, 3, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('The form is available in the HR Portal > Forms > PTO.', false, 4, 2, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('Found it, thank you!', false, 4, 4, CURRENT_TIMESTAMP);
INSERT INTO tb_messages (content, is_internal_note, ticket_id, sender_id, created_at) VALUES ('Could you specify the exact model of the printer? We have two on the 3rd floor.', false, 8, 2, CURRENT_TIMESTAMP);