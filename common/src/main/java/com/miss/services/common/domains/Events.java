package com.miss.services.common.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Events implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Event Name must be provided!")
    @Size(max = 255, message = "Event Name must less than 255 characters!")
    @Column(nullable = false)
    private String eventName;

    private boolean isStartSell;

    @Temporal(TemporalType.TIMESTAMP)
    @Future(message = "Event Date must be a future date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date eventDtm;

    @ManyToOne(fetch = FetchType.EAGER)
    private Venues venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Collection<Tickets> tickets = new ArrayList<>();

}
