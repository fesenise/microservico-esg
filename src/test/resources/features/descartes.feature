# language: pt
@descartes
Funcionalidade: Registro, consulta e exclusão de descartes
  Como usuário autenticado
  Quero registrar resíduos descartados em pontos cadastrados
  Para manter o histórico de reciclagem

  @positivo
  Cenário: Registrar descarte válido
    Dado que existem usuário e ponto de coleta para descarte
    Quando registro um descarte válido
    Então a resposta deve ter status 201
    E o JSON deve respeitar o contrato "descarte.schema.json"
    E a resposta identifica o descarte e seus vínculos

  @positivo
  Cenário: Listar descartes cadastrados
    Dado que existe um descarte cadastrado para os testes
    Quando consulto a lista de descartes
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "descartes-lista.schema.json"
    E a lista contém o descarte criado

  @positivo
  Cenário: Consultar descarte pelo ID
    Dado que existe um descarte cadastrado para os testes
    Quando consulto o descarte pelo ID
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "descarte.schema.json"
    E a resposta identifica o descarte e seus vínculos

  @negativo
  Cenário: Consultar descarte inexistente
    Dado que estou autenticado na API ESG
    Quando consulto um descarte inexistente
    Então a resposta deve ter status 404
    E o corpo da resposta HTTP deve estar vazio

  @negativo @validacao
  Cenário: Rejeitar descarte com peso zero
    Dado que existem usuário e ponto de coleta para descarte
    Quando registro um descarte com peso zero
    Então a resposta deve ter status 400
    E o JSON deve respeitar o contrato "erro.schema.json"
    E a mensagem de erro deve mencionar "pesoKg"

  @negativo @integridade
  Cenário: Rejeitar descarte para usuário inexistente
    Dado que existem usuário e ponto de coleta para descarte
    Quando registro um descarte com usuário inexistente
    Então a resposta deve ter status 400
    E o JSON deve respeitar o contrato "erro.schema.json"
    E a mensagem de erro deve mencionar "usuário"

  @negativo @integridade
  Cenário: Rejeitar descarte para ponto de coleta inexistente
    Dado que existem usuário e ponto de coleta para descarte
    Quando registro um descarte com ponto de coleta inexistente
    Então a resposta deve ter status 400
    E o JSON deve respeitar o contrato "erro.schema.json"
    E a mensagem de erro deve mencionar "ponto de coleta"

  @positivo
  Cenário: Excluir descarte cadastrado
    Dado que existe um descarte cadastrado para os testes
    Quando excluo o descarte cadastrado
    Então a resposta deve ter status 204
    E o corpo da resposta HTTP deve estar vazio
    E o descarte excluído não deve mais ser encontrado

  @negativo
  Cenário: Excluir descarte inexistente
    Dado que estou autenticado na API ESG
    Quando tento excluir um descarte inexistente
    Então a resposta deve ter status 404
    E o corpo da resposta HTTP deve estar vazio

  @seguranca @negativo
  Cenário: Bloquear consulta de descartes sem token
    Quando consulto os descartes sem token
    Então a resposta deve ter status 401
