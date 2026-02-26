# Prompt para Geração de Slides (Gamma AI)

Copie e cole este prompt no Gamma AI (gamma.app) para gerar os slides da apresentação do projeto.

---

**Prompt:**

Crie uma apresentação profissional e visualmente moderna de **exatamente 7 slides** para a disciplina de "Programação para Dispositivos Móveis" da Universidade Federal de Uberlândia (UFU). O trabalho é um aplicativo Android de quiz colaborativo desenvolvido com Firebase. Use um design limpo com paleta de cores escura/roxa, ícones e destaques visuais. Siga estritamente esta estrutura slide por slide:

---

**Slide 1 — Título**
- Título principal: **Quiz App Colaborativo com Firebase**
- Subtítulo: Trabalho Prático — Programação para Dispositivos Móveis
- Integrantes: **Leonardo, André, Gustavo e Marcelo**
- Universidade Federal de Uberlândia — FACOM, 2026

---

**Slide 2 — Visão Geral do Projeto**
- Objetivo: desenvolver um app Android de quiz moderno, funcional e colaborativo
- Principais características:
  - Acesso individualizado por login (Firebase Authentication)
  - Questões hospedadas na nuvem (Cloud Firestore) e disponíveis offline (Room Database)
  - Histórico e estatísticas de desempenho por usuário
  - Dashboard com ranking entre usuários
- Tecnologias centrais: Kotlin, Jetpack Compose, Firebase, Room

---

**Slide 3 — Funcionalidades Implementadas**
Liste em formato de cards ou ícones as seguintes funcionalidades:
- 🔐 **Autenticação:** Login por e-mail/senha e Google via Firebase Authentication
- ☁️ **Download de Questões:** Sincronização automática do Firestore para o banco local (Room)
- 📴 **Modo Offline:** Questões disponíveis sem conexão à internet
- 🏆 **Ranking:** Classificação dos usuários por pontuação geral
- 📊 **Dashboard e Histórico:** Estatísticas por sessão (acertos, tempo, percentual)
- 🔄 **Sincronização Automática:** Atualização do banco local quando há mudanças no Firebase

---

**Slide 4 — Arquitetura Técnica**
Apresente um diagrama simples mostrando o fluxo: Usuário → Jetpack Compose UI → ViewModel (MVVM) → Room (Local) ↔ Firestore (Nuvem)

Descreva:
- **Padrão MVVM:** Separação clara entre UI, lógica de negócios e dados
- **Jetpack Compose:** Interface declarativa seguindo Material Design 3
- **Room + Firestore:** Estratégia offline-first — Room como fonte de verdade primária
- **Firebase Auth:** Controle de sessão e perfil persistido via SharedPreferences

---

**Slide 5 — Dificuldades Encontradas** *(Slide obrigatório 1)*
Apresente as dificuldades em formato de lista com destaque visual:

1. **Sincronização Offline-First:** Garantir que o Room fosse atualizado somente quando havia mudanças reais no Firestore, evitando consultas desnecessárias e duplicação de dados.
2. **Crash de Gradientes na UI:** Um erro de `IllegalArgumentException` no renderizador de gradientes causava crash em algumas versões do Android. Resolvido padronizando os vetores de cor no Material Design 3.
3. **Gerenciamento de Estado com Compose:** Controlar o estado reativo durante transições entre telas (quiz → resultado → histórico) sem inconsistências, especialmente ao reconectar a rede.
4. **Questões Não Exibidas:** Problema de sincronização fazia com que apenas alguns quizzes exibissem perguntas — resolvido revisando a lógica de mapeamento entre documentos do Firestore e entidades Room.

---

**Slide 6 — Uso de Inteligência Artificial (LLMs)** *(Slide obrigatório 2)*
Apresente em dois blocos:

**LLMs Utilizadas:** Gemini 2.5 Pro

**Exemplos de Prompts Utilizados:**
- *"Como sincronizar dados do Firestore com Room Database no Android usando corrotinas Kotlin?"*
- *"Gere um JSON com 10 questões de múltipla escolha sobre Kotlin para um quiz Android"*
- *"Corrija o erro IllegalArgumentException ao renderizar gradiente inclinado com Material Design 3 no Jetpack Compose"*
- *"Implemente a tela de histórico com LazyColumn exibindo pontuação e data de cada sessão"*

**Opinião do Grupo:**
As LLMs foram essenciais para acelerar tarefas repetitivas (geração de dados, boilerplate de código), mas exigiram revisão constante — especialmente para lógica de sincronização e bugs de estado. Estimamos uma redução de ~40% no tempo de desenvolvimento de partes específicas.

---

**Slide 7 — Demonstração e Conclusão**
- Screenshot ou mockup das telas: Login → Home (lista de quizzes) → Quiz em andamento → Resultado → Histórico → Ranking/Dashboard
- Frase de conclusão: "Todos os requisitos foram atendidos: autenticação, armazenamento offline, histórico pessoal, dashboard e ranking."
- Agradecimentos: "Obrigado! Disponíveis para demonstração ao vivo no emulador."

---

**Instruções de estilo para o Gamma AI:**
- Máximo de 7 slides — não adicione nem remova slides
- Fonte grande e legível (mínimo 18pt para corpo de texto)
- Não use capturas de tela para código — copie e cole os trechos dentro de blocos de código formatados
- Paleta sugerida: tons escuros (roxo/azul-marinho) com acentos em ciano ou laranja
- Ícones modernos para ilustrar cada funcionalidade
- Diagrama de arquitetura no Slide 4 deve ser gerado visualmente (não imagem borrada)
