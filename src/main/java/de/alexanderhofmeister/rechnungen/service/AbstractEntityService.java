package de.alexanderhofmeister.rechnungen.service;

import de.alexanderhofmeister.rechnungen.model.BaseEntity;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import lombok.Getter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;


public class AbstractEntityService<E extends BaseEntity> {

    @Getter
    private final EntityManager em;

    AbstractEntityService() {
        this.em = PersistenceManager.INSTANCE.getEntityManager();
    }


    public List<E> listAll() {
        Class<E> rootClass = getEntityClass();
        final CriteriaQuery<E> cq = getEm().getCriteriaBuilder().createQuery(rootClass);
        return getEm().createQuery(cq.select(cq.from(rootClass))).getResultList();
    }


    public void delete(final E entity) {
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
        return findWithNamedQuery(namedQueryName, params, null, null);
    }

    private Class<E> getEntityClass() {
        return ClassUtil.getActualTypeBinding(getClass(), AbstractEntityService.class, 0);
    }

    public List<E> findWithNamedQuery(final String namedQueryName, final Map<String, Object> params, final Integer firstRow,
                                      final Integer maxRow) {
        TypedQuery<E> query = this.em.createNamedQuery(namedQueryName, getEntityClass());

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

        final TypedQuery<Long> query = this.em.createNamedQuery(namedQueryName, Long.class);
        addParams(params, query);

        return query.getSingleResult();
    }

    private void addParams(final Map<String, Object> params, final TypedQuery<?> query) {
        params.forEach(query::setParameter);
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

}
