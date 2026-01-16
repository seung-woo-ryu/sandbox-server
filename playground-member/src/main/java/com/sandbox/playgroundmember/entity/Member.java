package com.sandbox.playgroundmember.entity;

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

    @Column(nullable = false, unique = true)
    private String nickname;

    public static Member of(String email, String nickname) {
        Member m = new Member();
        m.setEmail(email);
        m.setNickname(nickname);
        return m;
    }
}
