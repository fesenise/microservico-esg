# language: pt

Funcionalidade: Cadastro de usuários
  Como cidadão que deseja contribuir para a reciclagem
  Quero me cadastrar no microsserviço ESG
  Para registrar minhas atividades de descarte

  @usuarios @positivo
  Cenário: Cadastrar usuário com dados válidos
    Dado que o microsserviço ESG está disponível
    Quando cadastro um usuário com dados válidos
    Então a resposta deve ter status 201
    E o JSON deve conter os dados do usuário cadastrado
    E o JSON não deve expor a senha
    E o JSON deve respeitar o contrato "usuario.schema.json"

  @usuarios @negativo @validacao
  Esquema do Cenário: Rejeitar cadastro com dados inválidos
    Dado que o microsserviço ESG está disponível
    Quando tento cadastrar um usuário com o campo "<campo>" igual a "<valor>"
    Então a resposta deve ter status 400
    E o JSON deve respeitar o contrato "erro.schema.json"
    E a mensagem de erro deve mencionar "<campo>"

    Exemplos:
      | campo | valor          |
      | nome  |                |
      | email | email-invalido |
      | senha | 123            |

  @usuarios @negativo @duplicidade
  Cenário: Rejeitar cadastro com e-mail já existente
    Dado que já existe um usuário cadastrado
    Quando tento cadastrar outro usuário com o mesmo e-mail
    Então a resposta deve ter status 400
    E o JSON deve respeitar o contrato "erro.schema.json"
    E a mensagem de erro deve mencionar "Já existe"
