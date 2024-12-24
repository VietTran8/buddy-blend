package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.model.UserFavourite;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavouriteRepository extends JpaRepository<UserFavourite, String> {
    Boolean existsByName(String name);
    Optional<UserFavourite> findByNameAndUser(String name, User user);
    List<UserFavourite> findByUser(User user);
    Optional<UserFavourite> findByIdAndUser(String id, User user);
}
