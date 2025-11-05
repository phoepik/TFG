CREATE DATABASE aplicacion_proyecto;
USE aplicacion_proyecto;

CREATE TABLE Usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    correo VARCHAR(100) UNIQUE,
    contrasena VARCHAR(255)
);

CREATE TABLE Grupo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT
);

CREATE TABLE Preferencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    zona_horaria VARCHAR(10),
    horario_preferido VARCHAR(100),
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

CREATE TABLE Calendario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT,
    grupo_id INT,
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    FOREIGN KEY (grupo_id) REFERENCES Grupo(id)
);

CREATE TABLE Grupo_Usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id INT,
    usuario_id INT,
    FOREIGN KEY (grupo_id) REFERENCES Grupo(id),
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

CREATE TABLE Evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100),
    descripcion TEXT,
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    calendario_id INT,
    FOREIGN KEY (calendario_id) REFERENCES Calendario(id)
);

CREATE TABLE Alerta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mensaje TEXT,
    fecha DATETIME,
    evento_id INT,
    FOREIGN KEY (evento_id) REFERENCES Evento(id)
);

CREATE TABLE Notificacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mensaje TEXT,
    fecha DATETIME,
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

CREATE TABLE PropuestaActividad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT,
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);
