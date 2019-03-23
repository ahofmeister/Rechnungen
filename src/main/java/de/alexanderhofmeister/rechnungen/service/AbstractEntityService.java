package de.alexanderhofmeister.rechnungen.service;

import de.alexanderhofmeister.rechnungen.model.BaseEntity;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import lombok.Getter;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class AbstractEntityService<E extends BaseEntity> {

    @Getter
    private final EntityManager em;

    AbstractEntityService() {
        this.em = PersistenceManager.INSTANCE.getEntityManager();
    }


    public void delete(final E entity) throws ConstraintViolationException {
        this.em.getTransaction().begin();
        this.em.remove(this.em.merge(entity));
        this.em.flush();
        this.em.getTransaction().commit();
        this.em.clear();
    }


    E findSingleWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {
        return findWithNamedQuery(namedQueryName, params).stream().findFirst().orElse(null);
    }


    private List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {

        final TypedQuery<E> query = this.em.createNamedQuery(namedQueryName, getEntityClass());

        for (final Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    private Class<E> getEntityClass() {
        return ClassUtil.getActualTypeBinding(getClass(), AbstractEntityService.class, 0);
    }

    public List<E> listAll(Integer first, Integer max) {
        final CriteriaBuilder cb = getEm().getCriteriaBuilder();
        Class<E> rootClass = getEntityClass();
        final CriteriaQuery<E> cq = cb
                .createQuery(rootClass);
        final Root<E> rootEntry = cq.from(rootClass);

        final CriteriaQuery<E> all = cq.select(rootEntry);
        final TypedQuery<E> allQuery = getEm().createQuery(all);
        if (first != null) {
            allQuery.setFirstResult(first);
        }
        if (max != null) {
            allQuery.setMaxResults(max);
        }
        return allQuery.getResultList();
    }

    public List<E> listAll() {
        return listAll(null, null);
    }


    public void update(final E entity) throws BusinessException {
        entity.validateFields();
        this.em.getTransaction().begin();

        if (entity.isNew()) {
            this.em.persist(entity);
            this.em.flush();
            this.em.refresh(entity);
        } else {
            this.em.merge(entity);
        }

        this.em.getTransaction().commit();
    }

    public int countAll() {
        final CriteriaBuilder cb = getEm().getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(getEntityClass())));
        final TypedQuery<Long> allQuery = getEm().createQuery(cq);
        return allQuery.getSingleResult().intValue();
    }

}
