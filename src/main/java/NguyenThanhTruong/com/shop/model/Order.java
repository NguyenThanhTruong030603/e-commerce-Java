package NguyenThanhTruong.com.shop.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String Diachi;
    private String SDT;
    private String Email;
    private String Ghichu;
    private String PTTT;
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
    private double  totalamount;

}

