package servicios;

import entidades.Libro;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class LibroServicio extends JPAController<Libro> {
    
    Scanner leer = new Scanner(System.in).useDelimiter("\n");
    
    public LibroServicio() {
        super(Libro.class);
    }

    public void crear(Libro libro) {
        super.create(libro);
    }

    public void editar(Libro libro) {
        super.update(libro);
    }

    public void borrar(Libro libro) {
        super.delete(libro);
    }

    public void buscar(Libro libro) {
        super.find(libro);
    }

    public List<Libro> listarLibros(String query) {
        try {
            JPAController<Libro> jpaController = new JPAController<>(Libro.class);
            Query q = jpaController.em.createQuery("SELECT l FROM Libro l");
            List<Libro> libros = q.getResultList(); // Se corrigió el nombre de la variable
            jpaController.disconnect();
            if (libros.isEmpty()) {
                throw new Exception("No se encontró ningún libro.");
            }
            System.out.println("Listado total de libros: ");
            for (Libro libro : libros) {
                System.out.println(libro.toString());
            }
            return libros;
        } catch (Exception e) {
            System.err.println("Error en listarLibros: " + e.getMessage());
            return null;
        }
    }

    public Libro buscarPorISBN(Long isbn) throws NoResultException, NonUniqueResultException {
        super.connect();
        try {
            Libro libro = (Libro) em.createQuery("SELECT l FROM Libro l WHERE l.isbn = :isbn")
                    .setParameter("isbn", isbn)
                    .getSingleResult();
            boolean estadoActual = libro.getAlta();
            System.out.println("El isbn ingresado corresponde al libro " + libro.getTitulo() + " y tiene el estado: " + (estadoActual ? "ALTA" : "BAJA"));
            super.disconnect();
            return libro;
        } catch (NoResultException e) {
            super.disconnect();
            throw new NoResultException("No se encontró ningún libro con el ISBN " + isbn);
        } catch (NonUniqueResultException e) {
            super.disconnect();
            throw new NonUniqueResultException("Se encontró más de un libro con el ISBN " + isbn);
        } catch (Exception e) {
            super.disconnect();
            throw new RuntimeException("Error al buscar el libro con el ISBN " + isbn, e);
        }
    }

    public List<Libro> buscarPorTitulo(String titulo) {
    try {
        JPAController<Libro> jpaController = new JPAController<>(Libro.class);
        TypedQuery<Libro> query = jpaController.em.createQuery("SELECT l FROM Libro l WHERE l.titulo LIKE :titulo", Libro.class);
        query.setParameter("titulo", "%" + titulo + "%");
        List<Libro> libros = query.getResultList();
        jpaController.disconnect();
        if (libros.isEmpty()) {
            throw new Exception("No se encontró ningún libro con el título proporcionado.");
        }
        System.out.println("Libros encontrados: ");
        for (Libro libro : libros) {
            System.out.println(libro.toString());
        }
        return libros;
    } catch (Exception e) {
        System.err.println("Error en buscarPorTitulo: " + e.getMessage());
        return null;
    }
}

    public List<Libro> buscarLibrosPorAutor(String nombreAutor) {
    super.connect();
    List<Libro> listaLibros = null;

    try {
        Query q = em.createQuery("SELECT l FROM Libro l WHERE l.autor.nombre = :nombreAutor");
        q.setParameter("nombreAutor", nombreAutor);
        listaLibros = q.getResultList();
        System.out.println("Libros encontrados: ");
        for (Libro libro : listaLibros) {
            System.out.println(libro.toString());
        }
    } catch (Exception e) {
        System.out.println("Error al buscar libros por autor: " + e.getMessage());
    } finally {
        em.close();
    }
    super.disconnect();
    return listaLibros;
}

    public List<Libro> buscarPorEditorial(String nombreEditorial) throws EditorialNotFoundException {
        super.connect();
    List<Libro> listaLibros = null;

    try {
        Query q = em.createQuery("SELECT l FROM Libro l WHERE l.editorial.nombre = :nombreEditorial");
        q.setParameter("nombreEditorial", nombreEditorial);
        listaLibros = q.getResultList();
        System.out.println("Libros encontrados: ");
        for (Libro libro : listaLibros) {
            System.out.println(libro.toString());
        }
    } catch (Exception e) {
        System.out.println("Error al buscar libros por Editorial: " + e.getMessage());
    } finally {
        em.close();
    }
    super.disconnect();
    return listaLibros;
    }

    public void creacionLibro (Libro libro) throws Exception {
        
        AutorServicio autorServicio = new AutorServicio();
        EditorialServicio editorialServicio = new EditorialServicio();
        System.out.println("Ingrese los datos del libro a incorporar a la libreria");

                  
        Long ISBN = null;
        do {
            System.out.println("Ingrese el ISBN");
            String entrada = leer.nextLine();
            if (!entrada.isEmpty()) {
                try {
                    ISBN = Long.parseLong(entrada);
                } catch (NumberFormatException e) {
                    System.out.println("El valor ingresado no es un número válido.");
                }
            } else {
                System.out.println("El campo no puede estar vacío. Intente nuevamente.");
            }
        } while (ISBN == null);

        libro.setIsbn(ISBN);

        // Validar que el ISBN no exista en la base de datos
        Libro libroExistente = em.find(Libro.class, libro.getIsbn());
        if (libroExistente != null) {
            throw new Exception("El ISBN ingresado ya existe en la base de datos.");
        }
          
        //Aqui Ingresamos el titulo y agregamos un "do while" para que no pueda dejar en blanco 
        String tituloLibro = null;
        do {
            System.out.println("Ingrese el Título");
            tituloLibro = leer.nextLine().trim();
            if (tituloLibro.isEmpty()) {
                System.out.println("El título es un campo obligatorio. Intente nuevamente.");
            }
        } while (tituloLibro.isEmpty());

        libro.setTitulo(tituloLibro);

        //Aqui Ingresamos el año y agregamos un "do while" para que no pueda dejar en blanco 
        Integer anio = null;
        do {
            System.out.println("Ingrese el año de publicación (Debe ser un valor entre 0 y el año actual)");
            String entrada = leer.nextLine();
            if (!entrada.isEmpty()) {
                try {
                    anio = Integer.parseInt(entrada);
                    if (anio < 0 || anio > LocalDate.now().getYear()) {
                        System.out.println("El valor ingresado debe ser un año entre 0 y el año actual.");
                        anio = null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("El valor ingresado no es un número válido.");
                }
            } else {
                System.out.println("El campo no puede estar vacío. Intente nuevamente.");
            }
        } while (anio == null);

        libro.setAnio(anio);
        
        //Aqui Ingresamos la cantidad de ejemplares y agregamos un "do while" para que no pueda dejar en blanco 
        Integer ejemplares = null;
        do {
            System.out.println("Ingrese la cantidad de ejemplares (No puede ser menor a 1)");
                    String entrada = leer.nextLine();
                    if (!entrada.isEmpty()) {
                         try {
                             ejemplares = Integer.parseInt(entrada);
                             if (ejemplares < 1) {
                        System.out.println("El valor ingresado debe ser mayor que 0.");
                        ejemplares = null;
                            }
                         } catch (NumberFormatException e) {
                    System.out.println("El valor ingresado no es un número válido.");
                }
               } else {
                System.out.println("El campo no puede estar vacío. Intente nuevamente.");
            }
        } while (ejemplares == null);
        
        libro.setEjemplares(ejemplares);
        
        libro.setEjemplaresRestantes(libro.getEjemplares());
        
        libro.setEjemplaresPrestados(0);

        //Para incorporarlo al libro debemos darlo de alta en la libreria
        libro.setAlta(true);
        
        //Ahora se incorpora el autor de libro
        
        String autorNombre = null;
        do {
            System.out.println("Ingrese el nombre completo del autor");
            autorNombre = leer.nextLine().trim();
            if (autorNombre.isEmpty()) {
                System.out.println("El autor es un campo obligatorio. Intente nuevamente.");
            }
        } while (autorNombre.isEmpty());
        

        libro.setAutor(autorServicio.agregarAutorSiNoExiste(autorNombre));

        
        //Por ultimo incorporamos la editorial
        
        
        String editorialNombre = null;
        
        do {
            System.out.println("Ingrese el nombre de la editorial");
            editorialNombre = leer.nextLine().trim();
            if (editorialNombre.isEmpty()) {
                System.out.println("La editorial es un campo obligatorio. Intente nuevamente.");
            }
        } while (editorialNombre.isEmpty());
                 
        
        libro.setEditorial(editorialServicio.agregarEditorialSiNoExiste(editorialNombre));
                
        
        System.out.println("El libro '" + libro.getTitulo() + "' ha sigo incluido en la lista");
        
        crear(libro);
//        em.getTransaction().begin();
//        em.persist(libro);
//        em.getTransaction().commit();
    }

    public void cambiarEstadoLibroPorISBN(Long isbn) {
    EntityManager em = null;
    try {
        em = emf.createEntityManager();
        Query query = em.createQuery("SELECT l FROM Libro l WHERE l.isbn = :isbn");
        query.setParameter("isbn", isbn);
        Libro libro = (Libro) query.getSingleResult();
        boolean estadoActual = libro.getAlta();
        System.out.println("El isbn " + isbn + " que pertenece al libro " + libro.getTitulo() + " tiene el estado " + (estadoActual ? "ALTA" : "BAJA"));
        System.out.println("¿Desea cambiar su estado? (s/n)");
        Scanner scanner = new Scanner(System.in);
        String respuesta = scanner.nextLine();
        if (respuesta.equalsIgnoreCase("s")) {
            libro.setAlta(!estadoActual);
            editar(libro);
            System.out.println("Estado cambiado exitosamente");
        }
    } catch (NoResultException ex) {
        System.out.println("No se encontró ningún libro con el isbn " + isbn);
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
    
    public Libro EliminarPorISBN(Long isbn) throws NoResultException, NonUniqueResultException {
        super.connect();
        try {
            Libro libro = (Libro) em.createQuery("SELECT l FROM Libro l WHERE l.isbn = :isbn")
                    .setParameter("isbn", isbn)
                    .getSingleResult();
            System.out.println("El isbn ingresado corresponde al libro " + libro.getTitulo());
            System.out.println("¿Desea eliminarlo? (s/n)");
            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                borrar(libro);
            System.out.println("Libro eliminado exitosamente");
        }
            super.disconnect();
            return libro;
        } catch (NoResultException e) {
            super.disconnect();
            throw new NoResultException("No se encontró ningún libro con el ISBN " + isbn);
        } catch (NonUniqueResultException e) {
            super.disconnect();
            throw new NonUniqueResultException("Se encontró más de un libro con el ISBN " + isbn);
        } catch (Exception e) {
            super.disconnect();
            throw new RuntimeException("Error al buscar el libro con el ISBN " + isbn, e);
        }
    }
    
    public List<Libro> eliminarPorAutor(String nombreAutor) {
        List<Libro> listaLibros = null;

        try {
            Query q = em.createQuery("SELECT l FROM Libro l WHERE l.autor.nombre = :nombreAutor");
            q.setParameter("nombreAutor", nombreAutor);
            listaLibros = q.getResultList();
            System.out.println("Libros eliminados.");
            for (Libro libro : listaLibros) {
                borrar(libro);
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar libros por autor: " + e.getMessage());
        } finally {
            em.close();
        }
        return listaLibros;
    }
    
}