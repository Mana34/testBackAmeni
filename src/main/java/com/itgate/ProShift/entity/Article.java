package com.itgate.ProShift.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String titre;

    @NotBlank
    @Size(max = 255)
    private String contenu;

    public long views;
    public int likes;

    private double price;
    private Date datePublication;
    private Date dateModification;

    private boolean freemium;
    // Set the creation date when inserting.
    @PrePersist
    protected void onCreate() {
        datePublication = new Date();

    }
    // Set the modification date when updating.
    @PreUpdate
    protected void onUpdate() {
        dateModification = new Date();
    }
}
