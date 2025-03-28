package NguyenThanhTruong.com.shop.repository;

import NguyenThanhTruong.com.shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

}
