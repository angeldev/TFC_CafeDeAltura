-- Script para crear el usuario de ejemplo y otorgar permisos en MySQL
-- Autor: Lola Fernández Fuentes 

-- Crea el usuario solo si no existe
CREATE USER IF NOT EXISTS 'cafedbuser'@'localhost' IDENTIFIED BY 'cafedbpass';

-- Otorga todos los privilegios sobre la base de datos del proyecto
GRANT ALL PRIVILEGES ON tfc_cafedealtura.* TO 'cafedbuser'@'localhost';

-- Aplica los cambios de privilegios
FLUSH PRIVILEGES;

-- Fin del script de usuario y permisos 