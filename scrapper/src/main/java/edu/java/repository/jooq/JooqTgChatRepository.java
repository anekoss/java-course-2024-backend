package edu.java.repository.jooq;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChat;
import edu.java.domain.jooq.tables.records.TgChatsRecord;
import edu.java.repository.TgChatRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.tables.TgChats.TG_CHATS;

@Repository
@RequiredArgsConstructor
public class JooqTgChatRepository implements TgChatRepository {
    private final DSLContext dslContext;

    @Override
    public long add(TgChat tgChat) throws ChatAlreadyExistException {
        TgChatsRecord tgChatsRecord = dslContext.insertInto(TG_CHATS)
                                                .set(TG_CHATS.CHAT_ID, tgChat.getChatId())
                                                .onConflictDoNothing()
                                                .returning(TG_CHATS.ID).fetchOne();
        if (tgChatsRecord == null || tgChatsRecord.getId() == null) {
            throw new ChatAlreadyExistException();
        }
        return tgChatsRecord.getId();
    }

    @Override
    public long remove(TgChat tgChat) throws ChatNotFoundException {
        TgChatsRecord tgChatsRecord = dslContext.deleteFrom(TG_CHATS)
                                                .where(TG_CHATS.CHAT_ID.eq(tgChat.getChatId()))
                                                .returning(TG_CHATS.ID)
                                                .fetchOne();
        if (tgChatsRecord == null || tgChatsRecord.getId() == null) {
            throw new ChatNotFoundException();
        }
        return tgChatsRecord.getId();
    }

    @Override
    @Transactional
    public List<TgChat> findAll() {
        return dslContext.selectFrom(TG_CHATS)
                         .fetch()
                         .map(chat -> new TgChat(chat.getId(), chat.getChatId()))
                         .stream()
                         .toList();
    }

    @Override
    @Transactional
    public TgChat findByChatId(Long chatId) throws ChatNotFoundException {
        TgChatsRecord tgChatsRecord = dslContext.selectFrom(TG_CHATS).where(TG_CHATS.CHAT_ID.eq(chatId)).fetchOne();
        if (tgChatsRecord == null) {
            throw new ChatNotFoundException();
        }
        return new TgChat(tgChatsRecord.getId(), tgChatsRecord.getChatId());
    }

    @Override
    @Transactional
    public Optional<TgChat> findById(Long id) {
        TgChatsRecord tgChatsRecord = dslContext.selectFrom(TG_CHATS).where(TG_CHATS.ID.eq(id)).fetchOne();
        if (tgChatsRecord == null) {
            return Optional.empty();
        }
        return Optional.of(new TgChat(tgChatsRecord.getId(), tgChatsRecord.getChatId()));
    }
}
