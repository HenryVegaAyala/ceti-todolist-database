package com.mvc.todolist.infrastructure.adapter.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity // Marca esta clase como una entidad JPA que se mapea a una tabla en la base de datos.
@Table(name = "roles") // Especifica el nombre de la tabla en la base de datos. En este caso, la tabla se llamará roles.
@Getter // Genera automáticamente métodos getter para todos los campos (de Lombok).
@Setter // Genera automáticamente métodos setter para todos los campos (de Lombok).
@Builder // Implementa el patrón Builder para crear instancias de la clase de forma fluida (de Lombok).
@NoArgsConstructor // Genera un constructor sin argumentos, requerido por JPA (de Lombok).
@AllArgsConstructor // Genera un constructor con todos los campos como parámetros (de Lombok).
@ToString(exclude = "users") // Genera el método toString() excluyendo el campo users para evitar referencias circulares (de Lombok).
@EqualsAndHashCode(exclude = "users") // Genera los métodos equals() y hashCode() excluyendo el campo users para evitar problemas con relaciones bidireccionales (de Lombok).
public class RoleEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserEntity> users = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

