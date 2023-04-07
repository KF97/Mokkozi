package com.b303.mokkozi.user;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.entity.UserFollow;
import com.b303.mokkozi.entity.UserInterest;
import com.b303.mokkozi.user.dto.UserFollowDto;
import com.b303.mokkozi.user.dto.UserInterestDto;
import com.b303.mokkozi.user.request.JoinInfoPostReq;
import com.b303.mokkozi.user.request.UserActivePatchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserInterestRepository userInterestRepository;

    @Autowired
    UserFollowRepository userFollowRepository;

    @Autowired
    UserRepositoryImpl userRepositoryImpl;

//    @Autowired
//    UserFollowRepositoryImpl ufRepositoryImpl;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public User userUpdate(User user) {
        return userRepository.save(user);
    }

    public User join(JoinInfoPostReq info) {
        // 1. DB에 저장하기 위한 엔티티 객체 생성
        User user = new User();
        user.setEmail(info.getEmail());
        user.setNickname(info.getNickname());
        user.setPassword(info.getPassword());
        user.setAddress(info.getAddress() + " " + info.getExtAddress());
        user.setGender(info.getGender());
        user.setBirth(info.getBirth());
        user.setRole(info.getRole());
        user.setActive("대기");
        user.setPenaltyCount(0L);

        // 2. DB에 저장
        User result = userRepository.save(user);

        // 3. 관심사 저장하기.
        for (String hobby:info.getHobby()) {
            UserInterest userInterest = new UserInterest();

            userInterest.setUser(user);
            userInterest.setInterest(hobby);

            userInterestRepository.save(userInterest);
        }


        return result;
    }


    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Page<User> getUserList(User user, int pageIdx) {

        int size = 10;
        int page = pageIdx <= 0 ? 0 : pageIdx - 1;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<User> list = userRepository.findAll(pageable);

        return list;
    }

    @Override
    public void modifyUserActive(UserActivePatchReq vupr) {
        User user = userRepository.findById(vupr.getUserId()).orElseThrow(() -> new NoSuchElementException("not found"));
        user.setActive(vupr.getActive());
        userRepository.save(user);
    }


    @Override
    public void createFollow(User fromUser, String toUserEmail) {
        User toUser = userRepository.findByEmail(toUserEmail).orElseThrow(() -> new NoSuchElementException("not found"));
        if (!userFollowRepository.existsByFromUserIdAndToUserId(fromUser.getId(), toUser.getId()) && fromUser.getId() != toUser.getId())
            userFollowRepository.save(UserFollow.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .build());
    }

    @Override
    public void deleteFollow(Long Id) {
        UserFollow follow = userFollowRepository.findById(Id).orElseThrow(() -> new NoSuchElementException("not found"));
        userFollowRepository.delete(follow);
    }

    @Transactional
    @Override
    public List<UserFollowDto> getFollowers(User user) {

        int page = 0;
        int size = userFollowRepository.countByToUserId(user.getId());
        size = size <= 0 ? 1 : size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserFollow> pageTuts = userFollowRepository.findByToUserId(pageable, user.getId());
        Page<UserFollowDto> followerList = pageTuts.map(m -> new UserFollowDto(m.getId(), m.getFromUser().getId(), m.getFromUser().getNickname(), m.getFromUser().getProfile()));

        return followerList.getContent();

    }

    @Override
    public List<UserFollowDto> getFollowing(User user) {

        int page = 0;
        int size = userFollowRepository.countByFromUserId(user.getId());
        size = size <= 0 ? 1 : size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserFollow> pageTuts = userFollowRepository.findByFromUserId(pageable, user.getId());
        Page<UserFollowDto> followerList = pageTuts.map(m -> new UserFollowDto(m.getId(), m.getToUser().getId(), m.getToUser().getNickname(), m.getToUser().getProfile()));

        return followerList.getContent();

    }

//    @Override
//    public List<UserFollowDto> getEachFollow(User user) {
//        List<UserFollowDto> list = ufRepositoryImpl.getEachFollow(user.getId());
//
////        Page<UserFollow> pageTuts = userFollowRepository.findByFromUser_IdIsAnd()
//        return null;
//    }

    @Override
    public List<User> getRandomUser(User user) {
        List<User> list = userRepositoryImpl.getRandomUser(user.getId());
        return list;
    }

    @Override
    public List<User> getRandomUserNotLogin() {
        List<User> list = userRepositoryImpl.getRandomUserNotLogin();
        return list;
    }

    @Override
    public List<User> getLocationUser(User user) {

        String[] address = user.getAddress().split(" ");
        String addr = address[0]+" "+address[1];


        List<User> list = userRepositoryImpl.getLocationUser(user.getId(), addr);

        return list;
    }

    @Override
    public List<UserInterestDto> getUserInterest(User user) {
        List<UserInterest> temp = userInterestRepository.findByUserId(user.getId());

        List<UserInterestDto> result = new ArrayList<>();
        for (UserInterest userInterest:temp) {
            UserInterestDto userInterestDto = new UserInterestDto();
            userInterestDto.setInterest(userInterest.getInterest());

            result.add(userInterestDto);
        }
        return result;
    }

}