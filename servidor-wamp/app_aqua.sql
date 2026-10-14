-- Banco de dados do App Aqua (WAMP Server / phpMyAdmin)
-- Como importar: acesse http://localhost/phpmyadmin → aba "Importar"
-- → selecione este arquivo → clique em "Executar"

CREATE DATABASE IF NOT EXISTS app_aqua
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE app_aqua;

CREATE TABLE IF NOT EXISTS usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,  -- UNIQUE impede e-mails duplicados
    senha           VARCHAR(255) NOT NULL,         -- senha criptografada (password_hash)
    criado_em       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar          MEDIUMTEXT NULL,               -- foto de perfil (base64 JPEG/PNG)
    banner          MEDIUMTEXT NULL,               -- foto do banner (base64 JPEG/PNG)
    bio             TEXT NULL,                     -- descrição do perfil
    cidade          VARCHAR(100) NULL,             -- metadados do perfil (perfilMetaInline)
    profissao       VARCHAR(100) NULL,
    website         VARCHAR(150) NULL,
    telefone        VARCHAR(30) NULL,
    data_nascimento VARCHAR(10) NULL               -- formato dd/MM/yyyy
);

-- Histórico de leituras do Dashboard (uma linha por leitura calculada).
-- Cada leitura pertence a um usuário; o app usa o id para editar/excluir.
CREATE TABLE IF NOT EXISTS leituras (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id       INT NOT NULL,
    data_registro    VARCHAR(10) NOT NULL,        -- formato dd/MM/yyyy
    categoria        VARCHAR(30) NOT NULL,        -- nome da tarifa (ex.: Residencial)
    leitura_anterior DECIMAL(12,2) NOT NULL,       -- m³ marcados no hidrômetro
    leitura_atual    DECIMAL(12,2) NOT NULL,
    consumo          DECIMAL(12,2) NOT NULL,       -- leitura_atual - leitura_anterior
    valor_agua       DECIMAL(12,2) NOT NULL,       -- tarifa calculada pelo app
    valor_esgoto     DECIMAL(12,2) NOT NULL,      -- 100% do valor da água
    valor_total      DECIMAL(12,2) NOT NULL,      -- água + esgoto
    criado_em        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_leituras_usuario (usuario_id)
);
