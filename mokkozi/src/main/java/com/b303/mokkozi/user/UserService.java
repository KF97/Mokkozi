package com.b303.mokkozi.user;

import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.entity.UserInterest;
import com.b303.mokkozi.user.dto.UserFollowDto;
import com.b303.mokkozi.user.dto.UserInterestDto;
import com.b303.mokkozi.user.request.JoinInfoPostReq;
import com.b303.mokkozi.user.request.UserActivePatchReq;
import org.springframework.data.domain.Page;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    User userUpdate(User user);

    void createFollow(User fromUser, String toUserEmail);

    void deleteFollow(Long Id);

    List<UserFollowDto> getFollowers(User user);

    List<UserFollowDto> getFollowing(User user);

    List<User> getRandomUser(User user);

    User join(JoinInfoPostReq info);

    Optional<User> findById(Long id);

    Page<User> getUserList(User user, int page);

    void modifyUserActive(UserActivePatchReq vupr);

    List<User> getRandomUserNotLogin();

    List<UserInterestDto> getUserInterest(User user);

    List<User>  getLocationUser(User user);
}
