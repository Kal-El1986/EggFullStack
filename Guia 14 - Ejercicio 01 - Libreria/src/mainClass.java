
import entidades.Autor;
import entidades.Editorial;
import entidades.Libro;
import java.util.List;
import java.util.Scanner;
import servicios.AutorServicio;
import servicios.EditorialServicio;
import servicios.JPAController;
import servicios.LibroServicio;
//import servicios.LibroServicio;

public class mainClass {

    public static void main(String[] args) throws Exception {
        Scanner leer = new Scanner(System.in, "ISO-8859-1").useDelimiter("\n");

        LibroServicio libroServicio = new LibroServicio();
        AutorServicio autorServicio = new AutorServicio();
        EditorialServicio editorialServicio = new EditorialServicio();
        Libro libro = new Libro();

        int opcionMenu1, opcionMenu2;

        do {
            System.out.println("*************************************************************************");
            System.out.println("*                  Bienvenido al menu de la libreria                    *");
            System.out.println("*************************************************************************");
            System.out.println("*                   Seleccione la opcion que desee                      *");
            System.out.println("*************************************************************************");
            System.out.println("* 1 - Cargar un Libro, Autor o Editorial.                               *");
            System.out.println("* 2 - Dar de alta o baja un Libro, Autor o Editorial.                   *");
            System.out.println("* 3 - Mostrar el listado completo de los Autores, Editoriales o Libros. *");
            System.out.println("* 4 - Buscar los datos de un libro por su ISBN.                         *");
            System.out.println("* 5 - Buscar los datos de un libro por su titulo.                       *");
            System.out.println("* 6 - Buscar el o los libros de un mismo autor.                         *");
            System.out.println("* 7 - Buscar el o los libros de una misma editorial.                    *");
            System.out.println("* 8 - Buscar un autor o una editorial por nombre.                       *");
            System.out.println("* 9 - Eliminar un autor, editorial o libro.                             *");
            System.out.println("* 0 - Salir.                                                            *");
            System.out.println("*************************************************************************");
            opcionMenu1 = leer.nextInt();
            switch (opcionMenu1) {

                case 1:

                    String respuesta;
                    do {
                        System.out.println("Elija lo que desee cargar");
                        System.out.println("1 - Autor");
                        System.out.println("2 - Editorial");
                        System.out.println("3 - Libro");
                        opcionMenu2 = leer.nextInt();
                        switch (opcionMenu2) {
                            case 1:
                                System.out.println("Ingrese el nombre del Autor");
                                String nombreAutor = leer.next().toUpperCase();
                                autorServicio.agregarAutorSiNoExiste(nombreAutor);
                                break;
                            case 2:
                                System.out.println("Ingrese el nombre de la Editorial");
                                String nombreEditorial = leer.next().toUpperCase();
                                editorialServicio.agregarEditorialSiNoExiste(nombreEditorial);
                                break;
                            case 3:
                                libroServicio.creacionLibro(libro);
                                break;
                        }
                        System.out.println("¿Desea ingresar otro Autor, Editorial o Libro?");
                        System.out.println("S o N");
                        respuesta = leer.next();
                    } while (!respuesta.equalsIgnoreCase("N"));

                    break;

                case 2:
                    do {
                        System.out.println("Elija lo que desee dar de alta/baja");
                        System.out.println("1 - Autor");
                        System.out.println("2 - Editorial");
                        System.out.println("3 - Libro");
                        opcionMenu2 = leer.nextInt();
                        switch (opcionMenu2) {
                            case 1:
                                System.out.println("Ingrese el nombre del Autor");
                                String nombreAutor = leer.next().toUpperCase();
                                autorServicio.cambiarEstadoAutorPorNombre(nombreAutor);
                                break;
                            case 2:
                                System.out.println("Ingrese el nombre de la Editorial");
                                String nombreEditorial = leer.next().toUpperCase();
                                editorialServicio.cambiarEstadoEditorialPorNombre(nombreEditorial);
                                break;
                            case 3:
                                System.out.println("Ingrese el isbn del libro");
                                Long isbn = leer.nextLong();
                                libroServicio.cambiarEstadoLibroPorISBN(isbn);
                                break;
                        }
                        System.out.println("¿Desea dar de alta/baja algun otro autor, editorial o libro?");
                        System.out.println("S o N");
                        respuesta = leer.next();
                    } while (!respuesta.equalsIgnoreCase("N"));
                    break;

                case 3:
                    do {
                        System.out.println("Elija el listado completo que desea obtener");
                        System.out.println("1 - Autor");
                        System.out.println("2 - Editorial");
                        System.out.println("3 - Libro");
                        opcionMenu2 = leer.nextInt();
                        switch (opcionMenu2) {
                            case 1:
                                String listarAutores = null;
                                autorServicio.listarAutores(listarAutores);
                                break;
                            case 2:
                                String listarEditoriales = null;
                                editorialServicio.listarEditoriales(listarEditoriales);
                                break;
                            case 3:
                                System.out.println("Listado completo de los libros");
                                String listarLibros = null;
                                libroServicio.listarLibros(listarLibros);
                                break;
                        }
                        System.out.println("¿Desea revisar otro listado?");
                        System.out.println("S o N");
                        respuesta = leer.next();
                    } while (!respuesta.equalsIgnoreCase("N"));
                    break;

                case 4:
                    System.out.println("Ingrese el ISBN del libro");
                    long isbn = leer.nextLong();
                    libroServicio.buscarPorISBN(isbn);

                    break;
                case 5:
                    System.out.println("Ingrese el titulo del libro");
                    String titulo = leer.nextLine();
                    libroServicio.buscarPorTitulo(titulo);
                    break;
                case 6:
                    System.out.println("Ingrese el nombre del Autor");
                    String nombreAutor_1 = leer.next().toUpperCase();
                    libroServicio.buscarLibrosPorAutor(nombreAutor_1);
                    break;
                case 7:
                    System.out.println("Ingrese el nombre de la Editorial");
                    String nombreEditorial = leer.next().toUpperCase();
                    libroServicio.buscarPorEditorial(nombreEditorial);
                    break;
                case 8:
                    do {
                        System.out.println("Elija lo que desea buscar");
                        System.out.println("1 - Autor");
                        System.out.println("2 - Editorial");
                        opcionMenu2 = leer.nextInt();
                        switch (opcionMenu2) {
                            case 1:
                                System.out.println("Ingrese el nombre del autor a buscar");
                                String nombreAutor = leer.next().toUpperCase();
                                autorServicio.buscarPorNombre(nombreAutor);
                                break;
                            case 2:
                                System.out.println("Ingrese el nombre de la editorial a buscar");
                                String nombreEditorial_4 = leer.next().toUpperCase();
                                editorialServicio.buscarPorNombre(nombreEditorial_4);
                                break;
                        }
                        System.out.println("¿Desea buscar otro autor o editorial?");
                        System.out.println("S o N");
                        respuesta = leer.next();
                    } while (!respuesta.equalsIgnoreCase("N"));
                    break;
                case 9:
                    do {
                        System.out.println("Elija lo que desea eliminar");
                        System.out.println("1 - Autor");
                        System.out.println("2 - Editorial");
                        System.out.println("3 - Libro");
                        opcionMenu2 = leer.nextInt();
                        switch (opcionMenu2) {
                            case 1:
                                System.out.println("Ingrese el nombre del autor a eliminar");
                                String nombreAutor = leer.next().toUpperCase();
                                autorServicio.EliminarPorNombre(nombreAutor);
                                break;
                            case 2:
                                System.out.println("Ingrese el nombre de la editorial a eliminar");
                                String nombreEditorial_4 = leer.next().toUpperCase();
                                editorialServicio.EliminarPorNombre(nombreEditorial_4);
                                break;
                            case 3:
                                System.out.println("Ingrese el nombre del libro a eliminar");
                                Long ISBN = leer.nextLong();
                                libroServicio.EliminarPorISBN(ISBN);
                                break;
                        }
                        System.out.println("¿Desea eliminar otro autor, editorial o libro?");
                        System.out.println("S o N");
                        respuesta = leer.next();
                    } while (!respuesta.equalsIgnoreCase("N"));
                case 0:
                    break;
            }

        } while (opcionMenu1 != 0);

    }

}