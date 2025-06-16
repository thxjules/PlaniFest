package com.example.planifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{
  
    
    // Consulta por nombre de usuario
    User findByUsername(String username);
    // Consulta por correo electrónico
    User findByEmail(String email); // Cambia el tipo de retorno a User


    //Consulta por rol
    User findByRole(Role role);


}
