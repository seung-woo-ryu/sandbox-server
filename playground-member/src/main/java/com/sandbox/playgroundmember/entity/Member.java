package com.sandbox.playgroundmember.entity;

import com.sandbox.common.security.SecurityRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private SecurityRole role;

    public static Member createWithRole(String email, String nickname, SecurityRole role) {
        Member m = new Member();
        m.setEmail(email);
        m.setNickname(nickname);
        m.setRole(role);
        return m;
    }

    public static Member createUser(String email, String nickname) {
        return createWithRole(email, nickname, SecurityRole.USER);
    }

    public static Member createAdmin(String email, String nickname) {
        return createWithRole(email, nickname, SecurityRole.ADMIN);
    }
}
