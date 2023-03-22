package servicios;

import entidades.Editorial;
import entidades.Libro;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

public class EditorialServicio extends JPAController<Editorial> {

    public EditorialServicio() {
        super(Editorial.class);
    }

    public void crear(Editorial editorial) {
        super.create(editorial);
    }

    public void editar(Editorial editorial) {
        super.update(editorial);
    }

    public void borrar(Editorial editorial) {
        super.delete(editorial);
    }

    public void buscar(Editorial editorial) {
        super.find(editorial);
    }

    public List<Editorial> listarEditoriales(String query) {
        try {
            JPAController<Editorial> jpaController = new JPAController<>(Editorial.class);
            Query q = jpaController.em.createQuery("SELECT e FROM Editorial e");
            List<Editorial> editoriales = q.getResultList(); // Se corrigió el nombre de la variable
            jpaController.disconnect();
            if (editoriales.isEmpty()) {
                throw new Exception("No se encontró ninguna editorial.");
            }
            System.out.println("********************************************");
            System.out.println("*       Listado total de editoriales:      *");
            System.out.println("********************************************");
            for (Editorial editorial : editoriales) {
                System.out.println(editorial.toString());
            }
            return editoriales;
        } catch (Exception e) {
            System.err.println("Error en listar Editoriales: " + e.getMessage());
            return null;
        }
    }
       
    public Editorial agregarEditorialSiNoExiste(String editorialNombre) {
        super.connect();
        Editorial editorial = null;
        try {
            editorial = (Editorial) em.createQuery("SELECT a FROM Editorial a WHERE a.nombre = :nombre")
                    .setParameter("nombre", editorialNombre)
                    .getSingleResult();
            System.out.println("********************************************");
            System.out.println("* La editorial ya se encuentra registrada. *");
            System.out.println("********************************************");

        } catch (NoResultException e) {
            // Si no se encuentra ninguna editorial con el nombre dado, se crea una nueva editorial
            editorial = new Editorial();
            
            editorial.setNombre(editorialNombre);
            editorial.setAlta(true);
            // Se guarda la nueva editorial en la base de datos
            create(editorial);
            System.out.println("************************************************");
            System.out.println("* La editorial ha sido incorporada a la lista. *");
            System.out.println("************************************************");

        } finally {
            super.disconnect();
        }
        return editorial;
    }
    
    public void cambiarEstadoEditorialPorNombre(String nombreEditorial) {
    EntityManager em = null;
    try {
        em = emf.createEntityManager();
        Query query = em.createQuery("SELECT l FROM Editorial l WHERE l.nombre = :nombreEditorial");
        query.setParameter("nombreEditorial", nombreEditorial);
        Editorial editorial = (Editorial) query.getSingleResult();
        boolean estadoActual = editorial.getAlta();
        System.out.println("La editorial " + nombreEditorial + " tiene el estado " + (estadoActual ? "ALTA" : "BAJA"));
        System.out.println("¿Desea cambiar su estado? (s/n)");
        Scanner scanner = new Scanner(System.in);
        String respuesta = scanner.nextLine();
        if (respuesta.equalsIgnoreCase("s")) {
            editorial.setAlta(!estadoActual);
            editar(editorial);
            System.out.println("*********************************");
            System.out.println("* Estado cambiado exitosamente. *");
            System.out.println("*********************************");
            System.out.println("¿Desea cambiar el estado de todos los libros asociados a la editorial? (s/n)");
                Scanner scannerl = new Scanner(System.in);
                String respuesta1 = scannerl.nextLine();
                if (respuesta1.equalsIgnoreCase("s")) {
                    List<Libro> listaLibros = null;
                    LibroServicio libroServicio = new LibroServicio();
                    Query q = em.createQuery("SELECT l FROM Libro l WHERE l.editorial.nombre = :nombreEditorial");
                    q.setParameter("nombreEditorial", nombreEditorial);
                    listaLibros = q.getResultList();

                    for (Libro libro : listaLibros) {
                        libro.setAlta(editorial.getAlta());
                        libroServicio.editar(libro);
                    }
                    System.out.println("Los libros fueron dados de " + (!estadoActual ? "ALTA" : "BAJA"));
                }
            }
    } catch (NoResultException ex) {
        System.out.println("No se encontró ninguna editorial con el nombre " + nombreEditorial);
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
    
    public Editorial buscarPorNombre(String nombreEditorial) throws NoResultException {
    super.connect();
    Editorial editorial = null;
    try {
        editorial = (Editorial) em.createQuery("SELECT a FROM Editorial a WHERE a.nombre LIKE :nombreEditorial")
                     .setParameter("nombreEditorial", nombreEditorial)
                     .getSingleResult();
        System.out.println("La editorial " + editorial.getNombre() + "se encuentra registrado con la id N°" + editorial.getId());
    } catch (NoResultException e) {
        // Manejo de la excepción
        throw new NoResultException("No se ha encontrado la editorial: " + nombreEditorial);
    } finally {
        super.disconnect();
    }
    return editorial;
}
    
    public Editorial EliminarPorNombre(String nombreEditorial) throws NoResultException, NonUniqueResultException {
        super.connect();
        try {
            Editorial editorial = (Editorial) em.createQuery("SELECT a FROM Editorial a WHERE a.nombre = :nombreEditorial")
                    .setParameter("nombreEditorial", nombreEditorial)
                    .getSingleResult();
            System.out.println("El nombre ingresado corresponde a la editorial Nº" + editorial.getId());
            System.out.println("Tenga en cuenta lo siguiente:");
            System.out.println("Si la editorial seleccionada tiene libros asociados se eliminaran de la libreria");
            System.out.println("¿Desea eliminarla de todos modos? (s/n)");
            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                List<Libro> listaLibros = null;
                LibroServicio libroServicio = new LibroServicio();
                Query q = em.createQuery("SELECT l FROM Libro l WHERE l.editorial.nombre = :nombreEditorial");
                q.setParameter("nombreEditorial", nombreEditorial);
                listaLibros = q.getResultList();
            for (Libro libro : listaLibros) {
                libroServicio.borrar(libro);
            }
                borrar(editorial);
                
            System.out.println("Autor eliminado exitosamente");
        }
            super.disconnect();
            return editorial;
        } catch (NoResultException e) {
            super.disconnect();
            throw new NoResultException("No se encontró ninguna editorial con el nombre " + nombreEditorial);
        } catch (NonUniqueResultException e) {
            super.disconnect();
            throw new NonUniqueResultException("Se encontró más de una editorial con el nombre " + nombreEditorial);
        } catch (Exception e) {
            super.disconnect();
            throw new RuntimeException("Error al buscar al autor con el nombre " + nombreEditorial, e);
        }
    }

}
