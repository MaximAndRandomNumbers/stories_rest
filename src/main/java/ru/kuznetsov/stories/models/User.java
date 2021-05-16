package ru.kuznetsov.stories.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Not;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String email;

    @Column(name="reg_date")
    private Date registrationDate;
    private Boolean enabled;

    @OneToMany(mappedBy = "user")
    List<Notification> notifications;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_role",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")})
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<Story> stories = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserStoryMark> marks = new HashSet<>();

    @OneToOne(mappedBy = "moderator")
    private ModeratedStory moderatedStory;

}
