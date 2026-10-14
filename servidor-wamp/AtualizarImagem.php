<?php
// AtualizarImagem.php — salva a foto de avatar ou banner do usuário no banco
// Recebe: id, tipo ("avatar" ou "banner") e imagem (base64 JPEG/PNG).
// Responde em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "..." }

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

$id     = $_POST['id']     ?? '';
$tipo   = $_POST['tipo']   ?? '';
$imagem = $_POST['imagem'] ?? '';

if ($id === '' || !ctype_digit($id)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não informado.']);
    exit;
}

// Só aceita os dois tipos conhecidos — o nome da coluna é fixo
// (nunca vem do usuário), então não há risco de SQL Injection aqui
if ($tipo !== 'avatar' && $tipo !== 'banner') {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Tipo de imagem inválido.']);
    exit;
}

if ($imagem === '') {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Imagem vazia.']);
    exit;
}

// Limite de segurança: base64 de até 4 MB
if (strlen($imagem) > 4 * 1024 * 1024) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Imagem muito grande.']);
    exit;
}

// Decodifica e confere os "bytes mágicos": JPEG começa com FF D8,
// PNG começa com 89 50 4E 47 — rejeita qualquer coisa que não seja imagem
$dados = base64_decode($imagem, true);

$ehJpeg = ($dados !== false
        && strlen($dados) >= 2
        && ord($dados[0]) === 0xFF
        && ord($dados[1]) === 0xD8);

$ehPng = ($dados !== false
        && strlen($dados) >= 4
        && substr($dados, 0, 4) === "\x89PNG");

if (!$ehJpeg && !$ehPng) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Arquivo de imagem inválido.']);
    exit;
}

try {
    $update = $pdo->prepare("UPDATE usuarios SET {$tipo} = ? WHERE id = ?");
    $update->execute([$imagem, (int) $id]);

    if ($update->rowCount() > 0) {
        echo json_encode(['status' => 'sucesso', 'mensagem' => 'Imagem atualizada!']);
    } else {
        // rowCount 0: imagem idêntica OU usuário inexistente — verifica qual
        $busca = $pdo->prepare('SELECT id FROM usuarios WHERE id = ?');
        $busca->execute([(int) $id]);

        if ($busca->fetch()) {
            echo json_encode(['status' => 'sucesso', 'mensagem' => 'Imagem atualizada!']);
        } else {
            echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não encontrado.']);
        }
    }

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
