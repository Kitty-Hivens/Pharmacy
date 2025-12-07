package haru.pharmacy;

import haru.pharmacy.dto.customer.CustomerCreateDto;
import haru.pharmacy.dto.customer.CustomerResponseDto;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.mapper.CustomerMapper;
import haru.pharmacy.model.Customer;
import haru.pharmacy.repository.CustomerRepository;
import haru.pharmacy.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CustomerServiceImpl}.
 * <p>
 * Tests CRUD operations for pharmacy customers.
 *
 * @author Haru
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository repository;
    @Mock private CustomerMapper mapper;

    @InjectMocks
    private CustomerServiceImpl service;

    /**
     * Tests successful retrieval of a customer by ID.
     */
    @Test
    @DisplayName("Get should return customer DTO when found")
    void get_ShouldReturnDto_WhenFound() {
        Long id = 1L;
        Customer entity = new Customer();
        CustomerResponseDto expectedDto = new CustomerResponseDto(id, BigDecimal.ZERO, "Ivan", "Ivanov", "123");

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(expectedDto);

        CustomerResponseDto result = service.get(id);

        assertEquals("Ivan", result.firstName());
    }

    /**
     * Tests that retrieval throws an exception if the customer does not exist.
     */
    @Test
    @DisplayName("Get should throw exception when not found")
    void get_ShouldThrow_WhenNotFound() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }
    
    /**
     * Tests the create method flow.
     */
    @Test
    @DisplayName("Create should map and save entity")
    void create_ShouldSave() {
        CustomerCreateDto dto = new CustomerCreateDto(BigDecimal.ZERO, "A", "B", "123");
        Customer entity = new Customer();
        
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        
        service.create(dto);
        
        verify(repository).save(entity);
    }
}
