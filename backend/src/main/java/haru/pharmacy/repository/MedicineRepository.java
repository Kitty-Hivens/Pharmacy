package haru.pharmacy.repository;

import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Query("""
        SELECT new haru.pharmacy.dto.medicine.MedicineResponseDto(
            m.id,
            m.name,
            m.price,
            m.manufacturer,
            m.description,
            m.prescriptionRequired,
            COALESCE(SUM(i.stockQuantity), 0L)
        )
        FROM Medicine m
        LEFT JOIN Inventory i ON i.medicine.id = m.id
        GROUP BY m.id, m.name, m.price, m.manufacturer, m.description, m.prescriptionRequired
    """)
    List<MedicineResponseDto> findAllSummarized();

    @Query("""
        SELECT new haru.pharmacy.dto.medicine.MedicineResponseDto(
            m.id,
            m.name,
            m.price,
            m.manufacturer,
            m.description,
            m.prescriptionRequired,
            COALESCE(SUM(i.stockQuantity), 0L)
        )
        FROM Medicine m
        LEFT JOIN Inventory i ON i.medicine.id = m.id
        WHERE m.id = :id
        GROUP BY m.id, m.name, m.price, m.manufacturer, m.description, m.prescriptionRequired
    """)
    Optional<MedicineResponseDto> findDtoById(@Param("id") Long id);
}
