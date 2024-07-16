package com.alura.literatura.principal;

import com.alura.literatura.model.*;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibrosRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.util.*;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private List<DatosLibros> datosLibros = new ArrayList<>();
    private LibrosRepository repository;
    private AutorRepository repositoryAutor;
    private List<Libros> libros;
    private List<Autor> autor;

    public Principal (){}
    public Principal(LibrosRepository repository, AutorRepository repositoryAutor) {
        this.repository = repository;
        this.repositoryAutor = repositoryAutor;
    }

    public void opcionesMenu (){
        var opcion = -1;
        while (opcion != 0) {

            infoMenu();
            scannerSoloNumeros();

            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    mostrarLibrosRegistrados();
                    break;
                case 3:
                    mostrarAutoresRegistrados();
                    break;
                case 4:
                    buscarAutorVivoEnAño();
                    break;
                case 5:
                    buscarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    // Forzar salida de la aplicación
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }


    public void buscarLibro() {
        System.out.println("Ingrese nombre del libro");
        var buscaLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + buscaLibro.replace(" ", "+"));
        var buscador = convierteDatos.obtenerDatos(json, DatosGenerales.class);

        Optional<DatosLibros> libroBuscado = buscador.resultado().stream()
                .filter(l -> l.titulo().toUpperCase().contains(buscaLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibros datosLibros = libroBuscado.get();
            DatosAutor datosAutor = datosLibros.autor().get(0);
            Autor autor = repositoryAutor.findByNombre(datosAutor.nombre());

            if (autor == null) {

                autor = new Autor(datosAutor);
                repositoryAutor.save(autor);
            }

            Libros libro = repository.findByTituloContainsIgnoreCase(datosLibros.titulo());


            if (libro == null) {
                System.out.println("¡Libro encontrado!");
                libro = new Libros(datosLibros, autor);
                repository.save(libro);
                System.out.println(libro);
            }else {
                System.out.println("Libro ya esta Registrado");
            }
        } else {
            System.out.println("Libro no encontrado");
        }
    }

    private void mostrarLibrosRegistrados(){
        libros = repository.findAll();
        libros.forEach(System.out::println);
    }

    private void mostrarAutoresRegistrados(){
        autor = repositoryAutor.findAll();
        autor.forEach(System.out::println);
    }

    private void buscarLibrosPorIdioma(){
        System.out.println("""
        --------------------------------
        Ingrese numero de idioma deseado
        
        1- Ingles
        2- Español
        3- Portuges
        4- Italiano
        
        -------------------------------
        """);
        scannerSoloNumeros();
        var  numero = teclado.nextInt();
        switch (numero) {
            case 1:
                buscarIdioma("en");
                break;
            case 2:
                buscarIdioma("es");
                break;
            case 3:
                buscarIdioma("pt");
                break;
            case 4:
                buscarIdioma("it");
                break;
            default:
                System.out.println("Opción inválida");
        }
    }
    private void buscarIdioma(String idioma) {
        try {
            libros = repository.findByIdiomas(idioma);

            if (libros == null) {
                System.out.println("No hay Libros registrados");
            } else {
                libros.forEach(System.out::println);
            }
        }catch (Exception e){
            System.out.println("Error en la busqueda");
        }

    }

    private void buscarAutorVivoEnAño () {
        System.out.println("Ingrese año");
        scannerSoloNumeros();
        var año = teclado.nextInt();
        autor = repositoryAutor.autoresVivosEnDeterminadoAño(año);
        if (autor == null) {
            System.out.println("No hay registro de autores en ese año");
        } else {
            autor.forEach(System.out::println);
        }

    }

    private void infoMenu(){
        var menu = """
                    ----------------------------------------
                        MENU:
                    
                    1 - Buscar libros por titulo
                    2 - Mostrar libros registrados
                    3 - Mostrar autores registrados
                    4 - Mostrar autores vivos en determinado año
                    5 - Mostrar libros por idiomas
               
                        
                    0 - Salir
                    -----------------------------------------
                    """;
        System.out.println(menu);
    }
    //filtro de opcion para solo permitir numeros
    private void scannerSoloNumeros() {
        while (!teclado.hasNextInt()) {
            System.out.println("ingrese solo numeros");
            teclado.next();
        }

    }
}