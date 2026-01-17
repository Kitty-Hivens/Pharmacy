package haru.pharmacy.mapper;

import haru.pharmacy.dto.medicine.MedicineCreateDto;
import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.dto.medicine.MedicineUpdateDto;
import haru.pharmacy.model.Medicine;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MedicineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isArchived", ignore = true) // Default value is set in Entity
    Medicine toEntity(MedicineCreateDto dto);

    @Mapping(target = "quantity", ignore = true)
    MedicineResponseDto toDto(Medicine entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    void updateEntity(MedicineUpdateDto dto, @MappingTarget Medicine entity);

    default MedicineResponseDto toDto(Medicine entity, Long quantity) {
        if (entity == null) return null;
        MedicineResponseDto base = toDto(entity);
        return new MedicineResponseDto(
                base.id(),
                base.name(),
                base.price(),
                base.manufacturer(),
                base.description(),
                base.prescriptionRequired(),
                quantity
        );
    }
}
