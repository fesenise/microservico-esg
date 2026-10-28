# language: pt
@autenticacao
Funcionalidade: Autenticação JWT e proteção das APIs
  Como usuário cadastrado
  Quero me autenticar e acessar apenas recursos autorizados
  Para utilizar o microsserviço ESG com segurança

  @positivo
  Cenário: Autenticar com credenciais corretas
    Dado que existe um usuário para autenticação
    Quando faço login com minhas credenciais corretas
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "token.schema.json"
    E o token JWT retornado deve estar preenchido

  @negativo
  Cenário: Rejeitar login com senha incorreta
    Dado que existe um usuário para autenticação
    Quando faço login com uma senha incorreta
    Então a resposta deve ter status 401
    E o JSON deve respeitar o contrato "erro-login.schema.json"
    E a mensagem de erro deve mencionar "inválidos"

  @seguranca @negativo
  Cenário: Bloquear consulta de usuários sem token
    Quando consulto usuários sem apresentar um token
    Então a resposta deve ter status 401

  @seguranca @positivo
  Cenário: Permitir acesso à API protegida com JWT válido
    Dado que estou autenticado na API ESG
    Quando consulto usuários com meu token JWT
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "usuarios-lista.schema.json"
