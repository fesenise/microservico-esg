# language: pt
@pontos_coleta
Funcionalidade: Cadastro, consulta e exclusão de pontos de coleta
  Como usuário autenticado
  Quero gerenciar pontos de coleta de resíduos
  Para identificar locais adequados para reciclagem

  @positivo
  Cenário: Cadastrar ponto de coleta válido
    Dado que estou autenticado na API ESG
    Quando cadastro um novo ponto de coleta
    Então a resposta deve ter status 201
    E o JSON deve respeitar o contrato "ponto-coleta.schema.json"
    E a resposta identifica o ponto de coleta criado

  @positivo
  Cenário: Listar pontos de coleta cadastrados
    Dado que existe um ponto de coleta cadastrado para os testes
    Quando consulto a lista de pontos de coleta
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "pontos-coleta-lista.schema.json"
    E a lista contém o ponto de coleta criado

  @positivo
  Cenário: Consultar um ponto de coleta pelo ID
    Dado que existe um ponto de coleta cadastrado para os testes
    Quando consulto o ponto de coleta pelo ID
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "ponto-coleta.schema.json"
    E a resposta identifica o ponto de coleta criado

  @negativo
  Cenário: Consultar ponto de coleta inexistente
    Dado que estou autenticado na API ESG
    Quando consulto um ponto de coleta inexistente
    Então a resposta deve ter status 404
    E o corpo da resposta HTTP deve estar vazio

  @negativo @validacao
  Cenário: Rejeitar estado com mais de duas letras
    Dado que estou autenticado na API ESG
    Quando cadastro ponto de coleta com estado inválido
    Então a resposta deve ter status 400
    E o JSON deve respeitar o contrato "erro.schema.json"
    E a mensagem de erro deve mencionar "estado"

  @positivo
  Cenário: Excluir ponto de coleta existente
    Dado que existe um ponto de coleta cadastrado para os testes
    Quando excluo o ponto de coleta cadastrado
    Então a resposta deve ter status 204
    E o corpo da resposta HTTP deve estar vazio
    E o ponto excluído não deve mais ser encontrado

  @negativo
  Cenário: Excluir ponto de coleta inexistente
    Dado que estou autenticado na API ESG
    Quando tento excluir um ponto de coleta inexistente
    Então a resposta deve ter status 404
    E o corpo da resposta HTTP deve estar vazio

  @seguranca @negativo
  Cenário: Bloquear consulta de pontos de coleta sem token
    Quando consulto os pontos de coleta sem token
    Então a resposta deve ter status 401
