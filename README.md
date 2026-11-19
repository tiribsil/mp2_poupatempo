# Sistema de Gerenciamento de Filas - Poupatempo

Miniprojeto Web MVC desenvolvido para a disciplina de Programação Web (UFSCar).
Implementa controle de emissão de senhas, filas de espera diferenciadas (normal e prioritária), chamada por guichê com regras de alternância (2:1), ciclo de vida dos atendimentos e painel público em tempo real.

## Tecnologias Utilizadas

- **Back-end:** Java 21, Jakarta Servlet 6.0, Gson 2.11.0, Apache Tomcat 10.1
- **Padrão de Arquitetura:** MVC (Model-View-Controller) com padrão DAO e Factory Method
- **Banco de Dados:** SQLite (via `sqlite-jdbc`), com inicialização automática e script `banco.sql` na raiz
- **Front-end:** HTML5, CSS3 estilizado e Vue.js 3
- **Comunicação Assíncrona:** AJAX (Fetch API) com respostas em JSON

## Estrutura do Projeto

```
├── banco.sql                                 # Script de criação e dados pré-populados
├── pom.xml                                   # Configuração Maven com dependências
├── run.sh                                    # Script de compilação e inicialização no Codespaces
├── specs.txt                                 # Especificação original do trabalho
└── src/main/
    ├── java/
    │   ├── controller/
    │   │   ├── AtendenteServlet.java         # API /api/atendente
    │   │   ├── EmissaoServlet.java           # API /api/emissao
    │   │   └── PainelServlet.java            # API /api/painel
    │   ├── dao/
    │   │   ├── GuicheDAO.java                # Acesso aos guichês e serviços vinculados
    │   │   ├── SenhaDAO.java                 # Persistência e consultas de senhas/filas
    │   │   └── TipoAtendimentoDAO.java       # Consulta de tipos de atendimento
    │   ├── database/
    │   │   └── ConnectionFactory.java        # Conexão e inicialização do SQLite
    │   ├── dto/
    │   │   ├── FilaGuicheDTO.java            # Dados de fila para o atendente
    │   │   ├── PainelDTO.java                # Dados para o painel público
    │   │   └── SenhaEmitidaDTO.java          # Dados de confirmação de senha emitida
    │   ├── model/
    │   │   ├── Guiche.java                   # Entidade Guichê
    │   │   ├── Senha.java                    # Entidade Senha
    │   │   ├── StatusSenha.java              # Enum de estados da senha
    │   │   └── TipoAtendimento.java          # Entidade Tipo de Atendimento
    │   └── service/
    │       └── AtendimentoService.java       # Regras de negócio, regra 2:1 e concorrência
    └── webapp/
        ├── css/
        │   └── style.css                     # Estilização visual inspirada no Poupatempo
        ├── js/
        │   ├── atendenteApp.js               # Aplicação Vue.js do atendente
        │   ├── emissaoApp.js                 # Aplicação Vue.js do totem de emissão
        │   └── painelApp.js                  # Aplicação Vue.js do painel público
        ├── index.html                        # Portal de navegação com links para as 3 interfaces
        ├── emissao.html                      # Interface 1: Totem do cidadão
        ├── atendente.html                    # Interface 2: Painel do atendente
        └── painel.html                       # Interface 3: Painel público de chamadas
```

## Como Executar no GitHub Codespaces

1. Abra o repositório no **GitHub Codespaces**.
2. No terminal integrado do Codespaces, execute:
   ```bash
   bash run.sh
   ```
3. O script irá:
   - Compilar o projeto (`mvn clean package`);
   - Publicar o pacote WAR em `$HOME/tomcat/webapps/ROOT.war`;
   - Iniciar o servidor Apache Tomcat na porta `8080`.
4. Clique na notificação de encaminhamento de porta para abrir a aplicação no navegador (porta 8080).

## Interfaces Independentes

Acesse cada interface em uma aba distinta do navegador:

1. **Portal Inicial:** `http://localhost:8080/`
2. **Totem de Emissão (Cidadão):** `http://localhost:8080/emissao.html`
3. **Módulo do Atendente (Funcionário):** `http://localhost:8080/atendente.html`
4. **Painel Público (Monitor de Chamadas):** `http://localhost:8080/painel.html`

## Regras de Negócio Implementadas

1. **Tipos de Atendimento Disponíveis:**
   - `CIN`: Carteira de Identidade
   - `CNH`: Carteira Nacional de Habilitação
   - `VEI`: Veículos (Licenciamento e Registro)
2. **Configuração de Guichês no Banco de Dados:**
   - **Guichê 1:** Atende CIN e CNH.
   - **Guichê 2:** Atende Todos (CIN, CNH e Veículos).
   - **Guichê 3:** Atende apenas Veículos.
3. **Regra de Alternância de Prioridades (Requisito 7):**
   - Senhas prioritárias possuem preferência natural.
   - Após 2 chamadas prioritárias consecutivas, caso exista senha normal aguardando e compatível com o guichê, a próxima chamada é obrigatoriamente convencional.
   - Se não houver normal compatível, o fluxo segue atendendo as prioritárias sem bloqueio.
4. **Prevenção de Chamada Duplicada (Requisito 9):**
   - A seleção da próxima senha é feita exclusivamente no servidor com bloco `synchronized` e verificação atômica de status `AGUARDANDO` -> `CHAMADA`.
5. **Armazenamento Temporal do Ciclo de Vida (Requisito 10):**
   - São gravados individualmente: instante de emissão, instante da chamada, instante de início do atendimento e instante de finalização.
