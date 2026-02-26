# Prompt para Geração de Slides (Gamma AI)

Copie e cole este prompt na Gamma AI (gamma.app) para gerar os slides da apresentação do projeto.

---

**Prompt:**

Crie uma apresentação de 7 slides para um trabalho acadêmico de um "Quiz App" Android desenvolvido com Firebase. O tom deve ser profissional e técnico, mas visualmente moderno. Siga exatamente esta estrutura de 7 slides:

1. **Título:** Quiz App Colaborativo: Integração Android & Firebase. Inclua os nomes dos desenvolvedores: Leonardo, André, Gustavo e Marcelo.
2. **O Projeto:** Explicação do objetivo do app (quiz moderno, funcional, com autenticação e uso offline).
3. **Funcionalidades Principais:** Listar Autenticação Firebase, Sincronização de Dados (Firestore -> Room), Histórico de Desempenho e Ranking.
4. **Arquitetura Técnica:** Explicar o uso de Jetpack Compose para UI, Room para banco local e Firestore para nuvem. Mencionar o padrão MVVM.
5. **Dificuldades Encontradas:** (Slide Obrigatório 1) Detalhar desafios na sincronização offline-first e na correção de crashes de interface relacionados a gradientes.
6. **Uso de LLMs:** (Slide Obrigatório 2) Mencionar o uso de Gemini/ChatGPT para geração de JSON de dados, auxílio em configurações do Gradle e automação de boilerplates. Dar uma opinião positiva sobre a agilidade.
7. **Demonstração e Conclusão:** Screenshot do Dashboard/Ranking (placeholder) e agradecimentos.

---

**Informações Complementares para o Slide de LLM (Contexto):**
- **LLMs usadas:** Gemini 3.0.
- **Principais Prompts:** "Como sincronizar Firestore com Room no Android?", "Gere um JSON com 10 questões de Kotlin para Quiz", "Corrija erro de gradiente no Material Design 3 Jetpack Compose".
- **Opinião:** O grupo considera que as LLMs reduzem o tempo de tarefas repetitivas em 40%, mas exigem revisão constante do código gerado.
