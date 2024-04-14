package edu.java.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "links")
@Accessors(chain = true)
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private URI uri;

    @Enumerated(EnumType.STRING)
    private LinkType linkType;

    private OffsetDateTime updatedAt;

    private OffsetDateTime checkedAt;

    @ManyToMany(mappedBy = "links")
    private Set<TgChatEntity> tgChats;

    public LinkEntity(Long id, URI uri, LinkType linkType, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        this.id = id;
        this.uri = uri;
        this.linkType = linkType;
        this.checkedAt = checkedAt;
        this.updatedAt = updatedAt;
    }

    public void addTgChat(TgChatEntity tgChat) {
        tgChat.getLinks().add(this);
        this.getTgChats().add(tgChat);
    }

    public void removeTgChat(TgChatEntity tgChat) {
        tgChat.getLinks().remove(this);
        this.getTgChats().remove(tgChat);
    }
}
