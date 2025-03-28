package NguyenThanhTruong.com.shop.repository;

import NguyenThanhTruong.com.shop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
