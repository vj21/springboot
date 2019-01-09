package com.vanderlei.apiuol.repository;

import com.vanderlei.apiuol.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {
}