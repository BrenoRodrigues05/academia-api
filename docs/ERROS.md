# ⚠️ Tratamento de Erros

A API possui um **Global Exception Handler** centralizado, responsável por capturar e padronizar todas as respostas de erro da aplicação.

## Exceptions Customizadas

* `ResourceNotFoundException`
* `BusinessException`
* `UnauthorizedException`

Essas exceptions são lançadas pelas camadas de serviço sempre que uma regra de negócio é violada ou um recurso não é encontrado, sendo capturadas pelo `GlobalExceptionHandler` e convertidas em respostas HTTP padronizadas.

## Tratamento de Validações

Erros de validação dos DTOs (`@Valid`, `@NotNull`, `@NotBlank`, `@Email`, etc.) são interceptados automaticamente e retornados em um formato padronizado, listando todos os campos inválidos e suas respectivas mensagens.

## Formato Padrão de Resposta de Erro

```json
{
  "timestamp": "2026-06-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação",
  "fields": {
    "email": "deve ser um e-mail válido",
    "nome": "não pode estar em branco"
  }
}
```