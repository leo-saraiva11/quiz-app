# Relatório Técnico — Quiz App Colaborativo com Firebase

**Disciplina:** Programação para Dispositivos Móveis  
**Professora/Professor:** Alexsandro Santos Soares  
**Universidade:** Universidade Federal de Uberlândia — FACOM  
**Data de Entrega:** 26 de Fevereiro de 2026  
**Grupo:** Leonardo, André, Gustavo e Marcelo  
**Repositório:** https://github.com/leo-saraiva11/quiz-app

---

## 1. Introdução

Este relatório descreve as decisões técnicas, os papéis dos membros e as dificuldades encontradas durante o desenvolvimento do **Quiz App Colaborativo com Firebase** — aplicativo Android desenvolvido como trabalho prático da disciplina de Programação para Dispositivos Móveis.

O aplicativo permite que usuários se autentiquem, respondam quizzes temáticos, acompanhem seu progresso histórico e compitam em um ranking global. As questões são hospedadas no Cloud Firestore e sincronizadas localmente via Room Database, garantindo funcionamento mesmo sem conexão com a internet.

---

## 2. Decisões de Arquitetura e Tecnologia

### 2.1 Padrão MVVM
Adotamos o padrão **Model-View-ViewModel (MVVM)** para organizar o projeto. Cada tela possui seu próprio `ViewModel` responsável por expor o estado da UI via `StateFlow`, enquanto os repositórios centralizam o acesso aos dados — seja do Room (local) ou do Firestore (remoto). Essa separação facilitou a divisão de tarefas entre os membros e tornou o código mais testável.

### 2.2 Jetpack Compose
Interface construída inteiramente com **Jetpack Compose**, seguindo as diretrizes do **Material Design 3**. Utilizamos `LazyColumn` para listas de quizzes e histórico, `AnimatedVisibility` para transições entre estados de carregamento, e gradientes como fundo das telas principais. O Compose eliminou a necessidade de XMLs de layout e acelerou a prototipação das telas.

### 2.3 Firebase Authentication
A autenticação foi implementada com **Firebase Authentication**, suportando login por **e-mail/senha** e **Google Sign-In**. Após o login, o UID do usuário é armazenado em `SharedPreferences` para persistência de sessão entre reinicializações do app.

### 2.4 Cloud Firestore
O **Cloud Firestore** armazena três coleções principais:
- `quizzes/` — metadados de cada quiz (título, categoria, número de questões)
- `questions/` — questões organizadas por `quizId`
- `results/` — resultados de cada sessão por usuário (`userId`, `quizId`, `score`, `timestamp`)

As regras de segurança do Firestore foram configuradas para que cada usuário só acesse seus próprios resultados, enquanto quizzes e questões são públicos para leitura.

### 2.5 Room Database (Estratégia Offline-First)
O **Room Database** serve como fonte de verdade primária. Na primeira execução, o app sincroniza todas as questões do Firestore para as tabelas locais. Nas execuções subsequentes, a sincronização é disparada manualmente pelo usuário via botão na tela inicial, ou automaticamente se for detectada uma diferença no campo `lastUpdated` do documento de configuração no Firestore.

---

## 3. Papéis dos Membros

| Membro | Papel Principal | Contribuições |
|---|---|---|
| **Leonardo** | UI / UX & Jetpack Compose | Implementou as telas de Login, Home, Quiz e Resultado. Criou o sistema de navegação com `NavController` e os componentes visuais reutilizáveis (cards, botões, gradientes). |
| **André** | Firebase & Autenticação | Configurou o projeto Firebase, implementou o `AuthRepository`, o Google Sign-In e a persistência de perfil via Firestore. Gerenciou as regras de segurança do banco. |
| **Gustavo** | Dados Locais & Sincronização | Projetou o esquema Room (`QuizEntity`, `QuestionEntity`, `ResultEntity`), implementou os DAOs e a lógica de sincronização com o Firestore. Resolveu os principais bugs de estado. |
| **Marcelo** | QA, Integração & Documentação | Responsável por testes manuais e robustez geral do app. Apoiou os colegas na integração entre módulos, importou as questões iniciais para o Firebase e elaborou este relatório e os slides. |

---

