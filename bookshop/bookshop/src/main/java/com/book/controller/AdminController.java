package com.book.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.book.model.Book;
import com.book.model.Category;
import com.book.model.Order;
import com.book.model.User;
import com.book.service.BookService;
import com.book.service.CategoryService;
import com.book.service.OrderService;
import com.book.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public AdminController(BookService bookService, CategoryService categoryService, OrderService orderService, UserRepository userRepository) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private boolean checkAdmin(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && "ADMIN".equalsIgnoreCase(loggedUser.getRole());
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";

        List<Book> books = bookService.getAllBooks();
        List<Order> orders = orderService.getAllOrders();
        double totalRevenue = orders.stream()
                .filter(o -> !"CANCELLED".equals(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("totalBooks", books.size());
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin/dashboard";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("activeTab", "books");
        org.springframework.data.domain.Page<Book> bookPage = bookService.getAllBooksPaged(
            org.springframework.data.domain.PageRequest.of(page, size)
        );
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("size", size);
        return "admin/list";
    }

    @GetMapping("/create")
    public String create(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("activeTab", "books");
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/create";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute Book book, @RequestParam Integer categoryId, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        book.setCategory(category);
        bookService.saveBook(book);
        return "redirect:/admin/list";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Integer id, HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        Book book = bookService.getBookById(id).orElseThrow();
        model.addAttribute("activeTab", "books");
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/update";
    }

    @PostMapping("/update/{id}")
    public String saveUpdate(@PathVariable Integer id,
                             @ModelAttribute Book book,
                             @RequestParam Integer categoryId,
                             HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        book.setId(id);
        book.setCategory(category);
        bookService.saveBook(book);
        return "redirect:/admin/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        bookService.deleteBook(id);
        return "redirect:/admin/list";
    }

    @GetMapping("/orders")
    public String listOrders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("activeTab", "orders");
        org.springframework.data.domain.Page<Order> orderPage = orderService.getAllOrdersPaged(
            org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "orderDate"))
        );
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("size", size);
        return "admin/orders";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam Integer orderId,
                                    @RequestParam String status,
                                    HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Integer id, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }

    // ==========================================
    // quản lý thống kê
    // ==========================================
    @GetMapping("/statistics")
    public String statistics(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("activeTab", "statistics");

        int currentYear = java.time.LocalDate.now().getYear();
        
        // Revenue by month: initialize 12 months with 0
        List<Double> revenueByMonth = new java.util.ArrayList<>(java.util.Collections.nCopies(12, 0.0));
        List<Object[]> revenueData = orderService.getRevenueByMonth(currentYear);
        for (Object[] row : revenueData) {
            int month = ((Number) row[0]).intValue();
            double revenue = ((Number) row[1]).doubleValue();
            revenueByMonth.set(month - 1, revenue); // 0-indexed
        }
        
        // Order count by status
        List<String> statuses = java.util.Arrays.asList("PENDING", "CONFIRMED", "DELIVERED", "CANCELLED");
        List<Long> orderCounts = new java.util.ArrayList<>(java.util.Collections.nCopies(4, 0L));
        List<Object[]> statusData = orderService.getOrderCountByStatus();
        for (Object[] row : statusData) {
            String status = (String) row[0];
            long count = ((Number) row[1]).longValue();
            int index = statuses.indexOf(status);
            if (index != -1) {
                orderCounts.set(index, count);
            }
        }

        model.addAttribute("currentYear", currentYear);
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("statuses", statuses);
        model.addAttribute("orderCounts", orderCounts);

        Double totalRevenue = orderService.getTotalRevenue();
        int totalOrders = orderService.getAllOrders().size();
        Integer totalBooksSold = orderService.getTotalBooksSold();
        long totalCustomers = userRepository.countByRole("USER");

        model.addAttribute("totalRevenueStr", totalRevenue != null ? totalRevenue : 0.0);
        model.addAttribute("totalOrdersCount", totalOrders);
        model.addAttribute("totalBooksSold", totalBooksSold != null ? totalBooksSold : 0);
        model.addAttribute("totalCustomers", totalCustomers);

        return "admin/statistics";
    }

    @GetMapping("/statistics/export")
    public void exportStatisticsToExcel(HttpSession session, HttpServletResponse response) throws IOException {
        if (!checkAdmin(session)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        int currentYear = java.time.LocalDate.now().getYear();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê " + currentYear);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO THỐNG KÊ NĂM " + currentYear);
        titleCell.setCellStyle(headerStyle);

        Double totalRevenue = orderService.getTotalRevenue();
        int totalOrders = orderService.getAllOrders().size();
        Integer totalBooksSold = orderService.getTotalBooksSold();
        long totalCustomers = userRepository.countByRole("USER");

        Row metricsHeaderRow = sheet.createRow(2);
        metricsHeaderRow.createCell(0).setCellValue("Tổng doanh thu (VND)");
        metricsHeaderRow.createCell(1).setCellValue("Tổng đơn hàng");
        metricsHeaderRow.createCell(2).setCellValue("Sách đã bán");
        metricsHeaderRow.createCell(3).setCellValue("Số khách hàng");
        for(int i=0; i<4; i++) metricsHeaderRow.getCell(i).setCellStyle(headerStyle);

        Row metricsDataRow = sheet.createRow(3);
        metricsDataRow.createCell(0).setCellValue(totalRevenue != null ? totalRevenue : 0.0);
        metricsDataRow.createCell(1).setCellValue(totalOrders);
        metricsDataRow.createCell(2).setCellValue(totalBooksSold != null ? totalBooksSold : 0);
        metricsDataRow.createCell(3).setCellValue(totalCustomers);

        Row revHeaderRow = sheet.createRow(6);
        revHeaderRow.createCell(0).setCellValue("Tháng");
        revHeaderRow.createCell(1).setCellValue("Doanh thu (VND)");
        revHeaderRow.getCell(0).setCellStyle(headerStyle);
        revHeaderRow.getCell(1).setCellStyle(headerStyle);

        List<Object[]> revenueData = orderService.getRevenueByMonth(currentYear);
        double[] revByMonth = new double[12];
        for (Object[] row : revenueData) {
            int m = ((Number) row[0]).intValue();
            revByMonth[m - 1] = ((Number) row[1]).doubleValue();
        }

        for (int i = 0; i < 12; i++) {
            Row row = sheet.createRow(7 + i);
            row.createCell(0).setCellValue("Tháng " + (i + 1));
            row.createCell(1).setCellValue(revByMonth[i]);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Bao_Cao_Thong_Ke_" + currentYear + ".xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ==========================================
    // quản lý danh mục (crud)
    // ==========================================
    @GetMapping("/categories")
    public String listCategories(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("activeTab", "categories");
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/categories/create")
    public String createCategoryForm(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("activeTab", "categories");
        model.addAttribute("category", new Category());
        return "admin/categories/create";
    }

    @PostMapping("/categories/create")
    public String saveCategory(@ModelAttribute Category category, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/update/{id}")
    public String updateCategoryForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        Category category = categoryService.getCategoryById(id).orElseThrow();
        model.addAttribute("activeTab", "categories");
        model.addAttribute("category", category);
        return "admin/categories/update";
    }

    @PostMapping("/categories/update/{id}")
    public String saveUpdateCategory(@PathVariable Integer id, @ModelAttribute Category category, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        category.setId(id);
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    // ==========================================
    // quản lý khách hàng
    // ==========================================
    @GetMapping("/customers")
    public String listCustomers(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/auth/login";
        org.springframework.data.domain.Page<User> customerPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            customerPage = userRepository.searchCustomers(keyword, org.springframework.data.domain.PageRequest.of(page, size));
        } else {
            customerPage = userRepository.findByRole("USER", org.springframework.data.domain.PageRequest.of(page, size));
        }
        model.addAttribute("activeTab", "customers");
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "admin/customers";
    }
}
