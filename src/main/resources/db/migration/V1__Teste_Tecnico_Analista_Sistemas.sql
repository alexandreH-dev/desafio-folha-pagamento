-- Usuários e Perfis
CREATE TABLE roles (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE usuarios (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuarios_roles (
  usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, role_id)
);

-- Funcionários
CREATE TABLE funcionarios (
  id BIGSERIAL PRIMARY KEY,
  matricula VARCHAR(30) UNIQUE NOT NULL,
  nome VARCHAR(150) NOT NULL,
  cpf VARCHAR(14) UNIQUE NOT NULL,
  orgao VARCHAR(60),
  lotacao VARCHAR(120),
  dt_admissao DATE
);

-- Rubricas (básicas)
CREATE TABLE rubricas (
  id BIGSERIAL PRIMARY KEY,
  codigo VARCHAR(20) UNIQUE NOT NULL,
  descricao VARCHAR(120) NOT NULL,
  tipo CHAR(1) NOT NULL CHECK (tipo IN ('V','D')) -- V=Vantagem, D=Desconto
);

-- Relação funcionário ↔ rubricas aplicadas por competência
CREATE TABLE funcionario_rubrica (
  id BIGSERIAL PRIMARY KEY,
  funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
  rubrica_id BIGINT NOT NULL REFERENCES rubricas(id),
  competencia_ano INT NOT NULL,
  competencia_mes INT NOT NULL CHECK (competencia_mes BETWEEN 1 AND 12),
  valor NUMERIC(14,2) NOT NULL,
  status VARCHAR(20) DEFAULT 'ATIVO',
  UNIQUE (funcionario_id, rubrica_id, competencia_ano, competencia_mes)
);

-- Saída do Batch (persistência do relatório por execução)
CREATE TABLE relatorio_financeiro_exec (
  execution_id BIGINT NOT NULL,
  funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id),
  competencia_ano INT NOT NULL,
  competencia_mes INT NOT NULL,
  total_vantagens NUMERIC(14,2) NOT NULL,
  total_descontos NUMERIC(14,2) NOT NULL,
  liquido NUMERIC(14,2) NOT NULL,
  detalhe JSONB NOT NULL, -- lista de rubricas agregadas (codigo, descricao, valor, tipo)
  PRIMARY KEY (execution_id, funcionario_id, competencia_ano, competencia_mes)
);



BEGIN;

-- =========================================================
-- 1) RUBRICAS (SALARIO, INSS, IRRF)
-- =========================================================
INSERT INTO rubricas (codigo, descricao, tipo)
VALUES
  ('SALARIO', 'Salário Base', 'V'),
  ('INSS',    'Contribuição Previdenciária', 'D'),
  ('IRRF',    'Imposto de Renda Retido na Fonte', 'D')
ON CONFLICT (codigo) DO NOTHING;

-- Guardar IDs das rubricas
WITH r AS (
  SELECT codigo, id FROM rubricas WHERE codigo IN ('SALARIO','INSS','IRRF')
)
SELECT * FROM r;

-- =========================================================
-- 2) FUNCIONÁRIOS (100 registros)
--    - cpf sintético (11 dígitos), sem pontuação
--    - lotação/órgão fictícios
--    - data de admissão aleatória entre 2010 e 2024
-- =========================================================
WITH seq AS (
  SELECT gs AS i
  FROM generate_series(1, 100) AS gs
),
data_adm AS (
  SELECT i,
         -- datas entre 2010-01-01 e 2024-12-31
         (DATE '2010-01-01' + ((RANDOM() * (DATE '2024-12-31' - DATE '2010-01-01'))::int))::date AS dt_adm
  FROM seq
)
INSERT INTO funcionarios (matricula, nome, cpf, orgao, lotacao, dt_admissao)
SELECT
  'M' || lpad(i::text, 6, '0')                                  AS matricula,
  'Funcionario ' || lpad(i::text, 3, '0')                        AS nome,
  -- CPF 11 dígitos sintético e único (ex.: 90000000001, 90000000002, ...)
  (90000000000 + i)::text                                        AS cpf,
  'SEPLAG'                                                       AS orgao,
  'Departamento ' || ((i % 10) + 1)                              AS lotacao,
  dt_adm                                                         AS dt_admissao
FROM data_adm
ON CONFLICT (matricula) DO NOTHING;

-- =========================================================
-- 3) Lançamentos por competência (JUN/2025, JUL/2025, AGO/2025)
--    Para cada funcionário, 3 rubricas por mês (SALARIO, INSS, IRRF)
--    Total esperado: 100 * 3 meses * 3 rubricas = 900 linhas
-- =========================================================

