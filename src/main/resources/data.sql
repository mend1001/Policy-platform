-- ─── 1. ROLES ────────────────────────────────────────────────────────────────
INSERT INTO roles (rol_id, rol_name, rol_description) VALUES
(RANDOM_UUID(), 'TOMADOR',      'Tomador de la póliza - arrendatario'),
(RANDOM_UUID(), 'BENEFICIARIO', 'Beneficiario de la póliza - arrendador'),
(RANDOM_UUID(), 'ASEGURADO',    'Persona asegurada bajo un riesgo'),
(RANDOM_UUID(), 'INMOBILIARIA', 'Empresa inmobiliaria - tomador colectivo');

-- ─── 2. USERS ────────────────────────────────────────────────────────────────
INSERT INTO users (usr_id, usr_name, usr_lastname, usr_email, usr_phone, usr_doc_type, usr_doc_number) VALUES
(RANDOM_UUID(), 'Juan',    'Pérez',    'juan.perez@email.com',    '3001111111', 'CC', '10000001'),
(RANDOM_UUID(), 'María',   'López',    'maria.lopez@email.com',   '3002222222', 'CC', '10000002'),
(RANDOM_UUID(), 'Carlos',  'Ruiz',     'carlos.ruiz@email.com',   '3003333333', 'CC', '10000003'),
(RANDOM_UUID(), 'Ana',     'Torres',   'ana.torres@email.com',    '3004444444', 'CC', '10000004'),
(RANDOM_UUID(), 'Pedro',   'Mora',     'pedro.mora@email.com',    '3005555555', 'CC', '10000005'),
(RANDOM_UUID(), 'Sofía',   'Díaz',     'sofia.diaz@email.com',    '3006666666', 'CC', '10000006'),
(RANDOM_UUID(), 'Luis',    'Vargas',   'luis.vargas@email.com',   '3007777777', 'CC', '10000007'),
(RANDOM_UUID(), 'Clara',   'Reyes',    'clara.reyes@email.com',   '3008888888', 'CC', '10000008'),
(RANDOM_UUID(), 'Roberto', 'Serna',    'roberto.serna@email.com', '3009999999', 'CC', '10000009'),
(RANDOM_UUID(), 'Sandra',  'Mejía',    'sandra.mejia@email.com',  '3010101010', 'CC', '10000010'),
(RANDOM_UUID(), 'David',   'Castro',   'david.castro@email.com',  '3011111111', 'CC', '10000011'),
(RANDOM_UUID(), 'Elena',   'Moreno',   'elena.moreno@email.com',  '3012121212', 'CC', '10000012'),
(RANDOM_UUID(), 'Felipe',  'Gómez',    'felipe.gomez@email.com',  '3013131313', 'CC', '10000013'),
(RANDOM_UUID(), 'Patricia','Herrera',  'patricia.herrera@email.com','3014141414','CC', '10000014'),
(RANDOM_UUID(), 'Andrés',  'Jiménez',  'andres.jimenez@email.com','3015151515', 'CC', '10000015');

-- ─── 3. USER_ROLES ───────────────────────────────────────────────────────────
INSERT INTO user_roles (usr_id, rol_id)
SELECT u.usr_id, r.rol_id FROM users u, roles r
WHERE u.usr_doc_number IN ('10000001','10000003','10000005','10000007')
  AND r.rol_name = 'TOMADOR';

INSERT INTO user_roles (usr_id, rol_id)
SELECT u.usr_id, r.rol_id FROM users u, roles r
WHERE u.usr_doc_number IN ('10000002','10000004','10000006','10000008')
  AND r.rol_name = 'BENEFICIARIO';

INSERT INTO user_roles (usr_id, rol_id)
SELECT u.usr_id, r.rol_id FROM users u, roles r
WHERE u.usr_doc_number IN ('10000009','10000010','10000011','10000012','10000013','10000014','10000015')
  AND r.rol_name = 'ASEGURADO';

-- ─── 4. POLICY_TYPES ─────────────────────────────────────────────────────────
INSERT INTO policy_types (pty_id, pty_name, pty_description) VALUES
(RANDOM_UUID(), 'INDIVIDUAL', 'Póliza para un único arrendatario'),
(RANDOM_UUID(), 'COLECTIVA',  'Póliza para inmobiliaria o copropiedad');

-- ─── 5. POLICY_STATES ────────────────────────────────────────────────────────
INSERT INTO policy_states (pst_id, pst_name, pst_description) VALUES
(RANDOM_UUID(), 'ACTIVA',    'Póliza actualmente vigente'),
(RANDOM_UUID(), 'CANCELADA', 'Póliza cancelada'),
(RANDOM_UUID(), 'RENOVADA',  'Póliza renovada por otro período');

-- ─── 6. RISK_STATES ──────────────────────────────────────────────────────────
INSERT INTO risk_states (rst_id, rst_name, rst_description) VALUES
(RANDOM_UUID(), 'ACTIVO',    'Riesgo actualmente vigente'),
(RANDOM_UUID(), 'CANCELADO', 'Riesgo cancelado');

-- ─── 7. POLICIES ─────────────────────────────────────────────────────────────
-- Póliza 1: INDIVIDUAL ACTIVA
INSERT INTO policies (pol_id, pty_id, pst_id, usr_id_holder, usr_id_beneficiary,
                      pol_canon, pol_premium, pol_months, pol_start_date, pol_end_date, pol_auto_renewal)
