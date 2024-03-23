package edu.java.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "github_links")
@Entity
public class GithubLink extends Link {

    @ElementCollection
    @CollectionTable(name = "github_links", joinColumns = @JoinColumn(name = "link_id"))
    @Column(name = "branche")
    private List<String> branches = new ArrayList<>();

    public GithubLink() {

    }

}
