<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Panel - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" th:href="@{/css/admin.css}">
</head>
<body class="bg-light">

<!-- Navbar -->
<nav class="navbar navbar-dark bg-dark shadow px-4">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold text-warning d-flex align-items-center gap-2" th:href="@{/admin/dashboard}">
            <i class="fa-solid fa-book-open"></i> BOOKSHOP Admin
        </a>
        <div class="d-flex align-items-center gap-3">
            <span class="text-white small">
                Xin chào, <b th:text="${session.username}">Admin</b>
            </span>
            <a th:href="@{/auth/logout}" class="btn btn-outline-light btn-sm"><i class="fa-solid fa-right-from-bracket"></i> Đăng xuất</a>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <div class="row g-4">
        <!-- Sidebar -->
        <div class="col-md-3">
            <div th:replace="~{fragments/admin-sidebar :: sidebar}"></div>
        </div>

        <!-- Content -->
        <div class="col-md-9">
            <h3 class="fw-bold mb-4 text-dark"><i class="fa-solid fa-chart-line text-primary"></i> Bảng điều khiển</h3>
            
            <div class="row g-3">
                <!-- Total Books -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm rounded-4 text-white bg-primary h-100 p-3">
                        <div class="card-body d-flex justify-content-between align-items-center">
                            <div>
                                <h4 class="fw-bold mb-1" th:text="${totalBooks}">0</h4>
                                <p class="mb-0 text-white-50 small">Tổng số sách</p>
                            </div>
                            <div class="fs-1 opacity-50"><i class="fa-solid fa-book"></i></div>
                        </div>
                    </div>
                </div>

                <!-- Total Orders -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm rounded-4 text-white bg-success h-100 p-3">
                        <div class="card-body d-flex justify-content-between align-items-center">
                            <div>
                                <h4 class="fw-bold mb-1" th:text="${totalOrders}">0</h4>
                                <p class="mb-0 text-white-50 small">Tổng đơn hàng</p>
                            </div>
                            <div class="fs-1 opacity-50"><i class="fa-solid fa-receipt"></i></div>
                        </div>
                    </div>
                </div>

                <!-- Total Revenue -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm rounded-4 text-white bg-danger h-100 p-3">
                        <div class="card-body d-flex justify-content-between align-items-center">
                            <div>
                                <h4 class="fw-bold mb-1" th:text="${#numbers.formatDecimal(totalRevenue, 0, 'COMMA', 0, 'POINT')} + ' ₫'">0 ₫</h4>
                                <p class="mb-0 text-white-50 small">Tổng doanh thu</p>
                            </div>
                            <div class="fs-1 opacity-50"><i class="fa-solid fa-wallet"></i></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Info & Instructions -->
            <div class="bg-white rounded shadow-sm p-4 mt-4">
                <h5 class="fw-bold mb-3">Chào mừng đến với Trang quản trị BOOKSHOP</h5>
                <p class="text-secondary mb-0" style="line-height: 1.7;">
                    Tại đây bạn có thể dễ dàng quản lý kho sách của mình (thêm mới, chỉnh sửa thông tin, cập nhật số lượng, xóa sách) và theo dõi tình trạng đơn đặt hàng từ khách hàng trong thời gian thực.
                </p>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>