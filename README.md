# PluginExample

Projeto consumidor completo para estudar e iniciar novos plugins baseados no PluginBase `0.0.1-beta5`.

## Requisitos

- Java 25;
- Maven 3.9.6 ou superior;
- Paper API `26.2.build.65-beta`;
- PluginBase `0.0.1` instalado no servidor e no repositório Maven local.

## Compilação

Na pasta do PluginBase, publique a dependência local:

```text
mvn clean install
```

Depois, nesta pasta:

```text
mvn clean package
```

O arquivo será criado em `target/pluginexample-0.0.1.jar`.

No servidor, instale os dois arquivos na pasta `plugins`:

```text
PluginBase-0.0.1-beta5.jar
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
