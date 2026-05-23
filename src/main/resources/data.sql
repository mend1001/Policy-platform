INSERT INTO policies (tipo, estado, canon, prima, fecha_inicio, fecha_fin, tomador_id, beneficiario_id) VALUES
('INDIVIDUAL', 'ACTIVA',   1500000.00, 18000000.00, '2024-01-01', '2025-01-01', 1, 2),
('INDIVIDUAL', 'RENOVADA', 2000000.00, 24000000.00, '2023-06-01', '2025-06-01', 3, 4),
('COLECTIVA',  'ACTIVA',   3500000.00, 84000000.00, '2024-01-01', '2026-01-01', 5, 6),
('COLECTIVA',  'ACTIVA',   4200000.00, 50400000.00, '2024-07-01', '2025-07-01', 7, 8);

INSERT INTO risks (poliza_id, asegurado_id, direccion, estado) VALUES
(1, 10, 'Calle 72 # 10-34, Bogotá',                   'ACTIVO'),
(2, 11, 'Carrera 15 # 93-45, Bogotá',                 'ACTIVO'),
(3, 12, 'Avenida El Dorado # 68C-61, Bogotá',         'ACTIVO'),
(3, 13, 'Calle 100 # 9-67, Bogotá',                   'ACTIVO'),
(3, 14, 'Carrera 7 # 32-21, Bogotá',                  'CANCELADO'),
(4, 15, 'Calle 26 # 92-32, Bogotá',                   'ACTIVO'),
(4, 16, 'Transversal 93 # 51A-98, Bogotá',            'ACTIVO');
