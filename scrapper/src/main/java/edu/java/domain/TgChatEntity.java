package edu.java.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cascade;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "tg_chats")
@Accessors(chain = true)
public class TgChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long chatId;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "tg_chat_links",
        joinColumns = {@JoinColumn(name = "tg_chat_id")},
        inverseJoinColumns = {@JoinColumn(name = "link_id")}
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private Set<LinkEntity> links;

    public TgChatEntity(Long id, Long chatId) {
        this.id = id;
        this.chatId = chatId;
    }

    public void addLink(LinkEntity link) {
        link.getTgChats().add(this);
        this.getLinks().add(link);
    }

    public void removeLink(LinkEntity link) {
        link.getTgChats().remove(this);
        this.getLinks().remove(link);
    }

}
