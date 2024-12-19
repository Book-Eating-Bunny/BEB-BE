package com.beb.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Optional;

@Entity
@Table(name = "category",
uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent_id", "mall_type"}))
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mall_type", nullable = false)
    private MallType mallType;

    @Getter
    public enum MallType {
        KOREAN_BOOK("국내도서"),
        FOREIGN_BOOK("외국도서");

        private final String label;

        MallType(String label) {
            this.label = label;
        }

        public static Optional<MallType> valueOfLabel(String label) {
            for (MallType mallType : MallType.values()) {
                if (mallType.label.equals(label)) {
                    return Optional.of(mallType);
                }
            }
            return Optional.empty();
        }
    }
}
