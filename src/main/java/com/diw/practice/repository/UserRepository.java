package com.diw.practice.repository;

import com.diw.practice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for User entity operations.
 * Inherits standard CRUD methods from JpaRepository.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

}