package com.nuguri.example.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Files(String filePath, String name) {
        this.filePath = filePath;
        this.name = name;
    }

}