## 4. Dificuldades Enfrentadas

### 4.1 Sincronização Seletiva entre Firestore e Room
A maior dificuldade técnica foi garantir que o banco local fosse atualizado **somente quando houvesse mudanças reais** no Firestore. A solução adotada foi manter um documento de configuração (`config/meta`) no Firestore contendo um campo `questionsVersion` (inteiro). O app compara esse valor com o salvo localmente e só realiza a sincronização completa se houver divergência, evitando downloads desnecessários em cada abertura do app.

### 4.2 Questões Não Exibidas em Determinados Quizzes
Durante os testes, descobrimos que vários quizzes apareciam vazios no app, mesmo com questões cadastradas no Firestore. O problema estava no mapeamento: o campo `quizId` nas questões do Firestore usava o nome completo do quiz (ex: `"Segurança da Informação"`), enquanto o app consultava pelo ID do documento (ex: `"seguranca"`). Após padronizar os IDs, todos os quizzes passaram a exibir suas questões corretamente.

### 4.3 Crash de Gradientes no Android 10 e Anteriores
A tela inicial utilizava um gradiente diagonal (`Brush.linearGradient`) com uma lista de cores e ângulo personalizado. Em dispositivos com Android 10 (API 29) ou inferior, isso gerava um `IllegalArgumentException` no `SkiaShader`, crashando o app na inicialização. A solução foi substituir os gradientes angulares por gradientes verticais simples (`Brush.verticalGradient`), compatíveis com todas as versões suportadas.

### 4.4 Gerenciamento de Estado em Navegação com Compose
Ao navegar da tela de resultado de volta para a tela inicial, o estado do quiz anterior ficava retido no `ViewModel`, causando exibição de dados incorretos ao iniciar um novo quiz. O problema foi resolvido chamando explicitamente uma função `resetState()` no `ViewModel` ao entrar na tela de quiz, garantindo que o estado fosse sempre limpo para cada nova sessão.

---

## 5. Uso de Inteligência Artificial (LLMs)

### 5.1 LLMs Utilizadas
- **Gemini 2.5 Pro** (principal ferramenta ao longo do desenvolvimento)

### 5.2 Principais Usos e Prompts

| Uso | Exemplo de Prompt |
|---|---|
| Geração de questões para o Firebase | *"Gere um JSON com 10 questões de múltipla escolha sobre Kotlin, com 4 alternativas e indicação da resposta correta, no formato usado pelo Firestore"* |
| Resolução de crash de gradiente | *"Estou tendo IllegalArgumentException ao usar Brush.linearGradient com angle no Jetpack Compose no Android 10, como resolver?"* |
| Sincronização Room + Firestore | *"Como implementar uma estratégia offline-first no Android usando Room e Firestore, sincronizando apenas quando há mudanças remotas?"* |
| Configuração do Gradle | *"Configure o build.gradle do módulo app para incluir Firebase BOM, Room, Coroutines e Navigation Compose sem conflitos de versão"* |
| Geração de boilerplate de DAO | *"Gere um DAO Room para a entidade QuestionEntity com funções de inserção em lote, busca por quizId e deleção total"* |

### 5.3 Opinião do Grupo
As LLMs foram muito úteis para **tarefas repetitivas e bem definidas**, como geração de dados JSON, configuração de dependências e criação de estruturas de código padrão. Estimamos que reduziram em aproximadamente **40% o tempo** gasto nessas etapas específicas.

Por outro lado, para problemas que envolvem **lógica de negócios específica do projeto** — como a sincronização seletiva entre Firestore e Room — as sugestões da IA foram pontos de partida, mas exigiram adaptação e revisão manual cuidadosa. Código gerado por LLM foi sempre revisado antes de ser integrado ao repositório.

---

## 6. Conclusão

O projeto atingiu todos os requisitos estabelecidos: autenticação individual com Firebase, download e armazenamento local das questões, execução do quiz com histórico de desempenho, ranking global e interface moderna com Jetpack Compose. A divisão de papéis entre os membros funcionou bem, com comunicação via WhatsApp e controle de versão via GitHub com branches por funcionalidade. As principais lições aprendidas envolvem a importância de padronizar identificadores de dados desde o início e de testar em versões mais antigas do Android.
