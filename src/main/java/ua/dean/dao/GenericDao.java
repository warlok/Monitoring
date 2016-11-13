package ua.dean.dao;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import ua.dean.conf.hibernate.JpaSessionManager;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class GenericDao<T, ID extends Serializable> {


	public static int executionTime;

	private Class<T> persistentClass;

	@Autowired
	private JpaSessionManager sessionManager;

	protected EntityManager getEntityManager() {
		return sessionManager.getEntityManager();
	}

	/**
	 * Creates a cached query
	 * 
	 * @param query
	 * @return
	 */
	protected TypedQuery<T> createCacheableQuery(CriteriaQuery<T> query) {
		return getEntityManager().createQuery(query).setHint("org.hibernate.cacheable", true);
	}

	/**
	 * Creates a not cached query
	 * 
	 * @param query
	 * @return
	 */
	protected TypedQuery<T> createQuery(CriteriaQuery<T> query) {
		return getEntityManager().createQuery(query);
	}

	/**
	 * Gets single result from the query
	 * 
	 * @param query
	 * @return
	 */
	protected T getSingleResultFromQuery(CriteriaQuery<T> query) {
		try {
			return createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Gets single result from the cacheable query
	 * 
	 * @param query
	 * @return
	 */
	protected T getSingleResultFromCacheableQuery(CriteriaQuery<T> query) {
		try {
			return createCacheableQuery(query).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Gets a result from the query
	 * 
	 * @param query
	 * @return
	 */
	protected List<T> getResultFromQuery(CriteriaQuery<T> query) {
		return createQuery(query).getResultList();
	}

	private void commitTransaction(EntityManager entityManager) {

		if (entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().commit();
		}
	}

	private void beginTransaction(EntityManager entityManager) {

		if (entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
			// do not open nested transaction
		} else {
			entityManager.getTransaction().begin();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GenericDao() {
		Class clazz = getClass();
		boolean converted = false;
		ParameterizedType pt = null;

		while (!converted) {
			try {
				pt = ((ParameterizedType) clazz.getGenericSuperclass());
				converted = true;
			} catch (ClassCastException e) {
				clazz = clazz.getSuperclass();
			}
		}
		this.persistentClass = (Class<T>) pt.getActualTypeArguments()[0];
	}

	public <T> T deproxy(T maybeProxy) {
		if (maybeProxy instanceof HibernateProxy) {
			return (T) ((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation();
		}
		return maybeProxy;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	/**
	 * Evicts object from session with cascaded eviction of children.
	 *
	 * @param object
	 */
	public void evictFromCache(T object) {
		getEntityManager().getEntityManagerFactory().getCache().evict(getPersistentClass(), object);
	}

	public T load(ID id) {
		T entity;
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			entity = session.find(getPersistentClass(), id);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T get(ID id) {
		T entity;
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			entity = session.find(getPersistentClass(), id);
			commitTransaction(session);
		} finally {

		}
		return entity;
	}

	public T saveOrUpdate(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.persist(entity);
			commitTransaction(session);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			;
		}
		return entity;
	}

	public T save(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.persist(entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T persist(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.persist(entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T refresh(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.refresh(entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T update(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.persist(entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T merge(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.merge(entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public T lock(T entity) {
		EntityManager session = getEntityManager();
		try {
			session.lock(entity, LockModeType.NONE);
		} finally {
			;
		}
		return entity;
	}

	public T delete(T entity) {
		EntityManager session = getEntityManager();
		beginTransaction(session);
		try {
			session.remove(entity);
			commitTransaction(session);
		} finally {
			;
		}
		return entity;
	}

	public Long getCount() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		// create the query with result of type long
		CriteriaQuery query = builder.createQuery(getPersistentClass());
		query.from(getPersistentClass());
		Root<T> entityRoot = (Root<T>) query.getRoots().toArray()[0];
		// now add the count projection
		query.select(builder.count(entityRoot));
		return (Long) getSingleResultFromQuery(query);

	}

	public List<T> findAll() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(getPersistentClass());
		query.from(getPersistentClass());
		return getResultFromQuery(query);
	}

}