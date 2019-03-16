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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class AbstractEntityService<E extends BaseEntity> {

    @Getter
    private final EntityManager em;

    public AbstractEntityService() {
        this.em = PersistenceManager.INSTANCE.getEntityManager();
    }

    private TypedQuery<E> addParams(final Map<String, Object> params, final TypedQuery<E> query) {
        for (final Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    private TypedQuery<Double> addParamsForDouble(final Map<String, Object> params, final TypedQuery<Double> query) {
        for (final Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    public void delete(final Collection<E> entities) {
        entities.stream().forEach(this::delete);
    }

    public void delete(final E entity) throws ConstraintViolationException {
        this.em.getTransaction().begin();
        this.em.remove(this.em.merge(entity));
        this.em.flush();
        this.em.getTransaction().commit();
        this.em.clear();
    }

    public E find(final E entity) {
        return find(entity.getId());
    }

    public E find(final Long id) {
        return this.em.find(getEntityClass(), id);
    }

    public Long findCountWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {

        final TypedQuery<Long> query = this.em.createNamedQuery(namedQueryName, Long.class);

        for (final Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getSingleResult();
    }

    public Double findCountWithNamedQuery(final String namedQueryName, final Map<String, Object> params,
                                          final int firstRow, final int maxRow) {

        TypedQuery<Double> query = this.em.createNamedQuery(namedQueryName, Double.class);

        query = addParamsForDouble(params, query);
        query.setFirstResult(firstRow).setMaxResults(maxRow).getResultList();
        query.setMaxResults(maxRow);

        return query.getSingleResult();
    }

    public E findSingleWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {
        return findWithNamedQuery(namedQueryName, params).stream().findFirst().orElse(null);
    }

    public List<E> findWithNamedQuery(final String namedQueryName) {
        return this.em.createNamedQuery(namedQueryName, getEntityClass()).getResultList();
    }

    public List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {

        final TypedQuery<E> query = this.em.createNamedQuery(namedQueryName, getEntityClass());

        for (final Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    public List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params, final int firstRow,
                                      final int maxRow) {

        TypedQuery<E> query = this.em.createNamedQuery(namedQueryName, getEntityClass());

        query = addParams(params, query);
        query.setFirstResult(firstRow).setMaxResults(maxRow).getResultList();
        query.setMaxResults(maxRow);

        return query.getResultList();
    }

    public List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params, final int first,
                                      final int pagesize, final String sortfield, final Map<String, Object> filters) {

        TypedQuery<E> query = this.em.createNamedQuery(namedQueryName, getEntityClass());
        query = addParams(params, query);
        return query.setFirstResult(first).setMaxResults(pagesize).getResultList();

    }

    protected Class<E> getEntityClass() {
        return ClassUtil.getActualTypeBinding(getClass(), AbstractEntityService.class, 0);
    }

    public List<E> listAll() {
        final CriteriaBuilder cb = getEm().getCriteriaBuilder();
        final CriteriaQuery<E> cq = cb
                .createQuery(ClassUtil.getActualTypeBinding(getClass(), AbstractEntityService.class, 0));
        final Root<E> rootEntry = cq.from(ClassUtil.getActualTypeBinding(getClass(), AbstractEntityService.class, 0));
        final CriteriaQuery<E> all = cq.select(rootEntry);
        final TypedQuery<E> allQuery = getEm().createQuery(all);
        return allQuery.getResultList();
    }

    public E update(final E entity) throws BusinessException {
        E result;
        entity.validateFields();
        this.em.getTransaction().begin();

        if (entity.isNew()) {
            this.em.persist(entity);
            this.em.flush();
            this.em.refresh(entity);
            result = entity;
        } else {
            result = this.em.merge(entity);
        }

        this.em.getTransaction().commit();

        return result;
    }

}
