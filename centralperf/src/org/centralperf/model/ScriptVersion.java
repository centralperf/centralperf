package org.centralperf.model;

import javax.persistence.*;
import java.util.List;

/**
 * @author Charles LE GALLIC
 */
@Entity
public class ScriptVersion {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Number of the version
     */
    @OrderColumn
    private Long number;

    /**
     * Short description for this version
     */
    private String description;

    /**
     * Set of variables for this script
     */
    @OneToMany(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    private List<ScriptVariableSet> scriptVariableSets;

    @Lob
    @Column( length = 100000 )
    public String jmx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scriptId")
    private Script script;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ScriptVariableSet> getScriptVariableSets() {
        return scriptVariableSets;
    }

    public void setScriptVariableSets(List<ScriptVariableSet> scriptVariableSets) {
        this.scriptVariableSets = scriptVariableSets;
    }

    public String getJmx() {
        return jmx;
    }

    public void setJmx(String jmx) {
        this.jmx = jmx;
    }
}