-- Buscar ids das rubricas
WITH rubs AS (
  SELECT codigo, id FROM rubricas WHERE codigo IN ('SALARIO','INSS','IRRF')
),
funcs AS (
  SELECT f.id AS funcionario_id,
         f.matricula,
         -- base salarial por funcionário: 3000 a ~6750 + ruído
         (3000
           + ((ROW_NUMBER() OVER (ORDER BY f.id) % 15) * 250)
           + ROUND((RANDOM() * 200)::numeric, 2)
         )::numeric(14,2) AS base_salarial
  FROM funcionarios f
),
comp AS (
  SELECT 2025 AS ano, x.mes
  FROM (values (1),(2),(3),(4),(5),(6),(7),(8)) AS x(mes)   
),
src AS (
  -- Combinar funcionário x competência e calcular valores
  SELECT
    funcs.funcionario_id,
    comp.ano,
    comp.mes,
    -- variação mensal leve no salário para não ficar estático (+0%, +1%, +2%)
    ROUND(funcs.base_salarial * (1 + (comp.mes - 1) * 0.01), 2) AS salario_mes,
    funcs.base_salarial
  FROM funcs
  CROSS JOIN comp
)
-- SALARIO
INSERT INTO funcionario_rubrica (funcionario_id, rubrica_id, competencia_ano, competencia_mes, valor, status)
SELECT
  s.funcionario_id,
  r_sal.id AS rubrica_id,
  s.ano, s.mes,
  s.salario_mes AS valor,
  'ATIVO' AS status
FROM src s
JOIN rubs r_sal ON r_sal.codigo = 'SALARIO'
ON CONFLICT (funcionario_id, rubrica_id, competencia_ano, competencia_mes) DO NOTHING;

-- INSS (11% do SALÁRIO já inserido, com leve ruído)
WITH rubs AS (
  SELECT codigo, id FROM rubricas WHERE codigo IN ('SALARIO','INSS')
),
sal AS (
  SELECT fr.funcionario_id, fr.competencia_ano, fr.competencia_mes, fr.valor AS salario_mes
  FROM funcionario_rubrica fr
  JOIN rubricas r ON r.id = fr.rubrica_id
  WHERE r.codigo = 'SALARIO'
    AND fr.competencia_ano = 2025
    AND fr.competencia_mes IN (1,2,3,4,5,6,7,8)
)
INSERT INTO funcionario_rubrica (funcionario_id, rubrica_id, competencia_ano, competencia_mes, valor, status)
SELECT
  s.funcionario_id,
  (SELECT id FROM rubs WHERE codigo = 'INSS') AS rubrica_id,
  s.competencia_ano, s.competencia_mes,
  ROUND(
  s.salario_mes * (0.11::numeric + ((RANDOM() - 0.5) * 0.01)::numeric),
  2
) AS valor,
  'ATIVO'
FROM sal s
ON CONFLICT (funcionario_id, rubrica_id, competencia_ano, competencia_mes) DO NOTHING;


-- IRRF (12% a 18% do SALÁRIO já inserido, faixa por funcionário)
WITH rubs AS (
  SELECT codigo, id FROM rubricas WHERE codigo IN ('SALARIO','IRRF')
),
sal AS (
  SELECT fr.funcionario_id, fr.competencia_ano, fr.competencia_mes, fr.valor AS salario_mes
  FROM funcionario_rubrica fr
  JOIN rubricas r ON r.id = fr.rubrica_id
  WHERE r.codigo = 'SALARIO'
    AND fr.competencia_ano = 2025
    AND fr.competencia_mes IN (1,2,3,4,5,6,7,8)
)
INSERT INTO funcionario_rubrica (funcionario_id, rubrica_id, competencia_ano, competencia_mes, valor, status)
SELECT
  s.funcionario_id,
  (SELECT id FROM rubs WHERE codigo = 'IRRF') AS rubrica_id,
  s.competencia_ano, s.competencia_mes,
  ROUND(s.salario_mes * (0.12 + ((s.funcionario_id % 7) * 0.01)), 2) AS valor,
  'ATIVO'
FROM sal s
ON CONFLICT (funcionario_id, rubrica_id, competencia_ano, competencia_mes) DO NOTHING;


COMMIT;

-- =========================================================
-- 4) Verificações rápidas
-- =========================================================
-- Quantidade de funcionários
 SELECT COUNT(*) FROM funcionarios;

-- Quantidade de lançamentos por rubrica
 SELECT r.codigo, COUNT(*) FROM funcionario_rubrica fr
 JOIN rubricas r ON r.id = fr.rubrica_id
 GROUP BY r.codigo ORDER BY r.codigo;

-- Total esperado: 900
 SELECT COUNT(*) FROM funcionario_rubrica;

-- Amostra de 5 registros
 SELECT f.matricula, r.codigo, fr.competencia_mes, fr.competencia_ano, fr.valor
 FROM funcionario_rubrica fr
 JOIN funcionarios f ON f.id = fr.funcionario_id
 JOIN rubricas r ON r.id = fr.rubrica_id
 ORDER BY f.id, fr.competencia_ano, fr.competencia_mes, r.codigo
 LIMIT 5;





