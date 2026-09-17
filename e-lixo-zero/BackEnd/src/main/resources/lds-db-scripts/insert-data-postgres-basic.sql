-- senha para todos: 123456 (hash BCrypt)
INSERT INTO users (full_name, email, password, user_type)
VALUES ('João Silva', 'joao@gmail.com', '$2a$10$7S1Zp5vdEH6YRylYp3sdcezcMwUZyABGxMt1uFJ9qevBTI3sqiHO6', 'CITIZEN');

INSERT INTO users (full_name, email, password, user_type, city)
VALUES ('Maria Santos', 'maria@gmail.com', '$2a$10$7S1Zp5vdEH6YRylYp3sdcezcMwUZyABGxMt1uFJ9qevBTI3sqiHO6', 'CITIZEN', 'Cachoeira de Minas');

INSERT INTO users (full_name, email, password, user_type)
VALUES ('Administrador', 'admin@elixozero.com', '$2a$10$7S1Zp5vdEH6YRylYp3sdcezcMwUZyABGxMt1uFJ9qevBTI3sqiHO6', 'ADMIN');

INSERT INTO users (full_name, email, password, user_type)
VALUES ('Carlos Coletor', 'coletor@elixozero.com', '$2a$10$7S1Zp5vdEH6YRylYp3sdcezcMwUZyABGxMt1uFJ9qevBTI3sqiHO6', 'COLLECTOR');

-- tipos de resíduo (ids 1 a 8, na ordem)
INSERT INTO waste_types (name, category, description)
VALUES ('Celulares', 'Eletrônicos', 'Telefones celulares e smartphones');

INSERT INTO waste_types (name, category, description)
VALUES ('Computadores', 'Eletrônicos', 'Computadores desktop e laptops');

INSERT INTO waste_types (name, category, description)
VALUES ('Pilhas e baterias', 'Perigosos', 'Pilhas e baterias de diversos tipos');

INSERT INTO waste_types (name, category, description)
VALUES ('Monitores', 'Eletrônicos', 'Monitores de tubo, LCD e LED');

INSERT INTO waste_types (name, category, description)
VALUES ('Cabos e carregadores', 'Eletrônicos', 'Cabos, fontes e carregadores em geral');

INSERT INTO waste_types (name, category, description)
VALUES ('Impressoras', 'Eletrônicos', 'Impressoras, scanners e multifuncionais');

INSERT INTO waste_types (name, category, description)
VALUES ('Teclados', 'Eletrônicos', 'Teclados de computador');

INSERT INTO waste_types (name, category, description)
VALUES ('Mouses', 'Eletrônicos', 'Mouses com e sem fio');

-- pontos de coleta: Santa Rita do Sapucaí e Cachoeira de Minas
INSERT INTO collection_points (name, street, number, neighborhood, city, state, phone, opening_hours, accepted_waste_types, latitude, longitude)
VALUES ('EcoPoint Centro', 'Rua Principal', '100', 'Centro', 'Santa Rita do Sapucaí', 'MG', '3534712345', 'Seg-Sex 8h-18h', 'Celulares,Computadores,Pilhas e baterias,Cabos e carregadores', -22.2522, -45.7034);

INSERT INTO collection_points (name, street, number, neighborhood, city, state, phone, opening_hours, accepted_waste_types, latitude, longitude)
VALUES ('EcoPoint Zona Sul', 'Avenida Sul', '200', 'Zona Sul', 'Santa Rita do Sapucaí', 'MG', '3534712346', 'Seg-Sex 9h-17h', 'Celulares,Pilhas e baterias', -22.2650, -45.6980);

INSERT INTO collection_points (name, street, number, neighborhood, city, state, phone, opening_hours, accepted_waste_types, latitude, longitude)
VALUES ('Ponto Verde Bela Vista', 'Rua das Flores', '45', 'Bela Vista', 'Santa Rita do Sapucaí', 'MG', '3534720010', 'Seg-Sáb 8h-17h', 'Computadores,Monitores,Impressoras,Teclados,Mouses', -22.2460, -45.7120);

