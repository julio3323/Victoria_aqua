# Servidor local do App Aqua (WAMP + phpMyAdmin)

Estes arquivos substituem o servidor remoto (Railway) pelo **WAMP Server do seu PC**.
O app Android conversa com o PHP, e o PHP conversa com o MySQL — o celular nunca
acessa o banco diretamente.

```
Celular (app)  ──HTTP──▶  Apache (WAMP, porta 80)  ──SQL──▶  MySQL (phpMyAdmin)
                                  │
                        Cadastrar.php / Logar.php / Perfil.php
```

## Arquivos

| Arquivo       | Função                                                    |
|---------------|-----------------------------------------------------------|
| `app_aqua.sql`   | Cria o banco `app_aqua` e as tabelas `usuarios` e `leituras` |
| `conexao.php`    | Conexão com o MySQL (padrão WAMP: root sem senha)      |
| `Cadastrar.php`  | Recebe nome, e-mail e senha → grava no banco           |
| `Logar.php`      | Valida e-mail e senha → devolve o id do usuário        |
| `Perfil.php`     | Recebe o id → devolve nome e e-mail do usuário         |
| `AtualizarPerfil.php` | Salva a bio e os metadados do perfil               |
| `AtualizarImagem.php` | Salva avatar/banner (base64)                      |
| `CadastrarLeitura.php` | Grava uma leitura do histórico → devolve o id dela |
| `ListarLeituras.php`  | Devolve o histórico de leituras do usuário          |
| `AtualizarLeitura.php` | Salva as alterações de uma leitura                |
| `ExcluirLeitura.php`   | Apaga uma leitura do histórico                    |

## Passo a passo

### 1. Criar o banco (phpMyAdmin)
1. Abra o WAMP e espere o ícone ficar **verde** (todos os serviços ativos)
2. No navegador do PC, acesse: `http://localhost/phpmyadmin`
3. Aba **Importar** → escolha o arquivo `app_aqua.sql` → **Executar**
4. O banco `app_aqua` com as tabelas `usuarios` e `leituras` aparecerá na coluna da esquerda

> **Já tinha importado o banco antes?** Importe o `app_aqua.sql` de novo:
> o `CREATE TABLE IF NOT EXISTS` não apaga nada, apenas cria a tabela
> `leituras` que falta (histórico do dashboard). Ou cole só o bloco
> `CREATE TABLE IF NOT EXISTS leituras (...)` na aba **SQL** do phpMyAdmin.

### 2. Instalar os arquivos PHP
Copie os arquivos `.php` para a pasta `C:\wamp64\www\html\login\app_aqua\`
(de preferência mantenha-os também aqui no projeto, para versionar com o app).

> **Importante:** a raiz do `localhost` no seu WAMP aponta para `www/html/login`
> (projeto antigo da aula). Por isso o App Aqua fica em `www/html/login/app_aqua/`.
> O acesso pela rede (celular) foi liberado **somente** para essa pasta no
> `httpd-vhosts.conf`. Se essa configuração for alterada, reinicie o Apache:
> ícone verde do WAMP na bandeja → **Restart All Services**.

### 3. Liberar o firewall do Windows
Na primeira vez que o Apache roda, o Windows costuma perguntar se pode ser
acessado pela rede → clique em **Permitir acesso**. Se não aparecer, rode
um terminal **como administrador** e execute:

```
netsh advfirewall firewall add rule name="WAMP porta 80" dir=in action=allow protocol=TCP localport=80
```

### 4. Celular e PC na MESMA rede Wi-Fi
- Descubra o IP do PC: abra o `cmd` e rode `ipconfig` → veja o **Endereço IPv4**
  (o IP configurado agora é `192.168.0.42`)
- Teste no navegador do celular: `http://192.168.0.42/app_aqua/Logar.php`
- Se aparecer uma página em branco ou um aviso em JSON, a conexão está OK

⚠️ O IP pode mudar se o roteador reiniciar — se o app parar de conectar,
rode `ipconfig` de novo e atualize a `BASE_URL` em `ApiClient.java`.

### 5. Testar direto do navegador (opcional)
Você pode testar o servidor sem o app usando um site como
`https://httpdebugger.com` ou uma requisição via PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost/app_aqua/Logar.php" `
  -Body @{ email = "teste@teste.com"; senha = "123456" }
```

## Como voltar para o servidor remoto (Railway)
Em `ApiClient.java`, troque a `BASE_URL` de volta para:
```
https://julio-s-repository-production.up.railway.app/
```
(e pode remover o `usesCleartextTraffic` do AndroidManifest.xml, se quiser)

## Segurança aplicada
- Senhas salvas **criptografadas** com `password_hash()` (bcrypt) — nunca em texto puro
- **Prepared statements** em todas as consultas — proteção contra SQL Injection
- Validação duplicada: no app **e** no servidor
