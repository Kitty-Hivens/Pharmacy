package haru.pharmacy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "medicine_id")
    @ManyToOne
    private Medicine medicine;

    @JoinColumn(name = "supplier_id")
    @ManyToOne
    private Supplier supplier;

    private Integer stockQuantity;
    private String batchNumber;
    private LocalDate expirationDate;

    @Version
    private Long version;
}
