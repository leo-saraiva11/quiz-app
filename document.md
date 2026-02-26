# Relatório Técnico: Quiz App Colaborativo com Firebase

**Data:** 25 de Fevereiro de 2026
**Disciplina:** Programação para Dispositivos Móveis
**Universidade:** Universidade Federal de Uberlândia (UFU)
**Grupo:** Leonardo, André, Gustavo e Marcelo

## 1. Introdução
Este documento detalha o desenvolvimento do aplicativo Quiz App, cujo objetivo é fornecer uma plataforma de quizzes interativa e offline-first para dispositivos Android, utilizando o ecossistema Firebase para gerenciamento de dados e autenticação.

## 2. Decisões de Arquitetura e Tecnologia
- **Jetpack Compose:** Optamos pelo Compose por ser a ferramenta moderna do Google para builds de UI nativas, permitindo uma interface reativa e seguindo os padrões do Material Design 3.
- **Firebase Authentication:** Utilizado para garantir que cada usuário tenha seu próprio perfil e histórico salvo com segurança.
- **Room Database (Local):** Implementamos o Room para servir como fonte de verdade quando o dispositivo está offline. O aplicativo baixa as questões do Firebase e as persiste localmente.
- **Cloud Firestore:** Escolhido pela facilidade de sincronização em tempo real e estrutura flexível de documentos para armazenar questões e resultados.

## 3. Papéis dos Membros
- **Leonardo:** Responsável pela arquitetura da UI e implementação dos componentes visuais com Jetpack Compose.
- **André:** Focou na integração com o Firebase, configurando autenticação e persistência remota.
- **Gustavo:** Atuou na lógica de negócios, integração de dados entre Room e Firestore, e na resolução de bugs críticos.
- **Marcelo:** Liderou a garantia de qualidade (QA), documentação técnica, verificação de requisitos e refinamento final.

## 4. Dificuldades Enfrentadas
1. **Sincronização de Dados:** Garantir que o banco de dados Room fosse atualizado corretamente apenas quando houvesse mudanças no Firestore, sem consumir dados desnecessários.
2. **Gerenciamento de Estado:** Lidar com o estado reativo do Compose durante a transição entre telas de quiz e resultados, especialmente em casos de reconexão de rede.
3. **Configuração de Gradientes:** Tivemos um problema inicial com o renderizador de gradientes da UI que causava crashes em algumas versões do Android, resolvido com a padronização das cores e vetores.

## 5. Uso de LLM (Inteligência Artificial)
Utilizamos assistentes de IA (como Gemini/ChatGPT) para:
- Geração de dados simulados (JSON) para as questões do quiz.
- Debug de erros de configuração no Gradle e dependências do Firebase.
- Refatoração de funções de mapeamento de dados entre entidades locais (Room) e remotas (Firestore).
- **Opinião do Grupo:** As LLMs foram fundamentais para agilizar o desenvolvimento da UI e para resolver problemas de configuração de ambiente, permitindo que focássemos mais na lógica de sincronização.

## 6. Conclusão
O projeto atingiu todos os requisitos propostos pelo professor, resultando em um aplicativo funcional, visualmente atraente e robusto na manipulação de dados em nuvem e locais.
