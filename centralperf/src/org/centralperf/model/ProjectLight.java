package org.centralperf.model;

public class ProjectLight {
    private Long id;
    private String name;
    
    public ProjectLight(Project project) {
		this.id=project.getId();
		this.name=project.getName();
	}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
