package haru.pharmacy.mapper;

import haru.pharmacy.dto.medicine.MedicineCreateDto;
import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.dto.medicine.MedicineUpdateDto;
import haru.pharmacy.model.Medicine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MedicineMapper {

    Medicine toEntity(MedicineCreateDto dto);

    MedicineResponseDto toDto(Medicine entity);

    void updateEntity(MedicineUpdateDto dto, @MappingTarget Medicine entity);
}