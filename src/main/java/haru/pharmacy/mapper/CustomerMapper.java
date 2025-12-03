package haru.pharmacy.mapper;

import haru.pharmacy.dto.customer.CustomerCreateDto;
import haru.pharmacy.dto.customer.CustomerResponseDto;
import haru.pharmacy.dto.customer.CustomerUpdateDto;
import haru.pharmacy.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerCreateDto dto);

    CustomerResponseDto toDto(Customer entity);

    void updateEntity(CustomerUpdateDto dto, @MappingTarget Customer entity);
}
