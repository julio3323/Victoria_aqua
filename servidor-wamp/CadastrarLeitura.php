<?php
// CadastrarLeitura.php — grava uma nova leitura no histórico do usuário
//
// Recebe via POST (formulário, enviado pelo Retrofit):
//   usuario_id, data_registro, categoria,
//   leitura_anterior, leitura_atual, consumo,
//   valor_agua, valor_esgoto, valor_total
//
// Responde em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "...", "id": id_da_leitura }
// O id volta para o app guardar na Leitura — é ele que permite
// editar e excluir a leitura depois.

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

$usuarioId = $_POST['usuario_id'] ?? '';

if ($usuarioId === '' || !ctype_digit($usuarioId)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não informado.']);
    exit;
}

// Campos da leitura (os valores são calculados pelo app)
$dataRegistro    = trim($_POST['data_registro'] ?? '');
$categoria       = trim($_POST['categoria'] ?? '');
$leituraAnterior = $_POST['leitura_anterior'] ?? '';
$leituraAtual    = $_POST['leitura_atual'] ?? '';
$consumo         = $_POST['consumo'] ?? '';
$valorAgua       = $_POST['valor_agua'] ?? '';
$valorEsgoto     = $_POST['valor_esgoto'] ?? '';
$valorTotal      = $_POST['valor_total'] ?? '';

// Validação no servidor (defesa em profundidade, além da que o app já faz)
if ($dataRegistro === '' || $categoria === '') {
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

// Limites defensivos (iguais aos tamanhos das colunas no banco)
if (mb_strlen($dataRegistro) > 10 || mb_strlen($categoria) > 30) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Algum campo é longo demais.']);
    exit;
}

try {
    // Confere se o usuário existe (leitura sempre pertence a alguém)
    $busca = $pdo->prepare('SELECT id FROM usuarios WHERE id = ?');
    $busca->execute([(int) $usuarioId]);

    if (!$busca->fetch()) {
        echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não encontrado.']);
        exit;
    }

    // Insere a leitura (prepared statement = proteção contra SQL Injection)
    $insere = $pdo->prepare(
        'INSERT INTO leituras
             (usuario_id, data_registro, categoria,
              leitura_anterior, leitura_atual, consumo,
              valor_agua, valor_esgoto, valor_total)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)'
    );

    $insere->execute([
        (int) $usuarioId,
        $dataRegistro,
        $categoria,
        (float) $leituraAnterior,
        (float) $leituraAtual,
        (float) $consumo,
        (float) $valorAgua,
        (float) $valorEsgoto,
        (float) $valorTotal
    ]);

    echo json_encode(
        [
            'status'   => 'sucesso',
            'mensagem' => 'Leitura salva com sucesso!',
            'id'       => (int) $pdo->lastInsertId()
        ],
        JSON_UNESCAPED_UNICODE
    );

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
