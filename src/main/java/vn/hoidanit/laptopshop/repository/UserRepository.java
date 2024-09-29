package vn.hoidanit.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hoidanit.laptopshop.domain.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User eric);

    User findById(long id);

    List<User> findOneByEmail(String email);

    List<User> findAll();

    void deleteById(long id);

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
