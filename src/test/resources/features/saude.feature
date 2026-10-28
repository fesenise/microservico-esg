# language: pt
@saude
Funcionalidade: Disponibilidade do microsserviço ESG
  Como sistema de monitoramento
  Quero consultar o estado da aplicação
  Para identificar se ela está operacional

  @positivo
  Cenário: Consultar estado de saúde sem autenticação
    Quando consulto a saúde do microsserviço sem autenticação
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "health.schema.json"
    E o estado do microsserviço deve estar UP
