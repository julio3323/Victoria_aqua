<?php
// ExcluirLeitura.php — apaga uma leitura do histórico do usuário
//
// Recebe via POST: id, usuario_id
//
// Responde em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "..." }
//
// O WHERE confere id E usuario_id: um usuário só pode excluir
// leituras que pertencem a ele mesmo.

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

$id        = $_POST['id'] ?? '';
$usuarioId = $_POST['usuario_id'] ?? '';

if ($id === '' || !ctype_digit($id)
        || $usuarioId === '' || !ctype_digit($usuarioId)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Leitura ou usuário não informados.']);
    exit;
}

try {
    $delete = $pdo->prepare(
        'DELETE FROM leituras WHERE id = ? AND usuario_id = ?'
    );

    $delete->execute([
        (int) $id,
        (int) $usuarioId
    ]);

    if ($delete->rowCount() > 0) {
        echo json_encode(['status' => 'sucesso', 'mensagem' => 'Leitura excluída!']);
    } else {
        echo json_encode(['status' => 'erro', 'mensagem' => 'Leitura não encontrada.']);
    }

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
