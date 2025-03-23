package NguyenThanhTruong.com.shop.repository;

import NguyenThanhTruong.com.shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
        @Query("SELECT o FROM Order o WHERE o.customerName = :username")
        List<Order> findOrdersByCustomerName(@Param("username") String username);
        @Query("SELECT COALESCE(SUM(o.totalamount), 0) FROM Order o")
        long sumTotalAmount();

}
