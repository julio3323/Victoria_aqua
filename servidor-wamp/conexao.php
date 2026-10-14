<?php
// conexao.php — conexão com o MySQL (padrão do WAMP: usuário root, sem senha)

$host     = 'localhost';
$banco    = 'app_aqua';
$usuario  = 'root';
$senhaBd  = ''; // WAMP não define senha para o root por padrão

try {
    $pdo = new PDO(
        "mysql:host={$host};dbname={$banco};charset=utf8mb4",
        $usuario,
        $senhaBd,
        [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,  // erros viram exceções
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC         // fetch devolve array simples
        ]
    );
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'erro', 'mensagem' => 'Falha ao conectar ao banco de dados']);
    exit;
}
