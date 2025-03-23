package NguyenThanhTruong.com.shop.Controller;


import NguyenThanhTruong.com.shop.model.Category;
import NguyenThanhTruong.com.shop.model.Product;
import NguyenThanhTruong.com.shop.service.CategoryService;
import NguyenThanhTruong.com.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.nio.file.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static java.io.File.separator;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;  // Đảm bảo bạn đã inject


    @GetMapping("/ql")
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }
    @GetMapping("/category")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId, Model model) {
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        model.addAttribute("products", productService.getProductsByCategory(category.getName()));
        return "/products/products-cus-view"; // Đảm bảo template hiển thị danh sách sản phẩm phù hợp
    }

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @GetMapping()
    public String showProductsList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-cus-view";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("products", productService.searchProducts(keyword));
        return "/products/products-cus-view";
    }
    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocomplete(@RequestParam("term") String term) {
        return productService.searchProductNames(term);
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());  //

        return "/products/add-product";
    }
    // Process the form for adding a new product
    // Process the form for adding a new product
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result ,@RequestParam MultipartFile imageProduct) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        if (imageProduct != null && imageProduct.getSize() > 0) {
            try {
                File saveFile= new ClassPathResource("static/images").getFile();
                String newImageFile =UUID.randomUUID()+ ".png";
                Path path =Paths.get(saveFile.getAbsolutePath()+File.separator+ newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(newImageFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productService.addProduct(product);
        return "redirect:/products/ql";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());  //

        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }
        productService.updateProduct(product);
        return "redirect:/products/ql";
    }
    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products/ql";
    }
}