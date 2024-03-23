package edu.java.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "stackOverflow_links")
@Entity
public class StackOverflowLink extends Link {

    @Column(name = "answer_count")
    private Long answerCount;

    public StackOverflowLink() {

    }

}
