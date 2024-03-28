package edu.java.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table(name = "tg_chats")
@Entity
@Getter
@Setter
public class TgChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "tg_chat_links",
            joinColumns = {@JoinColumn(name = "tg_chat_id")},
            inverseJoinColumns = {@JoinColumn(name = "link_id")}
    )
    private Set<Link> links = new HashSet<>();

    public TgChat() {

    }

    public TgChat(Long chatId) {
        this.chatId = chatId;
    }

    public TgChat(Long id, Long chatId) {
        this.id = id;
        this.chatId = chatId;
    }

}