INSERT INTO collection_points (name, street, number, neighborhood, city, state, phone, opening_hours, accepted_waste_types, latitude, longitude)
VALUES ('Recicla SRS', 'Avenida das Indústrias', '900', 'Distrito Industrial', 'Santa Rita do Sapucaí', 'MG', '3534720090', 'Seg-Sex 7h30-17h30', 'Celulares,Computadores,Pilhas e baterias,Monitores,Cabos e carregadores,Impressoras,Teclados,Mouses', -22.2380, -45.6900);

INSERT INTO collection_points (name, street, number, neighborhood, city, state, phone, opening_hours, accepted_waste_types, latitude, longitude)
VALUES ('EcoPoint Cachoeira Centro', 'Praça da Matriz', '10', 'Centro', 'Cachoeira de Minas', 'MG', '3535630100', 'Seg-Sex 8h-17h', 'Celulares,Computadores,Pilhas e baterias,Monitores', -22.3556, -45.7794);

INSERT INTO collection_points (name, street, number, neighborhood, city, state, phone, opening_hours, accepted_waste_types, latitude, longitude)
VALUES ('ColetaFácil Cachoeira', 'Rua do Comércio', '320', 'Centro', 'Cachoeira de Minas', 'MG', '3535630320', 'Seg-Sáb 8h-12h', 'Celulares,Pilhas e baterias,Cabos e carregadores', -22.3520, -45.7830);

-- coletas do João (user_id 1)
INSERT INTO pickup_requests (user_id, waste_type_id, street, number, neighborhood, city, state, estimated_quantity, desired_date, status, notes)
VALUES (1, 1, 'Rua das Acácias', '120', 'Centro', 'Santa Rita do Sapucaí', 'MG', '3', '2026-09-20', 'Scheduled', 'Manhã');

INSERT INTO pickup_requests (user_id, waste_type_id, street, number, neighborhood, city, state, estimated_quantity, desired_date, status, notes)
VALUES (1, 3, 'Rua XV de Novembro', '55', 'Bela Vista', 'Santa Rita do Sapucaí', 'MG', '10', '2026-09-05', 'Completed', 'Tarde');

INSERT INTO pickup_requests (user_id, waste_type_id, street, number, neighborhood, city, state, estimated_quantity, desired_date, status, notes)
VALUES (1, 2, 'Avenida Brasil', '300', 'Centro', 'Santa Rita do Sapucaí', 'MG', '2', '2026-09-22', 'In Progress', 'Tarde');

-- coletas da Maria (user_id 2)
INSERT INTO pickup_requests (user_id, waste_type_id, street, number, neighborhood, city, state, estimated_quantity, desired_date, status, notes)
VALUES (2, 4, 'Rua do Rosário', '88', 'Centro', 'Cachoeira de Minas', 'MG', '2', '2026-09-21', 'Scheduled', 'Noite');

INSERT INTO pickup_requests (user_id, waste_type_id, street, number, neighborhood, city, state, estimated_quantity, desired_date, status, notes)
VALUES (2, 6, 'Rua São João', '15', 'Centro', 'Cachoeira de Minas', 'MG', '1', '2026-09-02', 'Completed', 'Manhã');

-- notificações do João
INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (1, 'Coleta agendada', 'Sua coleta de Celulares foi agendada para 20/09/2026 no período da manhã.', 'INFO', false);

INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (1, 'Coleta em andamento', 'A coleta de Computadores está a caminho do endereço informado.', 'INFO', false);

INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (1, 'Coleta concluída', 'Sua coleta de Pilhas e baterias foi concluída. Obrigado por descartar corretamente!', 'INFO', true);

INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (1, 'Dica ambiental', 'Pilhas e baterias nunca devem ir para o lixo comum. Leve-as a um ponto de coleta.', 'TIP', false);

-- notificações da Maria
INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (2, 'Coleta agendada', 'Sua coleta de Monitores foi agendada para 21/09/2026 no período da noite.', 'INFO', false);

INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (2, 'Coleta concluída', 'Sua coleta de Impressoras foi concluída. Obrigada por descartar corretamente!', 'INFO', true);

INSERT INTO notifications (user_id, title, message, notification_type, is_read)
VALUES (2, 'Bem-vindo(a) ao E-Lixo Zero!', 'Olá, Maria! Encontre pontos de coleta em Cachoeira de Minas e agende coletas residenciais.', 'INFO', false);
