package haru.pharmacy.service.interfaces;

import haru.pharmacy.dto.customer.CustomerCreateDto;
import haru.pharmacy.dto.customer.CustomerResponseDto;
import haru.pharmacy.dto.customer.CustomerUpdateDto;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto create(CustomerCreateDto dto);

    CustomerResponseDto update(Long id, CustomerUpdateDto dto);

    CustomerResponseDto get(Long id);

    List<CustomerResponseDto> getAll();

    void delete(Long id);
}
