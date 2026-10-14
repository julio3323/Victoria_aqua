<?php
// Cadastrar.php — recebe nome, email e senha do app e grava no banco
// Responde sempre em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "...", "id": numero }

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

// Campos enviados pelo app via Retrofit (POST, formato de formulário)
$nome  = $_POST['nome']  ?? '';
$email = $_POST['email'] ?? '';
$senha = $_POST['senha'] ?? '';

// Validação no servidor (defesa em profundidade, além da que o app já faz)
if ($nome === '' || $email === '' || $senha === '') {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Preencha todos os campos!']);
    exit;
}

try {
    // 1) Verifica se o e-mail já foi cadastrado
    $busca = $pdo->prepare('SELECT id FROM usuarios WHERE email = ?');
    $busca->execute([$email]);

    if ($busca->fetch()) {
        echo json_encode(['status' => 'erro', 'mensagem' => 'E-mail já cadastrado!']);
        exit;
    }

    // 2) Criptografa a senha — NUNCA salvar senha em texto puro
    $hash = password_hash($senha, PASSWORD_DEFAULT);

    // 3) Insere o novo usuário
    $insere = $pdo->prepare('INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)');
    $insere->execute([$nome, $email, $hash]);

    echo json_encode(
        [
            'status'   => 'sucesso',
            'mensagem' => 'Usuário cadastrado com sucesso!',
            'id'       => (int) $pdo->lastInsertId()
        ],
        JSON_UNESCAPED_UNICODE
    );

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
