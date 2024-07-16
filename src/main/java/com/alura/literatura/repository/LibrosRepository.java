package com.alura.literatura.repository;

import com.alura.literatura.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibrosRepository extends JpaRepository<Libros,Long> {

    Libros findByTituloContainsIgnoreCase(String nombreLibro);

    List<Libros> findByIdiomas(String idioma);

}
