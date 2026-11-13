-- 005 — Índice único em servico(descricao), para o ETL em Python.
--
-- O banco.py cria o serviço pelo nome que vem na coluna `servicos_contratados` da planilha,
-- com "INSERT ... ON CONFLICT (descricao) DO NOTHING". No Postgres o ON CONFLICT exige um
-- índice único na coluna — sem isto o ETL falha com
--   "there is no unique or exclusion constraint matching the ON CONFLICT specification".
--
-- É também o comportamento correto: dois serviços com a mesma descrição seriam
-- indistinguíveis para o ETL, que só tem o texto da planilha para casar.
--
-- NO DBEAVER: arquivo inteiro com "Execute SQL Script" (Alt+X). Idempotente.

-- Confira duplicados ANTES — se esta consulta devolver linhas, o CREATE abaixo falha
-- (sem corromper nada) e os duplicados precisam ser resolvidos primeiro:
--   SELECT descricao, count(*) FROM servico GROUP BY descricao HAVING count(*) > 1;

CREATE UNIQUE INDEX IF NOT EXISTS ux_servico_descricao ON servico (descricao);

-- Conferência:
-- SELECT indexname FROM pg_indexes WHERE tablename = 'servico';
