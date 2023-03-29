package com.miss.services.common.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Venues implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name must be provided!")
    @Size(max = 100, message = "Name must less than 100 characters!")
    @Column(nullable = false)
    private String venueName;

    @NotBlank(message = "Address must be provided!")
    @Size(max = 255, message = "Address must less than 255 characters!")
    @Column(nullable = false)
    private String venueAddr;

    @Min(value = 1, message = "Number of seat must at least 1")
    @Max(value = 100, message = "Number of seat must less than 100")//to prevent the do while of generating unique qr code long run
    @Column(nullable = false)
    private int numberOfSeat;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Collection<Events> events = new ArrayList<>();

}
