package com.rtr.nettest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "title_de")
    private String titleDe;

    @Column(name = "text_en")
    private String textEn;

    @Column(name = "text_de")
    private String textDe;

    private boolean active = true;

    private String errorLabel = "";

    private boolean error = false;
}
