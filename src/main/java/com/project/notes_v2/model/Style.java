package com.project.notes_v2.model;

import com.project.notes_v2.enumeration.Police;
import com.project.notes_v2.enumeration.Unit;
import com.project.notes_v2.enumeration.Bloc;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="style")
public class Style {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Bloc bloc;
    private Police police;
    private Unit unit;
    private int policeSize;
    private int h1Size;
    private int h2Size;
    private int h3Size;
    private int h4Size;
    private int h5Size;
    private int h6Size;
    private Integer indent;
    private String fontColor;
    private String backgroundColor;
    private String borderColor;
    private String hoverBorderColor;
    private int borderWidth;

}
