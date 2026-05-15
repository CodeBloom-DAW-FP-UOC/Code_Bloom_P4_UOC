package CodeBloom.AlquilaTusVehiculos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String plateNumber;

    private String brand;
    private String model;
    private Integer manufacturingYear;
    private String description;
    private Double dailyPrice;
    private String gasType;
    private Integer km;
    private String state;

    // Relación con Rental (1 vehículo → muchos rentals)
    @JsonIgnore
    @OneToMany(mappedBy = "vehicle")
    private List<Rental> rentals;
}