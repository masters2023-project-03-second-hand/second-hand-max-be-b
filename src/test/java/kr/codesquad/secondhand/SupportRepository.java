package kr.codesquad.secondhand;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("test")
@Component
@Transactional
public class SupportRepository {

    @Autowired
    private EntityManager em;

    public <T> T save(T entity) {
        em.persist(entity);
        em.flush();
        em.clear();
        return entity;
    }
}
