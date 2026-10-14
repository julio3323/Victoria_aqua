<?php
// AtualizarLeitura.php — salva as alterações de uma leitura do histórico
//
// Recebe via POST: id, usuario_id, categoria, leitura_anterior,
//                  leitura_atual, consumo, valor_agua, valor_esgoto, valor_total
//
// Responde em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "..." }
//
// O WHERE confere id E usuario_id: um usuário só pode editar
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

// Campos da leitura (os valores são recalculados pelo app)
$categoria       = trim($_POST['categoria'] ?? '');
$leituraAnterior = $_POST['leitura_anterior'] ?? '';
$leituraAtual    = $_POST['leitura_atual'] ?? '';
$consumo         = $_POST['consumo'] ?? '';
$valorAgua       = $_POST['valor_agua'] ?? '';
$valorEsgoto     = $_POST['valor_esgoto'] ?? '';
$valorTotal      = $_POST['valor_total'] ?? '';

// Validação no servidor (defesa em profundidade, além da que o app já faz)
if ($categoria === '') {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Dados da leitura incompletos.']);
    exit;
}

if (!is_numeric($leituraAnterior) || !is_numeric($leituraAtual)
        || !is_numeric($consumo) || !is_numeric($valorAgua)
        || !is_numeric($valorEsgoto) || !is_numeric($valorTotal)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Valores da leitura inválidos.']);
    exit;
}

if ((float) $leituraAnterior > (float) $leituraAtual) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'A leitura anterior não pode ser maior que a atual.']);
    exit;
}

if (mb_strlen($categoria) > 30) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Algum campo é longo demais.']);
    exit;
}

try {
    $update = $pdo->prepare(
        'UPDATE leituras
         SET categoria = ?,
             leitura_anterior = ?,
             leitura_atual = ?,
             consumo = ?,
             valor_agua = ?,
             valor_esgoto = ?,
             valor_total = ?
         WHERE id = ? AND usuario_id = ?'
    );

    $update->execute([
        $categoria,
        (float) $leituraAnterior,
        (float) $leituraAtual,
        (float) $consumo,
        (float) $valorAgua,
        (float) $valorEsgoto,
        (float) $valorTotal,
        (int) $id,
        (int) $usuarioId
    ]);

    if ($update->rowCount() > 0) {
        echo json_encode(['status' => 'sucesso', 'mensagem' => 'Leitura atualizada!']);
    } else {
        // rowCount 0: nada mudou OU leitura não é do usuário — verifica qual
        $busca = $pdo->prepare(
            'SELECT id FROM leituras WHERE id = ? AND usuario_id = ?'
        );
        $busca->execute([(int) $id, (int) $usuarioId]);

        if ($busca->fetch()) {
            echo json_encode(['status' => 'sucesso', 'mensagem' => 'Leitura atualizada!']);
        } else {
            echo json_encode(['status' => 'erro', 'mensagem' => 'Leitura não encontrada.']);
        }
    }

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
