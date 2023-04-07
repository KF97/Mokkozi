package com.b303.mokkozi.user;

import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.user.dto.UserFollowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl {

    @Autowired
    private EntityManager em;

    @Transactional
    public List<User> getRandomUser(Long userId){
        int maxResults = 5;
        String str = "SELECT u FROM User u WHERE u.id is not "+ userId +" AND u.active LIKE '활동' ORDER BY RAND()";
        return Optional.ofNullable(em.createQuery(str).setMaxResults(maxResults)
                .getResultList()).orElse(null);
    }

    @Transactional
    public List<User> getRandomUserNotLogin(){
        int maxResults = 5;
        String str = "SELECT u FROM User u " +
                "WHERE u.active LIKE '활동' " +
                "ORDER BY RAND()";
        return Optional.ofNullable(em.createQuery(str).setMaxResults(maxResults)
                .getResultList()).orElse(null);
    }

    public List<User> getLocationUser(Long userId, String addr) {
        int maxResults = 5;
        String str = "SELECT u FROM User u WHERE u.id is not "+ userId +" AND u.address LIKE '%"+addr+"%' ORDER BY RAND()";
        return Optional.ofNullable(em.createQuery(str).setMaxResults(maxResults)
                .getResultList()).orElse(null);

    }
}
