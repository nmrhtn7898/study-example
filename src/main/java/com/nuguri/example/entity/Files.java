package com.nuguri.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", length = 1)
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@Getter
@Setter
public class Files extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String filePath;

    @Column(nullable = false, unique = true)
    private String name;

    public Files(Long id, String filePath, String name) {
        this.id = id;
        this.filePath = filePath;
        this.name = name;
    }

}
