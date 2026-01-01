CREATE DATABASE base_calendario;
USE base_calendario;

-- 1. TABLA DE USUARIOS, (las personas que usarán la app)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT, -- ID que se genera solo
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL, -- UNIQUE para q no coincidan correos
    contrasena VARCHAR(255) NOT NULL,
    notificaciones_activas BOOLEAN DEFAULT TRUE -- TRUE = si, FALSE = no
);

-- 2. TABLA DE GRUPOS, (que varios usuarios se junten)
CREATE TABLE IF NOT EXISTS grupos (
    id_grupo INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_admin INT, -- ID del usuario que manda en el grupo
    FOREIGN KEY (id_admin) REFERENCES usuarios(id_usuario)
);

-- 3. RELACIÓN USUARIOS Y GRUPOS, (un usuario puede estar en muchos grupos y un grupo muchos usuarios)
CREATE TABLE IF NOT EXISTS usuarios_grupos (
    id_usuario INT,
    id_grupo INT,
    PRIMARY KEY (id_usuario, id_grupo),
    -- ON DELETE CASCADE: Si borramos un usuario, se quita automáticamente de sus grupos.
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    -- ON DELETE CASCADE: Si el grupo se elimina, la lista de miembros se borra también.
    FOREIGN KEY (id_grupo) REFERENCES grupos(id_grupo) ON DELETE CASCADE
);

-- 4. TABLA DE CALENDARIOS, (guarda eventos)
CREATE TABLE IF NOT EXISTS calendarios (
    id_calendario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,	-- Ej: "Calendario Trabajo", "Calendario gimnasio"
    tipo ENUM('PERSONAL', 'GRUPAL') NOT NULL,
    id_propietario INT, -- Si es personal, aquí va el ID del dueño, no opcional
    id_grupo INT,   -- Si es de un grupo, aquí va el ID del grupo, opcional
    FOREIGN KEY (id_propietario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_grupo) REFERENCES grupos(id_grupo)
);

-- 5. TABLA DE EVENTOS, (eventos q registra el usuario)
CREATE TABLE IF NOT EXISTS eventos (
    id_evento INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    ubicacion VARCHAR(255),
    estado ENUM('PENDIENTE', 'CONFIRMADO') DEFAULT 'CONFIRMADO',
    id_calendario INT NOT NULL,
    -- ON DELETE CASCADE: Si borras un calendario (ej: "Trabajo"), 
    -- se borran automáticamente todos los eventos que tenía dentro.
    FOREIGN KEY (id_calendario) REFERENCES calendarios(id_calendario) ON DELETE CASCADE
);

-- 6. TABLA DE NOTIFICACIONES, (notificar al usuario)
CREATE TABLE notificaciones (
    id_notificacion INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL, -- Ej: 'Recordatorio de quedada'
    mensaje TEXT, -- Ej: 'La quedada es en 15 minutos'
    -- Si es un recordatorio, guardar cuántos minutos antes del evento debe saltar, sino no poner nada.
    tiempo_anticipacion INT, 
    tipo ENUM('RECORDATORIO', 'INVITACION', 'SISTEMA') NOT NULL,
    id_usuario INT NOT NULL,
    id_evento INT DEFAULT NULL, -- (Opcional) Si el aviso es sobre un evento específico
    -- Si borras al usuario, sus avisos se van con él.
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    -- Si borras el evento, el recordatorio desaparece automáticamente.
    FOREIGN KEY (id_evento) REFERENCES eventos(id_evento) ON DELETE CASCADE
);