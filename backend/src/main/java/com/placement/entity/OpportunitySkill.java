package com.placement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "opportunity_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"opportunity_id", "skill_id"})
})
public class OpportunitySkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "is_mandatory", nullable = false)
    private boolean mandatory = true;

    public OpportunitySkill() {}

    public OpportunitySkill(Opportunity opportunity, Skill skill, boolean mandatory) {
        this.opportunity = opportunity;
        this.skill = skill;
        this.mandatory = mandatory;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }
}
