package edu.java.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

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

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "tg_chat_id")
    private Set<Link> links;

    public TgChat() {

    }

    public TgChat(Long chatId) {
        this.chatId = chatId;
    }

}
