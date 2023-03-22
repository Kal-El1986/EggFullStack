
package servicios;

import entidades.Autor;
import entidades.Libro;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import servicios.LibroServicio;

public class AutorServicio extends JPAController<Autor> {

    public AutorServicio() {
        super(Autor.class);
    }

    public void crear(Autor autor) {
        super.create(autor);
    }

    public void editar(Autor autor) {
        super.update(autor);
    }

    public void borrar(Autor autor) {
        super.delete(autor);
    }

    public void buscar(Autor autor) {
        super.find(autor);
    }

    
    
    //8) Búsqueda de un Autor por nombre. 
public Autor buscarPorNombre(String nombreAutor) throws NoResultException {
    super.connect();
    Autor autor = null;
    try {
        autor = (Autor) em.createQuery("SELECT a FROM Autor a WHERE a.nombre LIKE :nombreAutor")
                     .setParameter("nombreAutor", nombreAutor)
                     .getSingleResult();
        System.out.println("El autor " + autor.getNombre() + "se encuentra registrado con la id N°" + autor.getId());
    } catch (NoResultException e) {
        // Manejo de la excepción
        throw new NoResultException("No se ha encontrado al autor: " + nombreAutor);
    } finally {
        super.disconnect();
    }
    return autor;
}

public Autor agregarAutorSiNoExiste(String autorNombre) {
    super.connect();
    Autor autor = null;
    try {
        autor = (Autor) em.createQuery("SELECT a FROM Autor a WHERE a.nombre = :nombre")
                .setParameter("nombre", autorNombre)
                .getSingleResult();
        System.out.println("****************************************");
        System.out.println("* El autor ya se encuentra registrado. *");
        System.out.println("****************************************");
    } catch (NoResultException e) {
        // Si no se encuentra ningún autor con el nombre dado, se crea un nuevo autor
        autor = new Autor();
        autor.setNombre(autorNombre);
        autor.setAlta(true);
        // Se guarda el nuevo autor en la base de datos
        create(autor);
        System.out.println("********************************************");
        System.out.println("* El autor ha sido incorporado a la lista. *");
        System.out.println("********************************************");
    } finally {
        super.disconnect();
    }
    return autor;
}

    public void cambiarEstadoAutorPorNombre(String nombreAutor) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Query query = em.createQuery("SELECT l FROM Autor l WHERE l.nombre = :nombre");
            query.setParameter("nombre", nombreAutor);
            Autor autor = (Autor) query.getSingleResult();
            boolean estadoActual = autor.getAlta();
            System.out.println("El autor " + nombreAutor + " tiene el estado " + (estadoActual ? "ALTA" : "BAJA"));
            System.out.println("¿Desea cambiar su estado? (s/n)");
            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                autor.setAlta(!estadoActual);
                editar(autor);
                System.out.println("*********************************");
                System.out.println("* Estado cambiado exitosamente. *");
                System.out.println("*********************************");
                System.out.println("¿Desea cambiar el estado de todos los libros asociados al autor? (s/n)");
                Scanner scannerl = new Scanner(System.in);
                String respuesta1 = scannerl.nextLine();
                if (respuesta1.equalsIgnoreCase("s")) {
                    List<Libro> listaLibros = null;
                    LibroServicio libroServicio = new LibroServicio();
                    Query q = em.createQuery("SELECT l FROM Libro l WHERE l.autor.nombre = :nombreAutor");
                    q.setParameter("nombreAutor", nombreAutor);
                    listaLibros = q.getResultList();

                    for (Libro libro : listaLibros) {
                        libro.setAlta(autor.getAlta());
                        libroServicio.editar(libro);
                    }
                    System.out.println("Los libros fueron dados de " + (!estadoActual ? "ALTA" : "BAJA"));
                }
            }
        } catch (NoResultException ex) {
            System.out.println("No se encontró ningún autor con el nombre " + nombreAutor);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

public List<Autor> listarAutores(String query) {
        try {
            JPAController<Autor> jpaController = new JPAController<>(Autor.class);
            Query q = jpaController.em.createQuery("SELECT a FROM Autor a");
            List<Autor> autores = q.getResultList(); // Se corrigió el nombre de la variable
            jpaController.disconnect();
            if (autores.isEmpty()) {
                throw new Exception("No se encontró ningún autor.");
            }
            System.out.println("*********************************");
            System.out.println("*   Listado total de autores:   *");
            System.out.println("*********************************");
            for (Autor autor : autores) {
                System.out.println(autor.toString());
            }
            return autores;
        } catch (Exception e) {
            System.err.println("Error en listar Autores: " + e.getMessage());
            return null;
        }
    }

public Autor EliminarPorNombre(String nombreAutor) throws NoResultException, NonUniqueResultException {
        super.connect();
        try {
            Autor autor = (Autor) em.createQuery("SELECT a FROM Autor a WHERE a.nombre = :nombreAutor")
                    .setParameter("nombreAutor", nombreAutor)
                    .getSingleResult();
            System.out.println("El nombre ingresado corresponde al autor Nº" + autor.getId());
            System.out.println("Tenga en cuenta lo siguiente:");
            System.out.println("Si el autor seleccionado tiene libros asociados se eliminaran de la libreria");
            System.out.println("¿Desea eliminarlo de todos modos? (s/n)");
            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                List<Libro> listaLibros = null;
                LibroServicio libroServicio = new LibroServicio();
                Query q = em.createQuery("SELECT l FROM Libro l WHERE l.autor.nombre = :nombreAutor");
                q.setParameter("nombreAutor", nombreAutor);
                listaLibros = q.getResultList();
                
            for (Libro libro : listaLibros) {
                libroServicio.borrar(libro);
            }
            System.out.println("Libros eliminados.");
                borrar(autor);
                
            System.out.println("Autor eliminado exitosamente");
        }
            super.disconnect();
            return autor;
        } catch (NoResultException e) {
            super.disconnect();
            throw new NoResultException("No se encontró ningún autor con el nombre " + nombreAutor);
        } catch (NonUniqueResultException e) {
            super.disconnect();
            throw new NonUniqueResultException("Se encontró más de un autor con el nombre " + nombreAutor);
        } catch (Exception e) {
            super.disconnect();
            throw new RuntimeException("Error al buscar al autor con el nombre " + nombreAutor, e);
        }
    }

}