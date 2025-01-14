package com.beb.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "review_like", uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "comment_id"}))
@Getter @Setter
@NoArgsConstructor
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public ReviewLike(Member member, Comment comment) {
        this.member = member;
        this.comment = comment;
    }
}
