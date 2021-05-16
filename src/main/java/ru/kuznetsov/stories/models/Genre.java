package ru.kuznetsov.stories.models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToMany(mappedBy = "genres")
    Set<Story> stories = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj instanceof Genre){
            Genre that = (Genre) obj;
            return this.id.equals(that.id) && this.name.equals(that.name);
        }
        return false;
    }
}
