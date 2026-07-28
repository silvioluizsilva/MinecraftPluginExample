# PluginExample

Projeto consumidor completo para estudar e iniciar novos plugins baseados no PluginBase `0.0.1`.

## Requisitos

- JDK 25 ou 26, mantendo compatibilidade de bytecode com Java 25;
- Maven 3.9.6 ou superior;
- Paper API `26.2.build.65-beta`;
- build conjunto pela raiz `Codigo-Fonte` ou PluginBase instalado localmente na mesma versão.

## Compilação

Na raiz `Codigo-Fonte`, execute:

```text
mvn clean verify
```

O arquivo será criado em `target/pluginexample-0.0.1.jar`.

No servidor, instale os dois arquivos na pasta `plugins`:

```text
pluginbase-0.0.1.jar
pluginexample-0.0.1.jar
```

## Demonstrações incluídas

- obtenção da API por `PluginBaseProvider`;
- comando Brigadier `/pluginexample hello`;
- listener de entrada de jogador;
- regra de negócio separada em `GreetingService`;
- tarefa JDBC assíncrona vinculada ao PluginExample;
- repositório com `PreparedStatement`;
- migração SQL idempotente com histórico central isolado por namespace;
- mensagens `pt_BR` e `en_US` com placeholders seguros;
- configuração tipada.

## Como transformar em outro plugin

1. Renomeie `PluginExample` e o `artifactId`.
2. Troque o pacote `br.net.silvioluizsilva.pluginexample`.
3. Atualize `plugin.yml`, permissões e mensagens.
4. Substitua `GreetingService`, o listener e o repositório pelas regras do novo plugin.
5. Crie migrações numeradas próprias, sem alterar tabelas internas do PluginBase.

O usuário MySQL continua sendo configurado exclusivamente no PluginBase. O consumidor recebe somente operações transacionais pela API pública.
