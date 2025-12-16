package com.kshrd.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sections")
@Getter
@Setter

public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID section_id;

    private String sectionName;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Question> questions;

}
