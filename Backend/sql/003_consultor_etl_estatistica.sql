-- 003 — Prepara o banco para o ETL em Python (Aula 14).
--
-- Duas coisas:
--   A) Afrouxa `consultor` para que o Python possa criar consultor pelo nome.
--   B) Cria `estatistica_lote`, onde o Python grava o JSON que o dashboard lê.
--
-- NO DBEAVER: selecione o arquivo inteiro e use "Execute SQL Script" (Alt+X),
-- NÃO "Execute SQL Statement" (Ctrl+Enter) — a ordem entre os comandos importa.
-- O script é idempotente: rodar de novo depois de uma falha parcial é seguro.
--
-- ATENÇÃO: a parte (A) precisa ser aplicada JUNTO com a mudança em
-- Consultor.java (email/senha nullable, senha sem unique). Com
-- ddl-auto=validate, afrouxar só um dos lados impede o Spring Boot de subir.


-- =========================================================================
-- A) consultor: de entidade com credencial para tabela de apoio de nomes
-- =========================================================================
-- Contexto: `Consultor` não é entidade de autenticação — o login roda em
-- `Usuario`/`senhaHash`. `email` e `senha` aqui são resquício das aulas 08/09.
-- O ETL precisa criar consultor só com o nome que vem na planilha.

-- A1. Antes de tudo: nomes duplicados impedem o índice único do passo A4.
--     Se esta consulta devolver alguma linha, resolva os duplicados antes de
--     seguir (o script vai falhar em A4 de qualquer forma, sem corromper nada).
--     SELECT nome, count(*) FROM consultor GROUP BY nome HAVING count(*) > 1;

-- A2. Remove o UNIQUE de `senha`. Isto é bug puro, independente do ETL: dois
--     consultores não podem ter o mesmo hash de senha. O nome da constraint
--     varia conforme quem criou a tabela, então descobrimos em tempo de execução.
DO $$
DECLARE r record;
BEGIN
    FOR r IN
        SELECT i.relname AS idxname, con.conname AS conname
          FROM pg_index x
          JOIN pg_class i ON i.oid = x.indexrelid
          JOIN pg_class t ON t.oid = x.indrelid
          LEFT JOIN pg_constraint con ON con.conindid = x.indexrelid
         WHERE t.relname = 'consultor'
           AND x.indisunique
           AND x.indnatts = 1
           AND (SELECT attname
                  FROM pg_attribute
                 WHERE attrelid = t.oid AND attnum = x.indkey[0]) = 'senha'
    LOOP
        IF r.conname IS NOT NULL THEN
            EXECUTE format('ALTER TABLE consultor DROP CONSTRAINT %I', r.conname);
            RAISE NOTICE 'constraint unique de senha removida: %', r.conname;
        ELSE
            EXECUTE format('DROP INDEX %I', r.idxname);
            RAISE NOTICE 'indice unique de senha removido: %', r.idxname;
        END IF;
    END LOOP;
END $$;

-- A3. email e senha passam a aceitar NULL (consultor criado pelo ETL não tem
--     nenhum dos dois). O UNIQUE de `email` fica: no Postgres, vários NULL
--     convivem com um índice único sem conflito.
--     `DROP NOT NULL` não dá erro se a coluna já for nullable.
ALTER TABLE consultor ALTER COLUMN email DROP NOT NULL;
ALTER TABLE consultor ALTER COLUMN senha DROP NOT NULL;

-- A4. Índice único em `nome` — é o que faz o `ON CONFLICT (nome)` do banco.py
--     funcionar. Semanticamente correto: dois consultores homônimos seriam
--     indistinguíveis para o ETL de qualquer forma.
CREATE UNIQUE INDEX IF NOT EXISTS ux_consultor_nome ON consultor (nome);


-- =========================================================================
-- B) estatistica_lote: onde o Python grava o resultado das contas
-- =========================================================================
-- Sem entidade JPA do lado Java de propósito: o dashboard lê esta tabela com
-- JdbcTemplate, e `ddl-auto=validate` só valida o que está mapeado.
--
-- Nome da coluna: `id_lote`, seguindo a convenção do Norte Data (a mesma de
-- cliente.id_lote). O banco.py e o DashboardService do professor usam
-- `lote_id` — como o banco.py vai ser adaptado de qualquer jeito, vale manter
-- a convenção daqui em vez de importar a de lá.
CREATE TABLE IF NOT EXISTS estatistica_lote (
    id_estatistica BIGSERIAL PRIMARY KEY,
    id_lote        BIGINT      NOT NULL UNIQUE
                   REFERENCES upload_lote (id_lote) ON DELETE CASCADE,
    gerado_em      TIMESTAMP   NOT NULL DEFAULT now(),
    -- TEXT, não jsonb: o Java lê com rs.getString() e desserializa com Jackson,
    -- e o Python insere uma string pronta — jsonb exigiria cast dos dois lados
    -- sem ganho nenhum aqui.
    resultado      TEXT        NOT NULL
);

-- UNIQUE em id_lote: um resultado por lote. Reprocessar o mesmo lote é
-- UPDATE/DELETE+INSERT, nunca duas linhas concorrendo.
COMMENT ON TABLE estatistica_lote IS
    'JSON de estatísticas gravado pelo serviço Python (analytics-python); lido pelo DashboardService.';


-- =========================================================================
-- Conferência (rode depois, com Ctrl+Enter em cada uma)
-- =========================================================================
-- SELECT column_name, is_nullable FROM information_schema.columns
--  WHERE table_name = 'consultor' AND column_name IN ('email','senha','nome');
-- SELECT indexname FROM pg_indexes WHERE tablename = 'consultor';
-- SELECT column_name, data_type, is_nullable FROM information_schema.columns
--  WHERE table_name = 'estatistica_lote' ORDER BY ordinal_position;
