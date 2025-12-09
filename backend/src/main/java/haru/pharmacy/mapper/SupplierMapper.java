package haru.pharmacy.mapper;

import haru.pharmacy.dto.SupplierDto;
import haru.pharmacy.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierDto dto);
    SupplierDto toDto(Supplier entity);
    void updateEntity(SupplierDto dto, @MappingTarget Supplier entity);
}