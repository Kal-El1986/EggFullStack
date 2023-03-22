package servicios;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAController<T> {
    protected EntityManagerFactory emf;
    protected EntityManager em;
    private Class<T> entityClass;

    public JPAController(Class<T> entityClass) {
        this.entityClass = entityClass;
        emf = Persistence.createEntityManagerFactory("LibreriaJPAPU");
        em = emf.createEntityManager();
    }

    protected void connect() {
        if (!em.isOpen()) em = emf.createEntityManager();
    }

    protected void disconnect() {
        if (em.isOpen()) em.close();
    }

    protected void create(T object) {
        connect();
        em.getTransaction().begin();
        em.persist(object);
        em.getTransaction().commit();
        disconnect();
    }

    protected void update(T object) {
        connect();
        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
        disconnect();
    }

    protected void delete(T object) {
        connect();
        em.getTransaction().begin();
        em.remove(em.merge(object));
        em.getTransaction().commit();
        disconnect();
    }

    protected T find(Object id) {
        connect();
        T object = em.find(entityClass, id);
        disconnect();
        return object;
    }

}
