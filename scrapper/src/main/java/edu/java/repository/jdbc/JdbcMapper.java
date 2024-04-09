package edu.java.repository.jdbc;

import edu.java.domain.ChatLink;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.TgChat;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JdbcMapper {
    private JdbcMapper() {
    }

    static List<TgChat> listMapToTgChatList(List<Map<String, Object>> tgChatList) {
        return tgChatList.stream()
            .map(m -> new TgChat().setId((Long) m.get("id")).setChatId((Long) m.get("chat_id")))
            .toList();
    }

    static List<Link> listMapToLinkList(List<Map<String, Object>> linkList) {
        List<Link> links = new ArrayList<>();
        linkList.forEach(m -> {
            Link link = new Link();
            link.setId((Long) m.get("id"))
                .setUri(URI.create((String) m.get("uri")))
                .setLinkType(LinkType.valueOf((String) m.get("link_type")))
                .setUpdatedAt(OffsetDateTime.ofInstant(((Timestamp) m.get("updated_at")).toInstant(), ZoneOffset.UTC))
                .setCheckedAt(OffsetDateTime.ofInstant(((Timestamp) m.get("checked_at")).toInstant(), ZoneOffset.UTC))
                .setTgChats(Set.of());
            links.add(link);
        });
        return links;
    }

    static List<ChatLink> listMapToChatLinkList(List<Map<String, Object>> chatLinkList) {
        List<ChatLink> chatLinks = new ArrayList<>();
        chatLinkList.forEach(m -> {
            ChatLink chatLink = new ChatLink((Long) m.get("tg_chat_id"), (Long) m.get("link_id"));
            chatLinks.add(chatLink);
        });
        return chatLinks;
    }

}
