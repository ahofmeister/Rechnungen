package de.alexanderhofmeister.rechnungen.service;

import de.alexanderhofmeister.rechnungen.model.BaseEntity;
import de.alexanderhofmeister.rechnungen.model.BusinessException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;


public class AbstractEntityService<E extends BaseEntity> {

    private final EntityManager entityManager;

    AbstractEntityService() {
        this.entityManager = PersistenceManager.INSTANCE.getEntityManager();
    }


    public List<E> listAll() {
        Class<E> rootClass = getEntityClass();
        final CriteriaQuery<E> cq = entityManager.getCriteriaBuilder().createQuery(rootClass);
        return entityManager.createQuery(cq.select(cq.from(rootClass))).getResultList();
    }


    public void delete(final E entity) {
        this.entityManager.getTransaction().begin();
        this.entityManager.remove(this.entityManager.merge(entity));
        this.entityManager.flush();
        this.entityManager.getTransaction().commit();
        this.entityManager.clear();
    }


    E findSingleWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {
        return findWithNamedQuery(namedQueryName, params).stream().findFirst().orElse(null);
    }


    private List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {
        return findWithNamedQuery(namedQueryName, params, null, null);
    }

    private Class<E> getEntityClass() {
        return ClassUtil.getActualTypeBinding(getClass(), AbstractEntityService.class, 0);
    }

    public List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params, final Integer firstRow,
                                      final Integer maxRow) {
        TypedQuery<E> query = this.entityManager.createNamedQuery(namedQueryName, getEntityClass());

        addParams(params, query);
        if (firstRow != null) {
            query.setFirstResult(firstRow);
        }

        if (maxRow != null) {
            query.setMaxResults(maxRow);
        }

        return query.getResultList();
    }


    public Long findCountWithNamedQuery(final String namedQueryName, final Map<String, Object> params) {

        final TypedQuery<Long> query = this.entityManager.createNamedQuery(namedQueryName, Long.class);
        addParams(params, query);

        return query.getSingleResult();
    }

    private void addParams(final Map<String, Object> params, final TypedQuery<?> query) {
        params.forEach(query::setParameter);
    }

    public void update(final E entity) throws BusinessException {
        entity.validateFields();
        this.entityManager.getTransaction().begin();

        if (entity.isNew()) {
            this.entityManager.persist(entity);
            this.entityManager.flush();
            this.entityManager.refresh(entity);
        } else {
            this.entityManager.merge(entity);
        }

        this.entityManager.getTransaction().commit();
    }

}
