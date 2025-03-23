package NguyenThanhTruong.com.shop.repository;

import NguyenThanhTruong.com.shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% ")
    List<Product> searchProducts(String keyword);
    @Query("SELECT p FROM Product p WHERE lower(p.category.name) LIKE lower(concat('%', ?1, '%'))")
    List<Product> findByCategoryName(String categoryName);

}

