<?php
// ListarLeituras.php — devolve o histórico de leituras do usuário
//
// Recebe via POST: usuario_id
//
// Responde em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "...",
//   "leituras": [ { "id": 1, "data_registro": "05/02/2026", "categoria": "Residencial",
//                   "leitura_anterior": 120, "leitura_atual": 135, "consumo": 15,
//                   "valor_agua": 96.82, "valor_esgoto": 96.82, "valor_total": 193.64 }, ... ] }
//
// A lista vem ordenada pela mais recente (maior id primeiro) — a mesma
// ordem que o app usa no histórico (a mais recente fica no topo).

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

$usuarioId = $_POST['usuario_id'] ?? '';

if ($usuarioId === '' || !ctype_digit($usuarioId)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não informado.']);
    exit;
}

try {
    $busca = $pdo->prepare(
        'SELECT id, data_registro, categoria,
                leitura_anterior, leitura_atual, consumo,
                valor_agua, valor_esgoto, valor_total
         FROM leituras
         WHERE usuario_id = ?
         ORDER BY id DESC'
    );

    $busca->execute([(int) $usuarioId]);

    $leituras = [];

    // (float) converte os DECIMAL do MySQL, que chegam como texto
    while ($linha = $busca->fetch()) {
        $leituras[] = [
            'id'               => (int) $linha['id'],
            'data_registro'    => $linha['data_registro'],
            'categoria'        => $linha['categoria'],
            'leitura_anterior' => (float) $linha['leitura_anterior'],
            'leitura_atual'    => (float) $linha['leitura_atual'],
            'consumo'          => (float) $linha['consumo'],
            'valor_agua'       => (float) $linha['valor_agua'],
            'valor_esgoto'     => (float) $linha['valor_esgoto'],
            'valor_total'      => (float) $linha['valor_total']
        ];
    }

    echo json_encode(
        [
            'status'   => 'sucesso',
            'mensagem' => 'Histórico carregado!',
            'leituras' => $leituras
        ],
        JSON_UNESCAPED_UNICODE
    );

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
