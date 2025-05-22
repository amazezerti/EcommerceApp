package com.ecommerce.controller;

import com.ecommerce.dto.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/images/";

    public MainController(UserService userService, ProductService productService, OrderService orderService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
    }

    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping("/")
    public String landingPage(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to Our E-Commerce Store! Please login to access your dashboard.");
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "signup";
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists.");
            return "signup";
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already exists.");
            return "signup";
        }
        userService.registerUser(user);
        return "redirect:/login?signupSuccess=true";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("No authentication or user not authenticated, redirecting to /login");
            return "redirect:/login";
        }
        String role = authentication.getAuthorities().toArray()[0].toString();
        System.out.println("Authenticated user role: " + role);
        if (role.equals("ROLE_ADMIN")) {
            System.out.println("Redirecting to /admin/dashboard");
            return "redirect:/admin/dashboard";
        } else if (role.equals("ROLE_CUSTOMER")) {
            System.out.println("Redirecting to /customer/dashboard");
            return "redirect:/customer/dashboard";
        } else {
            System.out.println("Unknown role, redirecting to /login");
            return "redirect:/login";
        }
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model, Authentication authentication, HttpSession session) {
        try {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", user);
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products != null ? products : Collections.emptyList());
            List<CartItem> cart = getCart(session);
            model.addAttribute("cartItems", cart);
            double cartTotal = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
            model.addAttribute("cartTotal", cartTotal);
            return "customer_dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load customer dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/customer/cart")
    public String viewCart(Model model, HttpSession session, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", user);
            List<CartItem> cart = getCart(session);
            model.addAttribute("cartItems", cart != null ? cart : Collections.emptyList());
            double total = cart.stream()
                    .filter(item -> item != null)
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            model.addAttribute("total", total);
            // Log cart contents for debugging
            System.out.println("Cart items: " + cart);
            return "cart";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load cart: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/customer/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, HttpSession session) {
        Product product = productService.getProductById(productId);
        if (product != null && quantity > 0 && quantity <= product.getStock()) {
            List<CartItem> cart = getCart(session);
            CartItem cartItem = new CartItem();
            cartItem.setProductId(productId);
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cart.add(cartItem);
        }
        return "redirect:/customer/cart";
    }

    @PostMapping("/customer/cart/remove")
    public String removeFromCart(@RequestParam("productId") Long productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        return "redirect:/customer/cart";
    }

    @GetMapping("/customer/cart/clear")
    public String clearCart(@RequestParam(value = "redirect", defaultValue = "/customer/cart") String redirect, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.clear();
        return "redirect:" + redirect;
    }

    @GetMapping("/customer/orders")
    public String viewCustomerOrders(Model model, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", user);
            List<Order> orders = orderService.getOrdersByUser(user);
            model.addAttribute("orders", orders != null ? orders : Collections.emptyList());
            return "customer_orders";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load order history: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/customer/orders/place")
    public String placeOrder(Authentication authentication, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/customer/cart";
        }
        try {
            User user = userService.findByUsername(authentication.getName());
            // Convert CartItem to OrderItem
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cart) {
                Product product = productService.getProductById(cartItem.getProductId());
                if (product == null) {
                    System.out.println("Product not found for ID: " + cartItem.getProductId());
                    return "redirect:/customer/cart?error=Invalid product in cart";
                }
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                orderItems.add(orderItem);
            }
            orderService.placeOrder(user, orderItems);
            cart.clear();
            session.setAttribute("cart", cart);
            System.out.println("Order placed successfully for user: " + user.getUsername());
        } catch (Exception e) {
            System.out.println("Error placing order: " + e.getMessage());
            return "redirect:/customer/cart?error=Failed to place order";
        }
        return "redirect:/customer/orders";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", user);
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products != null ? products : Collections.emptyList());

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);
            List<Order> orders = orderService.getOrdersBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
            Map<LocalDate, Double> salesData = orders.stream()
                    .collect(Collectors.groupingBy(
                            order -> order.getOrderDate().toLocalDate(),
                            Collectors.summingDouble(Order::getTotalAmount)
                    ));

            List<String> labels = new ArrayList<>();
            List<Double> data = new ArrayList<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                labels.add(date.toString());
                data.add(salesData.getOrDefault(date, 0.0));
            }

            model.addAttribute("salesLabels", labels);
            model.addAttribute("salesData", data);
            return "admin_dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load admin dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/users")
    public String manageUsers(Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", currentUser);
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users != null ? users : Collections.emptyList());
            return "admin_users";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load users: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/user/delete")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit")
    public String editUserPage(@RequestParam("userId") Long userId, Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", currentUser);
            User user = userService.getUserById(userId);
            if (user == null) {
                model.addAttribute("error", "User not found.");
                return "error";
            }
            model.addAttribute("user", user);
            return "admin_user_edit";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load edit user page: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/users/edit")
    public String updateUser(
            @RequestParam("userId") Long userId,
            @RequestParam("username") String username,
            @RequestParam(" E-Mail") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            Model model) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                model.addAttribute("error", "User not found.");
                return "redirect:/admin/users?error";
            }
            user.setUsername(username);
            user.setEmail(email);
            if (!password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            user.setRole(role);
            userService.updateUser(user);
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to update user: " + e.getMessage());
            return "redirect:/admin/users?error";
        }
    }

    @GetMapping("/admin/products")
    public String manageProducts(Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", currentUser);
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products != null ? products : Collections.emptyList());
            return "admin_products";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load products: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/products/add")
    public String addProductPage(Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("product", new Product());
            return "admin_products_add";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load add product page: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/products/add")
    public String addProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("image") MultipartFile image,
            Model model) {
        if (result.hasErrors()) {
            return "admin_products_add";
        }
        try {
            if (!image.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename().replace(" ", "_");
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                image.transferTo(filePath.toFile());
                product.setImagePath("/images/" + fileName);
            }
            productService.saveProduct(product);
            return "redirect:/admin/products";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            return "admin_products_add";
        }
    }

    @GetMapping("/admin/products/edit")
    public String editProductPage(@RequestParam("id") Long productId, Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", currentUser);
            Product product = productService.getProductById(productId);
            if (product == null) {
                model.addAttribute("error", "Product not found.");
                return "error";
            }
            model.addAttribute("product", new Product());
            return "admin_product_edit";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load edit product page: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/products/edit")
    public String editProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("image") MultipartFile image,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Please correct the errors in the form.");
            return "admin_product_edit";
        }
        try {
            if (product.getId() == null) {
                model.addAttribute("error", "Product ID is missing.");
                return "redirect:/admin/products?error";
            }
            Product existingProduct = productService.getProductById(product.getId());
            if (existingProduct == null) {
                model.addAttribute("error", "Product not found.");
                return "redirect:/admin/products?error";
            }
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStock(product.getStock());
            existingProduct.setCategory(product.getCategory());

            if (!image.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename().replace(" ", "_");
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                image.transferTo(filePath.toFile());
                existingProduct.setImagePath("/images/" + fileName);
            }
            productService.saveProduct(existingProduct);
            return "redirect:/admin/products";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            return "admin_product_edit";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update product: " + e.getMessage());
            return "admin_product_edit";
        }
    }

    @PostMapping("/admin/products/delete")
    public String deleteProduct(@RequestParam("productId") Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                model.addAttribute("error", "Product not found.");
                return "redirect:/admin/products?error";
            }
            productService.deleteProduct(productId);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete product: " + e.getMessage());
            return "redirect:/admin/products?error";
        }
    }

    @GetMapping("/admin/orders")
    public String manageOrders(Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("currentUser", currentUser);
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders != null ? orders : Collections.emptyList());
            return "admin_orders";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load orders: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/upload-profile-pic")
    public String uploadProfilePic(@RequestParam("profilePic") MultipartFile file, Authentication authentication) throws IOException {
        User user = userService.findByUsername(authentication.getName());
        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            user.setProfilePicPath("/images/" + fileName);
            userService.updateUser(user);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "redirect:/dashboard?error";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "redirect:/dashboard?error";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}