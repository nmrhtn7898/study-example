package com.nuguri.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@DiscriminatorValue("p")
@PrimaryKeyJoinColumn(name = "file_id")
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
public class ProfileImage extends Files {

    @Builder
    public ProfileImage(Long id, String filePath, String name) {
        super(id, filePath, name);
    }

}
