package com.example.planifest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.entity.Supply;
import com.example.planifest.service.SupplyServiceImp;

/* Define que la clase es un controlador para spring */
@RestController
/* Ruta base */
@RequestMapping("/supplies")
public class SupplyController {

    /* Se trae y se define a Service como un Objeto?? */
    private final SupplyServiceImp supplyService;

    /* Constructor de Service para traer las instrucciones */
    public SupplyController(SupplyServiceImp supplyService) {
        this.supplyService = supplyService;
    }

    /* Metodos con sus EndPoints-Rutas */

    /* Metodo para Obtener todos los Suministros/Recursos */
    @GetMapping
    /* En caso de una solicitud http asi */
    public ResponseEntity<List<Supply>> getAllSupplies() {
        /* Devuelve una respuesta y la info del service */
        return ResponseEntity.ok(supplyService.getAll());
    }

    @PostMapping
    /* Al no devolver nada no nesecita definirse el "Formato" */
    public ResponseEntity<?> createSupply(@RequestBody Supply supply) {
        /* Envia una respuesta http de que fue creado pero "Sin cuerpo" */
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    /* El path es para almacenar el ID? */
    public ResponseEntity<?> updateSupply(@PathVariable Long id, @RequestBody Supply supply) {
        supply.setId(id);
        /* Actualizacion de el Suministro por el ID */
        supplyService.update(supply);
        /* Respuesta de devuelve */
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupply(@PathVariable Long id) {
        /* Accede al metodo del SERVICE */
        supplyService.deleteById(id);
        /* Da el mensaje */
        return ResponseEntity.ok().build();
    }

}
