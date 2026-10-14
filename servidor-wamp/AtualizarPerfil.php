<?php
// AtualizarPerfil.php — salva a bio e os metadados do perfil
// Recebe: id, bio, cidade, profissao, website, telefone, data_nascimento
// Responde em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "..." }

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

$id = $_POST['id'] ?? '';

if ($id === '' || !ctype_digit($id)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não informado.']);
    exit;
}

// Campos do perfil (todos opcionais — vazio = apaga)
$bio            = trim($_POST['bio'] ?? '');
$cidade         = trim($_POST['cidade'] ?? '');
$profissao      = trim($_POST['profissao'] ?? '');
$website        = trim($_POST['website'] ?? '');
$telefone       = trim($_POST['telefone'] ?? '');
$dataNascimento = trim($_POST['data_nascimento'] ?? '');

// Limites defensivos (iguais aos tamanhos das colunas no banco)
if (mb_strlen($bio) > 1000
        || mb_strlen($cidade) > 100
        || mb_strlen($profissao) > 100
        || mb_strlen($website) > 150
        || mb_strlen($telefone) > 30
        || mb_strlen($dataNascimento) > 10) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Algum campo é longo demais.']);
    exit;
}

try {
    $update = $pdo->prepare(
        'UPDATE usuarios
         SET bio = ?,
             cidade = ?,
             profissao = ?,
             website = ?,
             telefone = ?,
             data_nascimento = ?
         WHERE id = ?'
    );

    $update->execute([
        $bio,
        $cidade,
        $profissao,
        $website,
        $telefone,
        $dataNascimento,
        (int) $id
    ]);

    if ($update->rowCount() > 0) {
        echo json_encode(['status' => 'sucesso', 'mensagem' => 'Perfil atualizado!']);
    } else {
        // rowCount 0: nada mudou OU usuário inexistente — verifica qual
        $busca = $pdo->prepare('SELECT id FROM usuarios WHERE id = ?');
        $busca->execute([(int) $id]);

        if ($busca->fetch()) {
            echo json_encode(['status' => 'sucesso', 'mensagem' => 'Perfil atualizado!']);
        } else {
            echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não encontrado.']);
        }
    }

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
