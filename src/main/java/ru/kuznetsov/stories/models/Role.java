package ru.kuznetsov.stories.models;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(length = 30)
    String roleName;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "roles")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Set<User> users = new HashSet<>();

    @Override
    public String getAuthority() {
        return roleName;
    }
}
