-- Drop database if exists and recreate
DROP DATABASE IF EXISTS bookdb;
CREATE DATABASE bookdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookdb;

-- 1. Create table categories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Create table users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    fullname VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Create table books
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    image_url VARCHAR(500),
    quantity INT NOT NULL DEFAULT 0,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Create table orders
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    fullname VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Create table order_items
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    book_id INT,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================================
-- INSERT MOCK DATA
-- ========================================================

-- Insert Categories
INSERT INTO categories (name, description) VALUES
('Văn học', 'Các tác phẩm văn học kinh điển, tiểu thuyết, truyện ngắn Việt Nam và thế giới.'),
('Kinh tế', 'Sách về quản trị kinh doanh, marketing, khởi nghiệp, tài chính và phát triển doanh nghiệp.'),
('Kỹ năng', 'Sách tự phát triển bản thân, kỹ năng sống, giao tiếp và tư duy.'),
('Công nghệ', 'Sách về lập trình, trí tuệ nhân tạo, khoa học máy tính và thiết kế.'),
('Thiếu nhi', 'Truyện tranh, truyện ngụ ngôn và sách giáo dục dành cho trẻ em.');

-- Insert Users (Passwords are in plain text as current code compares simple strings user.getPassword().equals(password))
INSERT INTO users (username, password, role, fullname, email, phone, address) VALUES
('admin', 'admin123', 'ADMIN', 'Quản trị viên', 'admin@bookshop.vn', '0987654321', 'Cầu Giấy, Hà Nội'),
('user', 'user123', 'USER', 'Nguyễn Văn Anh', 'user@gmail.com', '0123456789', 'Từ Liêm, Hà Nội');


INSERT INTO books (title, author, description, price, image_url, quantity, category_id) VALUES
('Đắc Nhân Tâm', 'Dale Carnegie', 'Đắc nhân tâm (được lòng người) là một cuốn sách nhằm tự giúp bản thân bán chạy nhất từ trước đến nay của Dale Carnegie.', 86000, '/images/dacnhantam.png', 50, 3),     -- nhớ thay giống đắc nhân tâm nếu chụp ảnh , tại nó thay rồi nhg dùng select vs hiện 
('Nhà Giả Kim', 'Paulo Coelho', 'Cuốn sách bán chạy chỉ sau Kinh Thánh. Một câu chuyện ngụ ngôn giàu chất thơ về hành trình tìm kiếm vận mệnh bản thân.', 79000, 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=300&h=400&fit=crop', 30, 1),
('Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', 'Cuốn tiểu thuyết đầy cảm xúc của Nguyễn Nhật Ánh về tuổi thơ ở một làng quê nghèo Việt Nam.', 125000, 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=300&h=400&fit=crop', 45, 1),
('Cha Giàu Cha Nghèo', 'Robert Kiyosaki', 'Bộ sách dạy con làm giàu giúp thay đổi tư duy tài chính của hàng triệu người trên thế giới.', 99000, 'https://images.unsplash.com/photo-1592496431122-2349e0fbc666?w=300&h=400&fit=crop', 25, 2),
('Clean Code', 'Robert C. Martin', 'Sách gối đầu giường của mọi lập trình viên chuyên nghiệp muốn viết code sạch, dễ đọc và dễ bảo trì.', 350000, 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=300&h=400&fit=crop', 15, 4),
('Thiết Kế Giải Thuật', 'Steven S. Skiena', 'Hướng dẫn chi tiết về các giải thuật thông dụng trong khoa học máy tính và lập trình thi đấu.', 280000, 'https://images.unsplash.com/photo-1610116306796-6fea9f4fae38?w=300&h=400&fit=crop', 20, 4),
('Quốc Gia Khởi Nghiệp', 'Dan Senor', 'Câu chuyện về nền kinh tế thần kỳ của Israel và bài học cho sự phát triển của Việt Nam.', 119000, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=300&h=400&fit=crop', 35, 2),
('Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 'Tác phẩm văn học thiếu nhi kinh điển của nhà văn Tô Hoài kể về chuyến phiêu lưu đầy thú vị của chú Dế Mèn.', 55000, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300&h=400&fit=crop', 60, 5),
('Kính Vạn Hoa', 'Nguyễn Nhật Ánh', 'Bộ truyện dài đầy ắp tiếng cười tinh nghịch của lứa tuổi học trò Việt Nam.', 145000, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=300&h=400&fit=crop', 10, 5),
('Tư Duy Nhanh Và Chậm', 'Daniel Kahneman', 'Cuốn sách phân tích hai hệ thống tư duy chi phối mọi quyết định và hành vi của con người.', 180000, 'https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=300&h=400&fit=crop', 12, 3);
select *from books;
