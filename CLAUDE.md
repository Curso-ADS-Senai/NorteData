# Norte Data

Projeto integrador (2° semestre). Professora: Tatiana Aparecida de Almeida.

O nome do sistema é **Norte Data**. "CTI Insights" era só o nome de exemplo usado no PDF de referência do projeto — não usar em nenhuma tela, texto ou commit.

## Problema

A empresa cliente gerencia sua base de clientes em planilhas Excel, preenchidas manualmente. Isso gera erros de digitação e inconsistências (ex: "IND." vs "Industria") e impede análises automáticas ou cruzamento de dados. Hoje é impossível responder rápido a perguntas como "qual segmento traz mais retorno para a empresa?".

## Solução

Sistema web Norte Data: upload automático de planilhas, tratamento e padronização dos dados, dashboards e gráficos interativos, insights estratégicos (ex: evolução de contratações, base de clientes por segmento/faturamento/status).

### Regra da planilha

- Estrutura de colunas é fixa (template definido, ex: ID, Empresa, Segmento, Faturamento, Nível, Status).
- Leitura por **nome da coluna no cabeçalho**, não por posição — tolera reordenação.
- O sistema padroniza os *valores* (ex: unifica "IND.", "Industria" → "Indústria"), não descobre a estrutura sozinho.
- Se faltar coluna obrigatória ou o arquivo não bater com o template, mostrar erro na tela de upload.

## Stack

- Front-end: Vue.js (Vite) + Vue Router.
- Estilização: Tailwind CSS. Classes utilitárias no `class`, sem CSS customizado. Não criar arquivos `.css` novos nem `<style>` a menos que estritamente necessário.
- Ordem das classes Tailwind: layout (`flex`, `grid`) → tamanho/espaço (`w-`, `p-`, `m-`, `gap-`) → cores (`bg-`, `text-`) → bordas (`rounded`, `border-`) → efeitos (`hover:`, `transition`).
- HTML semântico: usar `header`, `nav`, `main`, `section`, `footer`, `table`, `form`, `ul`/`li` em vez de `<div>` genérica. `<div>` só quando não há tag semântica equivalente.
- Gráficos: Chart.js.
- Back-end: Java (regras de negócio, autenticação).
- Banco de dados: PostgreSQL (tabelas principais: Clientes, Usuários; consultas com filtros e agrupamentos).

## Estrutura do front-end

- `src/App.vue` — estrutura principal da página.
- `src/main.js` — liga o Vue ao HTML.
- `src/components/` — componentes reutilizáveis.

## Páginas do sistema

Público:
1. Apresentação (landing page)
2. Login
3. Cadastro / Recuperar senha

Logado:
4. Upload de planilha
5. Dashboard — Visão Geral (KPIs: total de clientes, faturamento médio, serviços ativos; gráficos de distribuição por segmento e por nível A/B/C)
6. Dashboard — Evolução (gráfico de novos clientes por mês, filtro por ano)
7. Relatórios (tabela de clientes com busca)
8. Detalhe do Cliente
9. Gerenciar Usuários (admin)
10. Perfil / Configurações
11. Histórico de Uploads

Genéricas:
12. Erro 404
13. Erro de upload (planilha fora do template)

## Status atual

- `LandingPage.vue` 80% pronta.
- Demais telas ainda não iniciadas.

## Regras do projeto

- Evitar ao máximo CSS próprio — usar classes utilitárias do Tailwind.
- Respostas e commits diretos e simples, sem enrolação.
- Manter consistência visual e de nomenclatura entre todas as telas.