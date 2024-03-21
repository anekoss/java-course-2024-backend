package edu.java.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Table(name = "links")
@Entity
@Getter
@Setter
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private URI uri;
    @Enumerated(EnumType.STRING)
    private LinkType type;
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    @Column(name = "checked_at")
    private OffsetDateTime checkedAt;

    @ManyToMany(mappedBy = "links")
    private Set<TgChat> tgChats;

    public Link() {

    }

    public Link(URI uri, LinkType type) {
        this.uri = uri;
        this.type = type;
        this.checkedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

}
