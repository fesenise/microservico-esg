# language: pt
@usuarios @consultas
Funcionalidade: Consulta e exclusão de usuários
  Como usuário autenticado
  Quero consultar e excluir cadastros pela API
  Para validar as operações de usuários

  @positivo
  Cenário: Listar usuários cadastrados
    Dado que estou autenticado na API ESG
    Quando listo os usuários cadastrados
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "usuarios-lista.schema.json"
    E a lista contém meu usuário

  @positivo
  Cenário: Buscar usuário existente pelo ID
    Dado que estou autenticado na API ESG
    Quando busco meu usuário pelo ID
    Então a resposta deve ter status 200
    E o JSON deve respeitar o contrato "usuario.schema.json"
    E a resposta identifica meu usuário

  @negativo
  Cenário: Buscar usuário inexistente
    Dado que estou autenticado na API ESG
    Quando busco um usuário inexistente
    Então a resposta deve ter status 404
    E o corpo da resposta HTTP deve estar vazio

  @positivo
  Cenário: Excluir usuário existente
    Dado que estou autenticado na API ESG
    Quando excluo meu usuário pelo ID
    Então a resposta deve ter status 204
    E o corpo da resposta HTTP deve estar vazio
    E o usuário excluído não deve mais ser encontrado

  @negativo
  Cenário: Excluir usuário inexistente
    Dado que estou autenticado na API ESG
    Quando tento excluir um usuário inexistente
    Então a resposta deve ter status 404
    E o corpo da resposta HTTP deve estar vazio
