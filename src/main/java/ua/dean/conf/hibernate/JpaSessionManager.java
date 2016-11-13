package ua.dean.conf.hibernate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Component
public class JpaSessionManager {

	// session management block
	private static int openedSessions = 0;
	private static int closedSessions = 0;
	private static ThreadLocal<EntityManager> threadLocalSession = new ThreadLocal<EntityManager>();

	@Value("classpath:hibernate.properties")
	private Properties jpaProperties;

	private EntityManagerFactory entityManagerFactory;

	public JpaSessionManager() {
		LocalContainerEntityManagerFactoryBean localContainerBean = new LocalContainerEntityManagerFactoryBean();
		localContainerBean.setPersistenceXmlLocation("classpath*:persistance.xml");
		localContainerBean.setPackagesToScan("ua.dean.domain");
		localContainerBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		localContainerBean.setJpaProperties(jpaProperties);
		localContainerBean.setJpaDialect(new HibernateJpaDialect());
		localContainerBean.afterPropertiesSet();
		entityManagerFactory = localContainerBean.getObject();
		if (entityManagerFactory == null) {
			System.err.println("Entity manager factory is null");
		}
	}

	protected void setEntityManager(EntityManager entityManager) {
		threadLocalSession.set(entityManager);
		openedSessions++;
	}

	public EntityManager getEntityManager() {
		if (threadLocalSession.get() == null) {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			setEntityManager(entityManager);
		}
		return threadLocalSession.get();
	}

	public void closeEntityManager(EntityManager entityManager) {
		entityManager.close();
	}

	public void closeEntityManager() {
		if (threadLocalSession.get() != null) {
			EntityManager entityManager = threadLocalSession.get();
			entityManager.close();
			System.out.println("Close session");
			closedSessions++;
		}
		threadLocalSession.remove();
	}

	public static int getOpenedSessions() {
		return openedSessions;
	}

	public static int getClosedSessions() {
		return closedSessions;
	}
}
