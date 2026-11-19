-- Script de Criação e Inicialização do Banco de Dados Poupatempo

CREATE TABLE IF NOT EXISTS tipo_atendimento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    sigla VARCHAR(10) NOT NULL,
    documentos VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS guiche (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS guiche_tipo_atendimento (
    guiche_id INTEGER NOT NULL,
    tipo_atendimento_id INTEGER NOT NULL,
    PRIMARY KEY (guiche_id, tipo_atendimento_id),
    FOREIGN KEY (guiche_id) REFERENCES guiche(id),
    FOREIGN KEY (tipo_atendimento_id) REFERENCES tipo_atendimento(id)
);

CREATE TABLE IF NOT EXISTS senha (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_formatado VARCHAR(20) NOT NULL,
    tipo_atendimento_id INTEGER NOT NULL,
    prioritario INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    instante_emissao VARCHAR(30) NOT NULL,
    instante_chamada VARCHAR(30),
    instante_inicio_atendimento VARCHAR(30),
    instante_fim_atendimento VARCHAR(30),
    guiche_id INTEGER,
    FOREIGN KEY (tipo_atendimento_id) REFERENCES tipo_atendimento(id),
    FOREIGN KEY (guiche_id) REFERENCES guiche(id)
);

-- Carga inicial de tipos de atendimento (mínimo de 3 tipos)
INSERT OR IGNORE INTO tipo_atendimento (id, nome, sigla, documentos) VALUES (1, 'CIN - Carteira de Identidade', 'CIN', 'Certidão de Nascimento');
INSERT OR IGNORE INTO tipo_atendimento (id, nome, sigla, documentos) VALUES (2, 'CNH - Carteira de Motorista', 'CNH', 'Documento com foto,Comprovante de Residência');
INSERT OR IGNORE INTO tipo_atendimento (id, nome, sigla, documentos) VALUES (3, 'Veículos - Licenciamento', 'VEI', 'Documento com foto,Quitação de débitos');

-- Carga inicial de guichês
INSERT OR IGNORE INTO guiche (id, numero) VALUES (1, 1);
INSERT OR IGNORE INTO guiche (id, numero) VALUES (2, 2);
INSERT OR IGNORE INTO guiche (id, numero) VALUES (3, 3);

-- Configuração de tipos de atendimento por guichê
-- Guichê 1 atende CIN e CNH
INSERT OR IGNORE INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (1, 1);
INSERT OR IGNORE INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (1, 2);

-- Guichê 2 atende Todos (CIN, CNH e Veículos)
INSERT OR IGNORE INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (2, 1);
INSERT OR IGNORE INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (2, 2);
INSERT OR IGNORE INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (2, 3);

-- Guichê 3 atende apenas Veículos
INSERT OR IGNORE INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (3, 3);

-- Carga inicial de senhas demonstrativas
INSERT OR IGNORE INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
VALUES (1, 'CIN-001', 1, 0, 'FINALIZADA', '2026-11-15 08:30:00', '2026-11-15 08:35:00', '2026-11-15 08:36:00', '2026-11-15 08:50:00', 1);

INSERT OR IGNORE INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
VALUES (2, 'CNH-P001', 2, 1, 'FINALIZADA', '2026-11-15 08:40:00', '2026-11-15 08:45:00', '2026-11-15 08:46:00', '2026-11-15 09:05:00', 2);

INSERT OR IGNORE INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
VALUES (3, 'VEI-001', 3, 0, 'AGUARDANDO', '2026-11-15 09:10:00', NULL, NULL, NULL, NULL);

INSERT OR IGNORE INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
VALUES (4, 'CIN-P002', 1, 1, 'AGUARDANDO', '2026-11-15 09:15:00', NULL, NULL, NULL, NULL);

INSERT OR IGNORE INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
VALUES (5, 'CNH-002', 2, 0, 'AGUARDANDO', '2026-11-15 09:20:00', NULL, NULL, NULL, NULL);
