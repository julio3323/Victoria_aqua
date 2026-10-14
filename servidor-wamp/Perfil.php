<?php
// Perfil.php — recebe o id do usuário logado e devolve todos os dados do perfil:
// nome, email, fotos (base64), bio e os metadados (cidade, profissão, website…).
//
// Responde sempre em JSON no formato que a classe Resposta.java espera:
// { "status": "sucesso"|"erro", "mensagem": "...", "id": numero,
//   "nome": "...", "email": "...", "avatar": base64|null, "banner": base64|null,
//   "cidade": "...|null", "profissao": "...|null", "website": "...|null",
//   "telefone": "...|null", "data_nascimento": "...|null", "bio": "...|null" }

header('Content-Type: application/json; charset=utf-8');

require 'conexao.php';

// Id enviado pelo app via Retrofit (POST, formato de formulário)
$id = $_POST['id'] ?? '';

// Precisa ser um número inteiro válido
if ($id === '' || !ctype_digit($id)) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não informado.']);
    exit;
}

try {
    // Busca os dados do perfil (a senha NUNCA sai do banco)
    $busca = $pdo->prepare(
        'SELECT nome, email, avatar, banner, bio,
                cidade, profissao, website, telefone, data_nascimento
         FROM usuarios
         WHERE id = ?'
    );
    $busca->execute([(int) $id]);

    $usuario = $busca->fetch();

    if ($usuario) {
        echo json_encode(
            [
                'status'          => 'sucesso',
                'mensagem'        => 'Perfil carregado!',
                'id'              => (int) $id,
                'nome'            => $usuario['nome'],
                'email'           => $usuario['email'],
                'avatar'          => $usuario['avatar'],           // base64 ou null
                'banner'          => $usuario['banner'],           // base64 ou null
                'bio'             => $usuario['bio'],
                'cidade'          => $usuario['cidade'],
                'profissao'       => $usuario['profissao'],
                'website'         => $usuario['website'],
                'telefone'        => $usuario['telefone'],
                'data_nascimento' => $usuario['data_nascimento']
            ],
            JSON_UNESCAPED_UNICODE
        );
    } else {
        echo json_encode(['status' => 'erro', 'mensagem' => 'Usuário não encontrado.']);
    }

} catch (Exception $e) {
    echo json_encode(['status' => 'erro', 'mensagem' => 'Erro no servidor: ' . $e->getMessage()]);
}
