package ru.ufanet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Transient
    private long noteId;

    @Column(name = "userId")
    private long userId;

    @Transient
    private String cmdText;

    @Column(name = "noteText")
    private String noteText;
}