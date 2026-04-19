package com.example.servidorcalendario.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "usuarios_grupos")
@IdClass(UsuarioGrupo.UsuarioGrupoId.class)
public class UsuarioGrupo {

    @Id
    private Integer idUsuario;

    @Id
    private Integer idGrupo;

    public UsuarioGrupo() {}

    public UsuarioGrupo(Integer idUsuario, Integer idGrupo) {
        this.idUsuario = idUsuario;
        this.idGrupo = idGrupo;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }

    // ── Clase interna para clave compuesta ──

    public static class UsuarioGrupoId implements Serializable {
        private Integer idUsuario;
        private Integer idGrupo;

        public UsuarioGrupoId() {}

        public UsuarioGrupoId(Integer idUsuario, Integer idGrupo) {
            this.idUsuario = idUsuario;
            this.idGrupo = idGrupo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioGrupoId that = (UsuarioGrupoId) o;
            return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(idGrupo, that.idGrupo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idUsuario, idGrupo);
        }
    }
}