SELECT RANDOM_UUID(), pt.pty_id, ps.pst_id, h.usr_id, b.usr_id,
       1500000.00, 18000000.00, 12, '2024-01-01', '2025-01-01', FALSE
FROM policy_types pt, policy_states ps, users h, users b
WHERE pt.pty_name = 'INDIVIDUAL' AND ps.pst_name = 'ACTIVA'
  AND h.usr_doc_number = '10000001' AND b.usr_doc_number = '10000002';

-- Póliza 2: INDIVIDUAL RENOVADA
INSERT INTO policies (pol_id, pty_id, pst_id, usr_id_holder, usr_id_beneficiary,
                      pol_canon, pol_premium, pol_months, pol_start_date, pol_end_date, pol_auto_renewal)
SELECT RANDOM_UUID(), pt.pty_id, ps.pst_id, h.usr_id, b.usr_id,
       2000000.00, 24000000.00, 12, '2023-06-01', '2025-06-01', FALSE
FROM policy_types pt, policy_states ps, users h, users b
WHERE pt.pty_name = 'INDIVIDUAL' AND ps.pst_name = 'RENOVADA'
  AND h.usr_doc_number = '10000003' AND b.usr_doc_number = '10000004';

-- Póliza 3: COLECTIVA ACTIVA (con varios riesgos)
INSERT INTO policies (pol_id, pty_id, pst_id, usr_id_holder, usr_id_beneficiary,
                      pol_canon, pol_premium, pol_months, pol_start_date, pol_end_date, pol_auto_renewal)
SELECT RANDOM_UUID(), pt.pty_id, ps.pst_id, h.usr_id, b.usr_id,
       3500000.00, 84000000.00, 24, '2024-01-01', '2026-01-01', TRUE
FROM policy_types pt, policy_states ps, users h, users b
WHERE pt.pty_name = 'COLECTIVA' AND ps.pst_name = 'ACTIVA'
  AND h.usr_doc_number = '10000005' AND b.usr_doc_number = '10000006';

-- Póliza 4: COLECTIVA ACTIVA
INSERT INTO policies (pol_id, pty_id, pst_id, usr_id_holder, usr_id_beneficiary,
                      pol_canon, pol_premium, pol_months, pol_start_date, pol_end_date, pol_auto_renewal)
SELECT RANDOM_UUID(), pt.pty_id, ps.pst_id, h.usr_id, b.usr_id,
       4200000.00, 50400000.00, 12, '2024-07-01', '2025-07-01', FALSE
FROM policy_types pt, policy_states ps, users h, users b
WHERE pt.pty_name = 'COLECTIVA' AND ps.pst_name = 'ACTIVA'
  AND h.usr_doc_number = '10000007' AND b.usr_doc_number = '10000008';

-- ─── 8. RISKS ────────────────────────────────────────────────────────────────
-- Risk 1: póliza 1 (INDIVIDUAL) - asegurado user9
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Calle 72 # 10-34, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'ACTIVO' AND u.usr_doc_number = '10000009'
  AND h.usr_doc_number = '10000001' AND p.usr_id_holder = h.usr_id
LIMIT 1;

-- Risk 2: póliza 2 (INDIVIDUAL RENOVADA) - asegurado user10
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Carrera 15 # 93-45, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'ACTIVO' AND u.usr_doc_number = '10000010'
  AND h.usr_doc_number = '10000003' AND p.usr_id_holder = h.usr_id
LIMIT 1;

-- Risk 3: póliza 3 (COLECTIVA) - asegurado user11
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Avenida El Dorado # 68C-61, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'ACTIVO' AND u.usr_doc_number = '10000011'
  AND h.usr_doc_number = '10000005' AND p.usr_id_holder = h.usr_id
LIMIT 1;

-- Risk 4: póliza 3 (COLECTIVA) - asegurado user12
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Calle 100 # 9-67, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'ACTIVO' AND u.usr_doc_number = '10000012'
  AND h.usr_doc_number = '10000005' AND p.usr_id_holder = h.usr_id
LIMIT 1;

-- Risk 5: póliza 3 (COLECTIVA) - asegurado user13 - CANCELADO
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Carrera 7 # 32-21, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'CANCELADO' AND u.usr_doc_number = '10000013'
  AND h.usr_doc_number = '10000005' AND p.usr_id_holder = h.usr_id
LIMIT 1;

-- Risk 6: póliza 4 (COLECTIVA) - asegurado user14
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Calle 26 # 92-32, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'ACTIVO' AND u.usr_doc_number = '10000014'
  AND h.usr_doc_number = '10000007' AND p.usr_id_holder = h.usr_id
LIMIT 1;

-- Risk 7: póliza 4 (COLECTIVA) - asegurado user15
INSERT INTO risks (ris_id, pol_id, rst_id, usr_id_insured, ris_address)
SELECT RANDOM_UUID(), p.pol_id, rs.rst_id, u.usr_id, 'Transversal 93 # 51A-98, Bogotá'
FROM policies p, risk_states rs, users u, users h
WHERE rs.rst_name = 'ACTIVO' AND u.usr_doc_number = '10000015'
  AND h.usr_doc_number = '10000007' AND p.usr_id_holder = h.usr_id
LIMIT 1;
