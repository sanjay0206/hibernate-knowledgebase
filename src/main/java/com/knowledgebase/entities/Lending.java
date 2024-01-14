package com.knowledgebase.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lendings")
public class Lending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lending_id")
    private Long lendingId;

    @ManyToOne
    @JoinColumn(name = "book_id", updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Column(name = "date_out", updatable = false)
    private LocalDate dateOut;

    @Column(name = "date_returned", updatable = false)
    private LocalDate dateReturned;

    @Column(name = "due_date")
    private LocalDate dueDate;
}