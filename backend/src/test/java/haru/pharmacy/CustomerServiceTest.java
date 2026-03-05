package haru.pharmacy;

import haru.pharmacy.dto.customer.CustomerCreateDto;
import haru.pharmacy.dto.customer.CustomerResponseDto;
import haru.pharmacy.dto.customer.CustomerUpdateDto;
import haru.pharmacy.exception.BusinessConstraintException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CustomerServiceImpl}.
 *
 * @author Haru
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository repository;
    @Mock private CustomerMapper mapper;

    @InjectMocks private CustomerServiceImpl service;

    @Test
    @DisplayName("Create should map and save entity")
    void create_ShouldSave() {
        CustomerCreateDto dto = new CustomerCreateDto(BigDecimal.ZERO, "Ivan", "Ivanov", "123");
        Customer entity = new Customer();
        Customer saved = new Customer();
        saved.setId(1L);
        CustomerResponseDto response = new CustomerResponseDto(1L, BigDecimal.ZERO, "Ivan", "Ivanov", "123");

        when(repository.existsByPhone(dto.phone())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        CustomerResponseDto result = service.create(dto);

        assertEquals(1L, result.id());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Create should throw when phone exists")
    void create_ShouldThrow_WhenPhoneExists() {
        CustomerCreateDto dto = new CustomerCreateDto(BigDecimal.ZERO, "Ivan", "Ivanov", "123");
        when(repository.existsByPhone(dto.phone())).thenReturn(true);

        assertThrows(BusinessConstraintException.class, () -> service.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Update should modify entity when found")
    void update_ShouldUpdate_WhenFound() {
        Long id = 1L;
        CustomerUpdateDto dto = new CustomerUpdateDto(BigDecimal.TEN, "Petr", "Petrov", "321");
        Customer existing = new Customer();
        existing.setPhone("321");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(new CustomerResponseDto(id, BigDecimal.TEN, "Petr", "Petrov", "321"));

        CustomerResponseDto result = service.update(id, dto);

        assertEquals("Petr", result.firstName());
        verify(mapper).updateEntity(dto, existing);
    }

    @Test
    @DisplayName("Update should throw when phone exists for different customer")
    void update_ShouldThrow_WhenPhoneExists() {
        Long id = 1L;
        CustomerUpdateDto dto = new CustomerUpdateDto(BigDecimal.TEN, "Petr", "Petrov", "321");
        Customer existing = new Customer();
        existing.setPhone("123");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.existsByPhone(dto.phone())).thenReturn(true);

        assertThrows(BusinessConstraintException.class, () -> service.update(id, dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Update should throw when not found")
    void update_ShouldThrow_WhenNotFound() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(id, new CustomerUpdateDto(null, null, null, null)));
    }

    @Test
    @DisplayName("Get should return DTO")
    void get_ShouldReturnDto() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new Customer()));
        when(mapper.toDto(any())).thenReturn(new CustomerResponseDto(id, BigDecimal.ZERO, "A", "B", "C"));

        assertNotNull(service.get(id));
    }

    @Test
    @DisplayName("Get should throw when not found")
    void get_ShouldThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(99L));
    }

    @Test
    @DisplayName("GetAll should return list")
    void getAll_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new Customer()));
        assertFalse(service.getAll().isEmpty());
    }

    @Test
    @DisplayName("Delete should call repository")
    void delete_ShouldCallRepo() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }
}
