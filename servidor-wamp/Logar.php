<?php
// Logar.php — valida e-mail e senha e devolve o id do usuário logado
// Responde sempre em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "...", "id": numero }

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

$email = $_POST['email'] ?? '';
$senha = $_POST['senha'] ?? '';

if ($email === '' || $senha === '') {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Preencha todos os campos!']);
    exit;
}

try {
    // Busca o usuário pelo e-mail (prepared statement = proteção contra SQL Injection)
    $busca = $pdo->prepare('SELECT id, senha FROM usuarios WHERE email = ?');
    $busca->execute([$email]);
    $usuario = $busca->fetch();

    // password_verify compara a senha digitada com o hash salvo no cadastro
    if ($usuario && password_verify($senha, $usuario['senha'])) {
        echo json_encode(
            [
                'status'   => 'sucesso',
                'mensagem' => 'Login realizado com sucesso!',
                'id'       => (int) $usuario['id']
            ],
            JSON_UNESCAPED_UNICODE
        );
    } else {
        echo json_encode(['status' => 'erro', 'mensagem' => 'E-mail ou senha inválidos!']);
    }

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
