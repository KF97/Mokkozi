package com.b303.mokkozi.user;

//import com.b303.mokkozi.entity.QUserFollow;
import com.b303.mokkozi.entity.UserFollow;
import com.b303.mokkozi.user.dto.UserFollowDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserFollowRepositoryImpl {

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
//    QUserFollow qUserFollow = QUserFollow.userFollow;

    private UserFollowDto toDto(UserFollow uf){

        return null;
    }

    public List<UserFollowDto> getEachFollow(Long id) {

//        String str = "SELECT uf.id, uf.fromUser.id as userId, uf.fromUser.nickname, uf.fromUser.profile as profileUrl FROM UserFollow uf" +
        String str = "SELECT uf FROM UserFollow uf" +
                " WHERE uf.fromUser.id in (SELECT u.toUser.id FROM UserFollow u WHERE u.fromUser.id is "+ id +" )" +
                " AND uf.toUser.id is "+id;

//        List<UserFollowDto> list = new ArrayList<>();
//                Optional.ofNullable(em.createQuery(str)
//                .getResultList()).stream().forEach(m-> ).orElse(null);
//
//        return list;


        return null;
    }

}
