-- ============================================================
--  TUGAS AKHIR - BASIS DATA
--  Program Studi Teknik Informatika
--  Universitas Brawijaya 2026
--  Kelompok: Winnola Aiko Wijaya, Joshua Wisjmuller Madja,
--             Ulfa Aulia Sakina, Athiya Eka Zafirah
-- ============================================================

-- ============================================================
-- BAGIAN 1: DDL - PEMBUATAN DATABASE DAN TABEL
-- ============================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BD_TugasAkhir')
BEGIN
    CREATE DATABASE BD_TugasAkhir;
END
GO
USE BD_TugasAkhir;
GO

-- -------------------------------------------------------
-- Hapus tabel jika sudah ada (urutan terbalik dari FK)
-- -------------------------------------------------------
IF OBJECT_ID('E_WALLET',      'U') IS NOT NULL DROP TABLE E_WALLET;
IF OBJECT_ID('BANK_TRANSFER', 'U') IS NOT NULL DROP TABLE BANK_TRANSFER;
IF OBJECT_ID('PAYMENT',       'U') IS NOT NULL DROP TABLE PAYMENT;
IF OBJECT_ID('SHIPMENT',      'U') IS NOT NULL DROP TABLE SHIPMENT;
IF OBJECT_ID('REVIEW',        'U') IS NOT NULL DROP TABLE REVIEW;
IF OBJECT_ID('ORDER_ITEM',    'U') IS NOT NULL DROP TABLE ORDER_ITEM;
IF OBJECT_ID('[ORDER]',       'U') IS NOT NULL DROP TABLE [ORDER];
IF OBJECT_ID('ACCESSORY',     'U') IS NOT NULL DROP TABLE ACCESSORY;
IF OBJECT_ID('CLOTHING',      'U') IS NOT NULL DROP TABLE CLOTHING;
IF OBJECT_ID('PRODUCT',       'U') IS NOT NULL DROP TABLE PRODUCT;
IF OBJECT_ID('CATEGORY',      'U') IS NOT NULL DROP TABLE CATEGORY;
IF OBJECT_ID('SELLER',        'U') IS NOT NULL DROP TABLE SELLER;
IF OBJECT_ID('CUSTOMER_PHONE','U') IS NOT NULL DROP TABLE CUSTOMER_PHONE;
IF OBJECT_ID('CUSTOMER',      'U') IS NOT NULL DROP TABLE CUSTOMER;
IF OBJECT_ID('PROMO',         'U') IS NOT NULL DROP TABLE PROMO;
GO

-- -------------------------------------------------------
-- 1. CUSTOMER
-- -------------------------------------------------------
CREATE TABLE CUSTOMER (
    Customer_ID   VARCHAR(20)  NOT NULL,
    FullName      VARCHAR(100) NOT NULL,
    Email         VARCHAR(100) NOT NULL UNIQUE,
    Password      VARCHAR(100) NOT NULL,
    Balance       DECIMAL(15,2) NOT NULL DEFAULT 0,   -- saldo dompet (topup)
    Reg_Date      DATE          NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_Customer PRIMARY KEY (Customer_ID)
);

-- -------------------------------------------------------
-- 2. CUSTOMER_PHONE (atribut multivalued)
-- -------------------------------------------------------
CREATE TABLE CUSTOMER_PHONE (
    Customer_ID VARCHAR(20) NOT NULL,
    Phone_No    VARCHAR(15) NOT NULL,
    CONSTRAINT PK_CustomerPhone PRIMARY KEY (Customer_ID, Phone_No),
    CONSTRAINT FK_CustPhone_Cust FOREIGN KEY (Customer_ID)
        REFERENCES CUSTOMER(Customer_ID) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- 3. SELLER
-- -------------------------------------------------------
CREATE TABLE SELLER (
    seller_Id    VARCHAR(10)  NOT NULL,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    StoreName    VARCHAR(100) NOT NULL,
    Balance      DECIMAL(15,2) NOT NULL DEFAULT 0,
    CONSTRAINT PK_Seller PRIMARY KEY (seller_Id)
);

-- -------------------------------------------------------
-- 4. CATEGORY
-- -------------------------------------------------------
CREATE TABLE CATEGORY (
    Category_ID  VARCHAR(10)  NOT NULL,
    CName        VARCHAR(50)  NOT NULL,
    Description  VARCHAR(255),
    CONSTRAINT PK_Category PRIMARY KEY (Category_ID)
);

-- -------------------------------------------------------
-- 5. PROMO
-- -------------------------------------------------------
CREATE TABLE PROMO (
    Promo_Code    VARCHAR(20)  NOT NULL,
    Description   VARCHAR(255),
    Discount_Pct  DECIMAL(5,2) NOT NULL,   -- persentase diskon 0-100
    Min_Purchase  DECIMAL(15,2) NOT NULL DEFAULT 0,
    Valid_From    DATE NOT NULL,
    Valid_Until   DATE NOT NULL,
    Is_Active     BIT  NOT NULL DEFAULT 1,
    CONSTRAINT PK_Promo      PRIMARY KEY (Promo_Code),
    CONSTRAINT CHK_Discount  CHECK (Discount_Pct >= 0 AND Discount_Pct <= 100)
);

-- -------------------------------------------------------
-- 6. PRODUCT
-- -------------------------------------------------------
CREATE TABLE PRODUCT (
    Product_ID   VARCHAR(10)   NOT NULL,
    PName        VARCHAR(100)  NOT NULL,
    Price        DECIMAL(15,2) NOT NULL,
    Stock        INT           NOT NULL DEFAULT 0,
    Weight_gram  INT           NOT NULL DEFAULT 0,
    Description  VARCHAR(500),
    Category_ID  VARCHAR(10)   NOT NULL,
    seller_Id    VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_Product   PRIMARY KEY (Product_ID),
    CONSTRAINT FK_Prod_Cat  FOREIGN KEY (Category_ID) REFERENCES CATEGORY(Category_ID),
    CONSTRAINT FK_Prod_Sell FOREIGN KEY (seller_Id)   REFERENCES SELLER(seller_Id),
    CONSTRAINT CHK_Price    CHECK (Price >= 0),
    CONSTRAINT CHK_Stock    CHECK (Stock >= 0)
);

-- -------------------------------------------------------
-- 7. CLOTHING (subtipe PRODUCT)
-- -------------------------------------------------------
CREATE TABLE CLOTHING (
    Product_ID      VARCHAR(10) NOT NULL,
    Size            VARCHAR(10) NOT NULL,
    Material        VARCHAR(50),
    Gender_Category VARCHAR(20),
    CONSTRAINT PK_Clothing  PRIMARY KEY (Product_ID),
    CONSTRAINT FK_Cloth_Prod FOREIGN KEY (Product_ID) REFERENCES PRODUCT(Product_ID)
);

-- -------------------------------------------------------
-- 8. ACCESSORY (subtipe PRODUCT)
-- -------------------------------------------------------
CREATE TABLE ACCESSORY (
    Product_ID VARCHAR(10) NOT NULL,
    Type       VARCHAR(50),
    Material   VARCHAR(50),
    CONSTRAINT PK_Accessory   PRIMARY KEY (Product_ID),
    CONSTRAINT FK_Acc_Prod    FOREIGN KEY (Product_ID) REFERENCES PRODUCT(Product_ID)
);

-- -------------------------------------------------------
-- 9. ORDER
-- -------------------------------------------------------
CREATE TABLE [ORDER] (
    Order_ID    VARCHAR(10)   NOT NULL,
    Order_Date  DATETIME      NOT NULL DEFAULT GETDATE(),
    Status      VARCHAR(50)   NOT NULL DEFAULT 'Menunggu Pembayaran',
    Total_Price DECIMAL(15,2) NOT NULL DEFAULT 0,
    Customer_ID VARCHAR(10)   NOT NULL,
    Promo_Code  VARCHAR(20)   NULL,

    CONSTRAINT PK_Order PRIMARY KEY (Order_ID),
    CONSTRAINT FK_Ord_Cust FOREIGN KEY (Customer_ID)
        REFERENCES CUSTOMER(Customer_ID),

    CONSTRAINT FK_Ord_Promo FOREIGN KEY (Promo_Code)
        REFERENCES PROMO(Promo_Code)
);

-- -------------------------------------------------------
-- 10. ORDER_ITEM
-- -------------------------------------------------------
CREATE TABLE ORDER_ITEM (
    Order_ID   VARCHAR(10)   NOT NULL,
    Product_ID VARCHAR(10)   NOT NULL,
    Quantity   INT           NOT NULL DEFAULT 1,
    Unit_Price DECIMAL(15,2) NOT NULL,   -- harga saat transaksi (snapshot)
    CONSTRAINT PK_OrderItem  PRIMARY KEY (Order_ID, Product_ID),
    CONSTRAINT FK_OI_Order   FOREIGN KEY (Order_ID)   REFERENCES [ORDER](Order_ID),
    CONSTRAINT FK_OI_Product FOREIGN KEY (Product_ID) REFERENCES PRODUCT(Product_ID),
    CONSTRAINT CHK_Qty       CHECK (Quantity > 0)
);

-- -------------------------------------------------------
-- 11. REVIEW
-- -------------------------------------------------------
CREATE TABLE REVIEW (
    Review_ID   VARCHAR(10)  NOT NULL,
    Rating      INT          NOT NULL,
    Comment     VARCHAR(500),
    Review_Date DATE         NOT NULL DEFAULT GETDATE(),
    Order_ID    VARCHAR(10)  NOT NULL,
    Product_ID  VARCHAR(10)  NOT NULL,
    CONSTRAINT PK_Review     PRIMARY KEY (Review_ID),
    CONSTRAINT FK_Rev_OI     FOREIGN KEY (Order_ID, Product_ID)
                             REFERENCES ORDER_ITEM(Order_ID, Product_ID),
    CONSTRAINT CHK_Rating    CHECK (Rating >= 1 AND Rating <= 5)
);

-- -------------------------------------------------------
-- 12. SHIPMENT
-- -------------------------------------------------------
CREATE TABLE SHIPMENT (
    TrackingNo    VARCHAR(20)  NOT NULL,
    Courier       VARCHAR(50)  NOT NULL,
    Ship_Date     DATE,
    Ship_Status   VARCHAR(50)  NOT NULL DEFAULT 'Menunggu Konfirmasi',
    Deliv_Address VARCHAR(255) NOT NULL,
    Order_ID      VARCHAR(10)  NOT NULL,
    CONSTRAINT PK_Shipment  PRIMARY KEY (TrackingNo),
    CONSTRAINT FK_Ship_Ord  FOREIGN KEY (Order_ID) REFERENCES [ORDER](Order_ID)
);

-- -------------------------------------------------------
-- 13. PAYMENT
-- -------------------------------------------------------
CREATE TABLE PAYMENT (
    ReferenceNo    VARCHAR(20)   NOT NULL,
    Order_ID       VARCHAR(10)   NOT NULL,
    Payment_Date   DATETIME      NOT NULL DEFAULT GETDATE(),
    Amount         DECIMAL(15,2) NOT NULL,
    Payment_Status VARCHAR(50)   NOT NULL DEFAULT 'Menunggu',
    CONSTRAINT PK_Payment  PRIMARY KEY (ReferenceNo, Order_ID),
    CONSTRAINT FK_Pay_Ord  FOREIGN KEY (Order_ID) REFERENCES [ORDER](Order_ID),
    CONSTRAINT CHK_Amount  CHECK (Amount >= 0)
);

-- -------------------------------------------------------
-- 14. BANK_TRANSFER (subtipe PAYMENT)
-- -------------------------------------------------------
CREATE TABLE BANK_TRANSFER (
    ReferenceNo  VARCHAR(20) NOT NULL,
    Order_ID     VARCHAR(10) NOT NULL,
    BnkName      VARCHAR(50) NOT NULL,
    Sender_AccNo VARCHAR(50) NOT NULL,
    CONSTRAINT PK_BankTrf  PRIMARY KEY (ReferenceNo, Order_ID),
    CONSTRAINT FK_BT_Pay   FOREIGN KEY (ReferenceNo, Order_ID)
                           REFERENCES PAYMENT(ReferenceNo, Order_ID)
);

-- -------------------------------------------------------
-- 15. E_WALLET (subtipe PAYMENT)
-- -------------------------------------------------------
CREATE TABLE E_WALLET (
    ReferenceNo   VARCHAR(20) NOT NULL,
    Order_ID      VARCHAR(10) NOT NULL,
    Provider_Name VARCHAR(50) NOT NULL,
    Acc_Phone     VARCHAR(15) NOT NULL,
    CONSTRAINT PK_EWallet  PRIMARY KEY (ReferenceNo, Order_ID),
    CONSTRAINT FK_EW_Pay   FOREIGN KEY (ReferenceNo, Order_ID)
                           REFERENCES PAYMENT(ReferenceNo, Order_ID)
);
GO

-- ============================================================
-- BAGIAN 2: TRIGGER
-- ============================================================

-- Trigger 1: Kurangi stok otomatis saat ORDER_ITEM dimasukkan
CREATE OR ALTER TRIGGER trg_AfterInsertOrderItem
ON ORDER_ITEM
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE P
    SET P.Stock = P.Stock - I.Quantity
    FROM PRODUCT P
    INNER JOIN INSERTED I ON P.Product_ID = I.Product_ID;
END;
GO

-- Trigger 2: Kembalikan stok jika ORDER_ITEM dihapus (cancel)
CREATE OR ALTER TRIGGER trg_AfterDeleteOrderItem
ON ORDER_ITEM
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE P
    SET P.Stock = P.Stock + D.Quantity
    FROM PRODUCT P
    INNER JOIN DELETED D ON P.Product_ID = D.Product_ID;
END;
GO

-- Trigger 3: Update Total_Price di ORDER secara otomatis
CREATE OR ALTER TRIGGER trg_UpdateOrderTotal
ON ORDER_ITEM
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    -- Hitung ulang total dari ORDER_ITEM
    UPDATE O
    SET O.Total_Price = ISNULL((
        SELECT SUM(OI.Quantity * OI.Unit_Price)
        FROM ORDER_ITEM OI
        WHERE OI.Order_ID = O.Order_ID
    ), 0)
    FROM [ORDER] O
    WHERE O.Order_ID IN (
        SELECT DISTINCT Order_ID FROM INSERTED
        UNION
        SELECT DISTINCT Order_ID FROM DELETED
    );
END;
GO

-- Trigger 4: Ubah status ORDER menjadi 'Selesai' saat SHIPMENT Delivered
CREATE OR ALTER TRIGGER trg_ShipmentDelivered
ON SHIPMENT
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE O
    SET O.Status = 'Selesai'
    FROM [ORDER] O
    INNER JOIN INSERTED I ON O.Order_ID = I.Order_ID
    WHERE I.Ship_Status = 'Delivered'
      AND O.Status <> 'Selesai';
END;
GO

-- ============================================================
-- BAGIAN 3: DML - INSERT DATA (data diperbanyak untuk pengujian)
-- ============================================================

-- 1. CUSTOMER (10 data)
INSERT INTO CUSTOMER (Customer_ID, FullName, Email, Password, Balance, Reg_Date) VALUES
('C001', 'Budi Santoso',      'budi@email.com',        'pass123',    500000,  '2023-10-26'),
('C002', 'Siti Aminah',       'siti@email.com',         'aman456',   750000,  '2023-10-30'),
('C003', 'Andi Wijaya',       'andi.w@email.com',       'rahasia789', 200000,  '2023-11-04'),
('C004', 'Rina Kusuma',       'rina.k@email.com',       'rina2024',  1000000, '2023-11-10'),
('C005', 'Dodi Prasetyo',     'dodi.p@email.com',       'dodi5678',  300000,  '2023-11-15'),
('C006', 'Mega Putri',        'mega.p@email.com',       'mega8899',  600000,  '2023-12-01'),
('C007', 'Hendra Gunawan',    'hendra.g@email.com',     'hg2345',    150000,  '2023-12-05'),
('C008', 'Dewi Lestari',      'dewi.l@email.com',       'dl9988',    900000,  '2024-01-10'),
('C009', 'Fajar Nugroho',     'fajar.n@email.com',      'fn1122',    450000,  '2024-01-20'),
('C010', 'Laras Wulandari',   'laras.w@email.com',      'lw3344',    800000,  '2024-02-01');

-- 2. CUSTOMER_PHONE
INSERT INTO CUSTOMER_PHONE (Customer_ID, Phone_No) VALUES
('C001','081234567890'),('C001','081566778899'),
('C002','085711223344'),('C003','081199887766'),
('C004','082211334455'),('C005','083344556677'),
('C006','084455667788'),('C007','085566778899'),
('C008','086677889900'),('C009','087788990011'),
('C010','088899001122');

-- 3. SELLER (5 data)
INSERT INTO SELLER (seller_Id, username, password, email, StoreName, Balance) VALUES
('S001', 'TokoBajuKeren',    'toko123', 'admin@tokokeren.com',         'Toko Baju Keren',     2500000),
('S002', 'AksesorisMantap',  'aks123',  'halo@aksesorismantap.id',     'Aksesoris Mantap',    1200000),
('S003', 'FashionStyle99',   'fs9999',  'cs@fashionstyle99.com',       'Fashion Style 99',    3000000),
('S004', 'OutfitKece',       'ok2024',  'support@outfitkece.id',       'Outfit Kece',         800000),
('S005', 'BrandLokal',       'bl5678',  'info@brandlokal.com',         'Brand Lokal',         1500000);

-- 4. CATEGORY (6 data)
INSERT INTO CATEGORY (Category_ID, CName, Description) VALUES
('CAT-01', 'Atasan Pria',     'Kumpulan baju atasan untuk pria'),
('CAT-02', 'Bawahan Wanita',  'Kumpulan celana dan rok wanita'),
('CAT-03', 'Aksesoris Kepala','Topi, bando, jepit rambut, dll'),
('CAT-04', 'Atasan Wanita',   'Blouse, kemeja, dan kaos wanita'),
('CAT-05', 'Bawahan Pria',    'Celana chino, jeans, dan jogger pria'),
('CAT-06', 'Aksesoris Badan', 'Sabuk, gelang, kalung, dll');

-- 5. PROMO (5 data)
INSERT INTO PROMO (Promo_Code, Description, Discount_Pct, Min_Purchase, Valid_From, Valid_Until) VALUES
('PROMO10',  'Diskon 10% untuk semua produk',          10.00, 50000,  '2023-11-01', '2024-12-31'),
('SALE20',   'Flash Sale 20% weekend',                 20.00, 100000, '2023-12-01', '2024-06-30'),
('NEWUSER',  'Diskon 15% untuk member baru',           15.00, 0,      '2023-10-01', '2024-12-31'),
('HARBOL30', 'Harbolnas Diskon 30%',                   30.00, 200000, '2023-12-12', '2023-12-12'),
('FREEONG',  'Gratis Ongkos Kirim (Diskon 5% ekstra)', 5.00,  75000,  '2024-01-01', '2024-12-31');

-- 6. PRODUCT (15 data)
INSERT INTO PRODUCT (Product_ID, PName, Price, Stock, Weight_gram, Description, Category_ID, seller_Id) VALUES
('P001', 'Kaos Polos Hitam',     50000,  100, 200, 'Kaos katun combed 30s berwarna hitam',         'CAT-01', 'S001'),
('P002', 'Rok Plisket Mocca',    75000,   50, 300, 'Rok wanita bahan jatuh warna mocca',           'CAT-02', 'S001'),
('P003', 'Topi Baseball',        35000,  200, 100, 'Topi kasual pria/wanita model baseball',       'CAT-03', 'S002'),
('P004', 'Kemeja Flanel',       120000,   30, 250, 'Kemeja kotak-kotak premium bahan flanel',      'CAT-01', 'S001'),
('P005', 'Blouse Bunga',         90000,   80, 220, 'Blouse wanita motif bunga bahan katun',        'CAT-04', 'S003'),
('P006', 'Celana Chino',        145000,   60, 500, 'Celana chino pria slim fit berbagai warna',    'CAT-05', 'S003'),
('P007', 'Sabuk Kulit',          65000,  150, 180, 'Sabuk kulit sintetis pria warna hitam/coklat', 'CAT-06', 'S002'),
('P008', 'Kaos Polos Putih',     50000,  120, 200, 'Kaos katun combed 30s berwarna putih',         'CAT-01', 'S001'),
('P009', 'Jeans Slim Fit',      195000,   40, 600, 'Celana jeans pria slim fit warna navy',        'CAT-05', 'S004'),
('P010', 'Cardigan Rajut',      115000,   70, 350, 'Cardigan rajut wanita oversize',               'CAT-04', 'S003'),
('P011', 'Topi Bucket',          45000,  160, 120, 'Topi bucket unisex bahan kanvas tebal',        'CAT-03', 'S002'),
('P012', 'Kemeja Batik',        175000,   25, 280, 'Kemeja batik modern lengan panjang pria',      'CAT-01', 'S005'),
('P013', 'Rok Jeans Mini',       88000,   35, 260, 'Rok jeans pendek wanita warna biru',           'CAT-02', 'S004'),
('P014', 'Kalung Rantai',        55000,  200, 80,  'Kalung rantai wanita bahan stainless anti karat','CAT-06','S002'),
('P015', 'Jogger Pants',         99000,   90, 400, 'Celana jogger pria bahan fleece nyaman',       'CAT-05', 'S005');

-- 7. CLOTHING (subtipe)
INSERT INTO CLOTHING (Product_ID, Size, Material, Gender_Category) VALUES
('P001', 'L',        'Katun Combed 30s', 'Pria'),
('P002', 'All Size', 'Bahan Hyget',      'Wanita'),
('P004', 'XL',       'Kain Flanel',      'Pria'),
('P005', 'M',        'Katun Rayon',      'Wanita'),
('P006', 'L',        'Katun Chino',      'Pria'),
('P008', 'M',        'Katun Combed 30s', 'Unisex'),
('P009', '32',       'Denim',            'Pria'),
('P010', 'All Size', 'Rajut Acrylic',    'Wanita'),
('P012', 'L',        'Katun Batik',      'Pria'),
('P013', '28',       'Denim',            'Wanita'),
('P015', 'XL',       'Fleece',           'Pria');

-- 8. ACCESSORY (subtipe)
INSERT INTO ACCESSORY (Product_ID, Type, Material) VALUES
('P003', 'Topi',    'Kanvas'),
('P007', 'Sabuk',   'Kulit Sintetis'),
('P011', 'Topi',    'Kanvas Tebal'),
('P014', 'Kalung',  'Stainless Steel');

-- 9. ORDER (20 data)
INSERT INTO [ORDER] (Order_ID, Order_Date, Status, Total_Price, Customer_ID, Promo_Code) VALUES
('ORD-101','2023-11-05 10:00','Selesai',           135000, 'C001', NULL),
('ORD-102','2023-11-06 14:30','Selesai',            75000, 'C002', NULL),
('ORD-103','2023-11-09 09:15','Dibatalkan',         240000, 'C003', NULL),
('ORD-104','2023-11-15 11:00','Selesai',            90000, 'C004', 'PROMO10'),
('ORD-105','2023-11-20 16:45','Selesai',           195000, 'C005', NULL),
('ORD-106','2023-12-01 08:30','Selesai',           365000, 'C006', 'SALE20'),
('ORD-107','2023-12-05 13:20','Selesai',           115000, 'C007', NULL),
('ORD-108','2023-12-10 10:55','Selesai',           200000, 'C008', NULL),
('ORD-109','2023-12-12 00:01','Selesai',           316000, 'C001', 'HARBOL30'),
('ORD-110','2023-12-15 15:30','Selesai',           175000, 'C002', NULL),
('ORD-111','2024-01-05 09:00','Selesai',            99000, 'C003', 'FREEONG'),
('ORD-112','2024-01-10 11:30','Selesai',           260000, 'C004', NULL),
('ORD-113','2024-01-15 14:00','Selesai',           145000, 'C005', NULL),
('ORD-114','2024-01-20 16:00','Selesai',           120000, 'C006', NULL),
('ORD-115','2024-01-25 10:20','Dikirim',           295000, 'C007', NULL),
('ORD-116','2024-02-01 09:45','Dikirim',           165000, 'C008', NULL),
('ORD-117','2024-02-05 13:10','Dikirim',            50000, 'C009', NULL),
('ORD-118','2024-02-10 15:30','Menunggu Pembayaran',195000, 'C010', 'NEWUSER'),
('ORD-119','2024-02-15 11:00','Menunggu Pembayaran',175000, 'C009', NULL),
('ORD-120','2024-02-20 14:00','Menunggu Pembayaran',115000, 'C010', NULL);

-- 10. ORDER_ITEM (30 data)
INSERT INTO ORDER_ITEM (Order_ID, Product_ID, Quantity, Unit_Price) VALUES
('ORD-101','P001',2, 50000),('ORD-101','P003',1, 35000),
('ORD-102','P002',1, 75000),
('ORD-103','P004',2,120000),
('ORD-104','P005',1, 90000),
('ORD-105','P009',1,195000),
('ORD-106','P012',1,175000),('ORD-106','P007',1, 65000),('ORD-106','P011',1, 45000),
('ORD-107','P010',1,115000),
('ORD-108','P006',1,145000),('ORD-108','P007',1, 55000),
('ORD-109','P001',2, 50000),('ORD-109','P008',2, 50000),('ORD-109','P003',2, 35000),
('ORD-110','P012',1,175000),
('ORD-111','P015',1, 99000),
('ORD-112','P009',1,195000),('ORD-112','P006',1, 65000),
('ORD-113','P006',1,145000),
('ORD-114','P004',1,120000),
('ORD-115','P012',1,175000),('ORD-115','P015',1, 99000),('ORD-115','P007',1, 21000),
('ORD-116','P010',1,115000),('ORD-116','P014',1, 50000),
('ORD-117','P001',1, 50000),
('ORD-118','P009',1,195000),
('ORD-119','P012',1,175000),
('ORD-120','P010',1,115000);

-- 11. REVIEW
INSERT INTO REVIEW (Review_ID, Rating, Comment, Review_Date, Order_ID, Product_ID) VALUES
('REV-001',5,'Bahan bagus banget, adem dipakai!',    '2023-11-08','ORD-101','P001'),
('REV-002',4,'Topi lumayan, sesuai deskripsi.',       '2023-11-08','ORD-101','P003'),
('REV-003',5,'Roknya cantik, bahan jatuh banget.',    '2023-11-08','ORD-102','P002'),
('REV-004',4,'Blouse nyaman dan motifnya lucu.',       '2023-11-18','ORD-104','P005'),
('REV-005',5,'Jeans kualitas bagus, pas di badan.',   '2023-11-22','ORD-105','P009'),
('REV-006',3,'Kemeja batik oke, tapi jahitan agak kasar.','2023-12-04','ORD-106','P012'),
('REV-007',5,'Cardigan oversize nyaman banget.',       '2023-12-08','ORD-107','P010'),
('REV-008',4,'Celana chino slim fit, rapi dipake.',   '2023-12-13','ORD-108','P006'),
('REV-009',5,'Kaos putih polos bagus buat sehari-hari.','2023-12-14','ORD-109','P008'),
('REV-010',4,'Kemeja batik, pengiriman cepat.',        '2023-12-18','ORD-110','P012'),
('REV-011',5,'Jogger pants empuk, cocok santai.',      '2024-01-08','ORD-111','P015'),
('REV-012',4,'Jeans bagus, ukuran sesuai.',            '2024-01-13','ORD-112','P009'),
('REV-013',3,'Celana chino oke tapi warna beda sedikit.','2024-01-18','ORD-113','P006'),
('REV-014',5,'Kemeja flanel hangat dan nyaman.',       '2024-01-23','ORD-114','P004');

-- 12. SHIPMENT
INSERT INTO SHIPMENT (TrackingNo, Courier, Ship_Date, Ship_Status, Deliv_Address, Order_ID) VALUES
('TRK-001','JNE',  '2023-11-06','Delivered',     'Jl. Mawar No 1, Jakarta Selatan',     'ORD-101'),
('TRK-002','J&T',  '2023-11-07','Delivered',     'Jl. Melati No 5, Bandung',            'ORD-102'),
('TRK-003','SiCepat','2023-11-15','Delivered',   'Jl. Anggrek No 3, Surabaya',          'ORD-104'),
('TRK-004','Anteraja','2023-11-21','Delivered',  'Jl. Dahlia No 7, Yogyakarta',         'ORD-105'),
('TRK-005','JNE',  '2023-12-02','Delivered',     'Jl. Kenanga No 9, Semarang',          'ORD-106'),
('TRK-006','J&T',  '2023-12-06','Delivered',     'Jl. Flamboyan No 11, Malang',         'ORD-107'),
('TRK-007','SiCepat','2023-12-11','Delivered',   'Jl. Tulip No 2, Medan',               'ORD-108'),
('TRK-008','Anteraja','2023-12-13','Delivered',  'Jl. Mawar No 1, Jakarta Selatan',     'ORD-109'),
('TRK-009','JNE',  '2023-12-16','Delivered',     'Jl. Melati No 5, Bandung',            'ORD-110'),
('TRK-010','J&T',  '2024-01-06','Delivered',     'Jl. Anggrek No 3, Surabaya',          'ORD-111'),
('TRK-011','SiCepat','2024-01-11','Delivered',   'Jl. Dahlia No 7, Yogyakarta',         'ORD-112'),
('TRK-012','JNE',  '2024-01-16','Delivered',     'Jl. Kenanga No 9, Semarang',          'ORD-113'),
('TRK-013','Anteraja','2024-01-21','Delivered',  'Jl. Flamboyan No 11, Malang',         'ORD-114'),
('TRK-014','JNE',  '2024-01-26','On Transit',    'Jl. Tulip No 2, Medan',               'ORD-115'),
('TRK-015','J&T',  '2024-02-02','On Transit',    'Jl. Mawar No 4, Jakarta Timur',       'ORD-116'),
('TRK-016','SiCepat','2024-02-06','On Transit',  'Jl. Melati No 8, Bandung',            'ORD-117');

-- 13. PAYMENT
INSERT INTO PAYMENT (ReferenceNo, Order_ID, Payment_Date, Amount, Payment_Status) VALUES
('PAY-001','ORD-101','2023-11-05 10:05', 135000,'Lunas'),
('PAY-002','ORD-102','2023-11-06 14:35',  75000,'Lunas'),
('PAY-003','ORD-104','2023-11-15 11:10',  81000,'Lunas'),   -- setelah diskon PROMO10
('PAY-004','ORD-105','2023-11-20 16:50', 195000,'Lunas'),
('PAY-005','ORD-106','2023-12-01 08:35', 292000,'Lunas'),   -- setelah diskon SALE20
('PAY-006','ORD-107','2023-12-05 13:25', 115000,'Lunas'),
('PAY-007','ORD-108','2023-12-10 11:00', 200000,'Lunas'),
('PAY-008','ORD-109','2023-12-12 00:05', 221200,'Lunas'),   -- setelah diskon HARBOL30
('PAY-009','ORD-110','2023-12-15 15:35', 175000,'Lunas'),
('PAY-010','ORD-111','2024-01-05 09:05',  94050,'Lunas'),   -- setelah diskon FREEONG
('PAY-011','ORD-112','2024-01-10 11:35', 260000,'Lunas'),
('PAY-012','ORD-113','2024-01-15 14:05', 145000,'Lunas'),
('PAY-013','ORD-114','2024-01-20 16:05', 120000,'Lunas'),
('PAY-014','ORD-118','2024-02-10 15:35', 165750,'Menunggu');-- setelah diskon NEWUSER

-- 14. BANK_TRANSFER (subtipe)
INSERT INTO BANK_TRANSFER (ReferenceNo, Order_ID, BnkName, Sender_AccNo) VALUES
('PAY-001','ORD-101','BCA',    '1234567890'),
('PAY-003','ORD-104','Mandiri','9876543210'),
('PAY-004','ORD-105','BNI',    '1122334455'),
('PAY-006','ORD-107','BRI',    '5566778899'),
('PAY-007','ORD-108','BCA',    '6677889900'),
('PAY-009','ORD-110','Mandiri','7788990011'),
('PAY-011','ORD-112','BNI',    '8899001122'),
('PAY-012','ORD-113','BCA',    '9900112233'),
('PAY-013','ORD-114','BRI',    '0011223344');

-- 15. E_WALLET (subtipe)
INSERT INTO E_WALLET (ReferenceNo, Order_ID, Provider_Name, Acc_Phone) VALUES
('PAY-002','ORD-102','GoPay',  '085711223344'),
('PAY-005','ORD-106','OVO',    '084455667788'),
('PAY-008','ORD-109','Dana',   '081234567890'),
('PAY-010','ORD-111','ShopeePay','083344556677'),
('PAY-014','ORD-118','GoPay',  '087788990011');
GO

-- ============================================================
-- BAGIAN 4: VIEW
-- ============================================================

-- View 1: Ringkasan lengkap setiap pesanan
CREATE OR ALTER VIEW vw_OrderSummary AS
SELECT
    O.Order_ID,
    O.Order_Date,
    O.Status,
    O.Total_Price,
    C.FullName       AS CustomerName,
    C.Email          AS CustomerEmail,
    O.Promo_Code,
    P.Discount_Pct   AS DiskonPct,
    COUNT(OI.Product_ID) AS JumlahItem,
    SH.TrackingNo,
    SH.Ship_Status
FROM [ORDER] O
JOIN CUSTOMER C  ON O.Customer_ID = C.Customer_ID
LEFT JOIN PROMO P   ON O.Promo_Code   = P.Promo_Code
LEFT JOIN ORDER_ITEM OI ON O.Order_ID = OI.Order_ID
LEFT JOIN SHIPMENT SH   ON O.Order_ID = SH.Order_ID
GROUP BY O.Order_ID, O.Order_Date, O.Status, O.Total_Price,
         C.FullName, C.Email, O.Promo_Code, P.Discount_Pct,
         SH.TrackingNo, SH.Ship_Status;
GO

-- View 2: Produk terlaris berdasarkan total kuantitas terjual
CREATE OR ALTER VIEW vw_ProductSales AS
SELECT
    P.Product_ID,
    P.PName        AS NamaProduk,
    S.StoreName    AS NamaToko,
    CAT.CName      AS Kategori,
    P.Price        AS Harga,
    P.Stock        AS StokSisa,
    SUM(OI.Quantity)              AS TotalTerjual,
    SUM(OI.Quantity * OI.Unit_Price) AS TotalPendapatan,
    AVG(CAST(R.Rating AS FLOAT))  AS RataRating,
    COUNT(R.Review_ID)            AS JumlahUlasan
FROM PRODUCT P
JOIN SELLER S    ON P.seller_Id   = S.seller_Id
JOIN CATEGORY CAT ON P.Category_ID = CAT.Category_ID
LEFT JOIN ORDER_ITEM OI ON P.Product_ID = OI.Product_ID
LEFT JOIN [ORDER] O     ON OI.Order_ID  = O.Order_ID AND O.Status <> 'Dibatalkan'
LEFT JOIN REVIEW R      ON P.Product_ID = R.Product_ID
GROUP BY P.Product_ID, P.PName, S.StoreName, CAT.CName, P.Price, P.Stock;
GO

-- View 3: Rekap penjualan per toko
CREATE OR ALTER VIEW vw_SellerRevenue AS
SELECT
    S.seller_Id,
    S.StoreName,
    S.email,
    COUNT(DISTINCT O.Order_ID)   AS TotalOrder,
    SUM(OI.Quantity)             AS TotalItemTerjual,
    SUM(OI.Quantity * OI.Unit_Price) AS TotalPendapatan
FROM SELLER S
LEFT JOIN PRODUCT P  ON S.seller_Id  = P.seller_Id
LEFT JOIN ORDER_ITEM OI ON P.Product_ID = OI.Product_ID
LEFT JOIN [ORDER] O     ON OI.Order_ID  = O.Order_ID AND O.Status <> 'Dibatalkan'
GROUP BY S.seller_Id, S.StoreName, S.email;
GO

-- View 4: Status pengiriman lengkap
CREATE OR ALTER VIEW vw_ShipmentDetail AS
SELECT
    SH.TrackingNo,
    SH.Courier,
    SH.Ship_Date,
    SH.Ship_Status,
    SH.Deliv_Address,
    O.Order_ID,
    O.Order_Date,
    O.Status        AS OrderStatus,
    C.FullName      AS CustomerName,
    C.Email         AS CustomerEmail
FROM SHIPMENT SH
JOIN [ORDER] O   ON SH.Order_ID   = O.Order_ID
JOIN CUSTOMER C  ON O.Customer_ID  = C.Customer_ID;
GO

-- View 5: Laporan pembayaran gabungan (semua metode)
CREATE OR ALTER VIEW vw_PaymentReport AS
SELECT
    PAY.ReferenceNo,
    PAY.Order_ID,
    PAY.Payment_Date,
    PAY.Amount,
    PAY.Payment_Status,
    CASE
        WHEN BT.ReferenceNo IS NOT NULL THEN 'Transfer Bank'
        WHEN EW.ReferenceNo IS NOT NULL THEN 'E-Wallet'
        ELSE 'Tidak Diketahui'
    END AS MetodePembayaran,
    ISNULL(BT.BnkName,      EW.Provider_Name) AS PenyediaLayanan,
    ISNULL(BT.Sender_AccNo, EW.Acc_Phone)     AS NomorAkun
FROM PAYMENT PAY
LEFT JOIN BANK_TRANSFER BT ON PAY.ReferenceNo = BT.ReferenceNo AND PAY.Order_ID = BT.Order_ID
LEFT JOIN E_WALLET EW      ON PAY.ReferenceNo = EW.ReferenceNo AND PAY.Order_ID = EW.Order_ID;
GO

-- ============================================================
-- BAGIAN 5: STORED PROCEDURE
-- ============================================================

-- SP 1: Buat pesanan baru + item (simulasi front-end checkout)
CREATE OR ALTER PROCEDURE sp_CreateOrder
    @Order_ID    VARCHAR(10),
    @Customer_ID VARCHAR(10),
    @Promo_Code  VARCHAR(20) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            INSERT INTO [ORDER] (Order_ID, Order_Date, Status, Customer_ID, Promo_Code)
            VALUES (@Order_ID, GETDATE(), 'Menunggu Pembayaran', @Customer_ID, @Promo_Code);
        COMMIT TRANSACTION;
        SELECT 'Pesanan berhasil dibuat' AS Pesan, @Order_ID AS Order_ID;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT ERROR_MESSAGE() AS Pesan;
    END CATCH
END;
GO

-- SP 2: Tambah item ke pesanan
CREATE OR ALTER PROCEDURE sp_AddOrderItem
    @Order_ID   VARCHAR(10),
    @Product_ID VARCHAR(10),
    @Quantity   INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            DECLARE @Stock INT, @Price DECIMAL(15,2);
            SELECT @Stock = Stock, @Price = Price
            FROM PRODUCT WHERE Product_ID = @Product_ID;

            IF @Stock < @Quantity
            BEGIN
                RAISERROR('Stok tidak mencukupi!', 16, 1);
                ROLLBACK; RETURN;
            END

            INSERT INTO ORDER_ITEM (Order_ID, Product_ID, Quantity, Unit_Price)
            VALUES (@Order_ID, @Product_ID, @Quantity, @Price);
        COMMIT TRANSACTION;
        SELECT 'Item berhasil ditambahkan' AS Pesan;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT ERROR_MESSAGE() AS Pesan;
    END CATCH
END;
GO

-- SP 3: Simulasi pembayaran (transfer bank)
CREATE OR ALTER PROCEDURE sp_PayWithBank
    @ReferenceNo VARCHAR(20),
    @Order_ID    VARCHAR(10),
    @BankName    VARCHAR(50),
    @AccNo       VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            DECLARE @Total DECIMAL(15,2);
            SELECT @Total = Total_Price FROM [ORDER] WHERE Order_ID = @Order_ID;

            INSERT INTO PAYMENT (ReferenceNo, Order_ID, Payment_Date, Amount, Payment_Status)
            VALUES (@ReferenceNo, @Order_ID, GETDATE(), @Total, 'Lunas');

            INSERT INTO BANK_TRANSFER (ReferenceNo, Order_ID, BnkName, Sender_AccNo)
            VALUES (@ReferenceNo, @Order_ID, @BankName, @AccNo);

            UPDATE [ORDER] SET Status = 'Menunggu Konfirmasi'
            WHERE Order_ID = @Order_ID;
        COMMIT TRANSACTION;
        SELECT 'Pembayaran berhasil' AS Pesan, @ReferenceNo AS ReferenceNo;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT ERROR_MESSAGE() AS Pesan;
    END CATCH
END;
GO

-- SP 4: Simulasi Top Up Saldo
CREATE OR ALTER PROCEDURE sp_TopUpBalance
    @Customer_ID VARCHAR(10),
    @Amount      DECIMAL(15,2)
AS
BEGIN
    SET NOCOUNT ON;
    IF @Amount <= 0
    BEGIN
        SELECT 'Jumlah topup harus lebih dari 0' AS Pesan; RETURN;
    END
    UPDATE CUSTOMER SET Balance = Balance + @Amount
    WHERE Customer_ID = @Customer_ID;
    SELECT 'Topup berhasil' AS Pesan,
           Balance AS SaldoBaru
    FROM CUSTOMER WHERE Customer_ID = @Customer_ID;
END;
GO

-- SP 5: Cari produk berdasarkan kata kunci dan kategori
CREATE OR ALTER PROCEDURE sp_SearchProduct
    @Keyword     VARCHAR(100) = NULL,
    @Category_ID VARCHAR(10)  = NULL,
    @MinPrice    DECIMAL(15,2)= NULL,
    @MaxPrice    DECIMAL(15,2)= NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        P.Product_ID,
        P.PName,
        P.Price,
        P.Stock,
        CAT.CName AS Kategori,
        S.StoreName AS Toko,
        AVG(CAST(R.Rating AS FLOAT)) AS RataRating,
        ISNULL(SUM(OD.Qty),0) AS TotalTerjual

    FROM PRODUCT P

    JOIN CATEGORY CAT
        ON P.Category_ID = CAT.Category_ID

    JOIN SELLER S
        ON P.Seller_ID = S.Seller_ID

    LEFT JOIN REVIEW R
        ON P.Product_ID = R.Product_ID

    LEFT JOIN ORDER_DETAIL OD
        ON P.Product_ID = OD.Product_ID

    WHERE
        (@Keyword IS NULL OR P.PName LIKE '%' + @Keyword + '%')
        AND (@Category_ID IS NULL OR P.Category_ID = @Category_ID)
        AND (@MinPrice IS NULL OR P.Price >= @MinPrice)
        AND (@MaxPrice IS NULL OR P.Price <= @MaxPrice)

    GROUP BY
        P.Product_ID,
        P.PName,
        P.Price,
        P.Stock,
        CAT.CName,
        S.StoreName

    ORDER BY TotalTerjual DESC;
END;
GO

-- ============================================================
-- BAGIAN 6: USER DEFINED FUNCTION
-- ============================================================

-- UDF 1: Hitung total belanja satu customer
CREATE OR ALTER FUNCTION fn_CustomerTotal (@Customer_ID VARCHAR(10))
RETURNS DECIMAL(15,2)
AS
BEGIN
    DECLARE @Total DECIMAL(15,2);
    SELECT @Total = SUM(O.Total_Price)
    FROM [ORDER] O
    WHERE O.Customer_ID = @Customer_ID
      AND O.Status = 'Selesai';
    RETURN ISNULL(@Total, 0);
END;
GO

-- UDF 2: Hitung diskon berdasarkan kode promo
CREATE OR ALTER FUNCTION fn_ApplyPromo
    (@Promo_Code VARCHAR(20), @Amount DECIMAL(15,2))
RETURNS DECIMAL(15,2)
AS
BEGIN
    DECLARE @Disc DECIMAL(5,2), @Min DECIMAL(15,2), @Result DECIMAL(15,2);
    SELECT @Disc = Discount_Pct, @Min = Min_Purchase
    FROM PROMO
    WHERE Promo_Code = @Promo_Code AND Is_Active = 1
      AND GETDATE() BETWEEN Valid_From AND Valid_Until;

    IF @Disc IS NULL OR @Amount < @Min
        SET @Result = @Amount;
    ELSE
        SET @Result = @Amount - (@Amount * @Disc / 100);
    RETURN @Result;
END;
GO

-- UDF 3: Hitung rata-rata rating sebuah produk
CREATE OR ALTER FUNCTION fn_ProductRating (@Product_ID VARCHAR(10))
RETURNS DECIMAL(3,2)
AS
BEGIN
    DECLARE @Avg DECIMAL(3,2);
    SELECT @Avg = AVG(CAST(Rating AS FLOAT))
    FROM REVIEW WHERE Product_ID = @Product_ID;
    RETURN ISNULL(@Avg, 0);
END;
GO

-- ============================================================
-- BAGIAN 7: QUERY LANJUT (Soal Nomor 2)
-- ============================================================

-- Query 2a: 5 produk dengan penjualan tertinggi per toko (merchant)
WITH RankedProduct AS (
    SELECT
        S.seller_Id,
        S.StoreName AS NamaToko,
        P.Product_ID,
        P.PName     AS NamaProduk,
        SUM(OI.Quantity) AS TotalTerjual,
        RANK() OVER (PARTITION BY S.seller_Id ORDER BY SUM(OI.Quantity) DESC) AS Peringkat
    FROM SELLER S
    JOIN PRODUCT P     ON S.seller_Id   = P.seller_Id
    JOIN ORDER_ITEM OI ON P.Product_ID  = OI.Product_ID
    JOIN [ORDER] O     ON OI.Order_ID   = O.Order_ID
    WHERE O.Status <> 'Dibatalkan'
    GROUP BY S.seller_Id, S.StoreName, P.Product_ID, P.PName
)
SELECT NamaToko, NamaProduk, TotalTerjual, Peringkat
FROM RankedProduct
WHERE Peringkat <= 5
ORDER BY NamaToko, Peringkat;
GO

-- Query 2b: 5 toko dengan penjualan tertinggi dalam 3 bulan terakhir
SELECT TOP 5
    S.seller_Id,
    S.StoreName                          AS NamaToko,
    SUM(OI.Quantity * OI.Unit_Price)     AS TotalPendapatan,
    SUM(OI.Quantity)                     AS TotalItemTerjual,
    COUNT(DISTINCT O.Order_ID)           AS TotalTransaksi
FROM SELLER S
JOIN PRODUCT P     ON S.seller_Id   = P.seller_Id
JOIN ORDER_ITEM OI ON P.Product_ID  = OI.Product_ID
JOIN [ORDER] O     ON OI.Order_ID   = O.Order_ID
WHERE O.Status <> 'Dibatalkan'
  AND O.Order_Date >= DATEADD(MONTH, -3, GETDATE())
GROUP BY S.seller_Id, S.StoreName
ORDER BY TotalPendapatan DESC;
GO

-- Query 2c: 3 produk yang paling sering dibeli bersamaan dengan 'Kaos Polos Hitam' (P001)
SELECT TOP 3
    P.Product_ID,
    P.PName    AS NamaProduk,
    COUNT(*)   AS FrekuensiBersamaan
FROM ORDER_ITEM OI1
JOIN ORDER_ITEM OI2 ON OI1.Order_ID   = OI2.Order_ID
                    AND OI2.Product_ID <> 'P001'
JOIN PRODUCT P      ON OI2.Product_ID  = P.Product_ID
WHERE OI1.Product_ID = 'P001'
GROUP BY P.Product_ID, P.PName
ORDER BY FrekuensiBersamaan DESC;
GO

-- Query tambahan: Laporan bulanan pendapatan (grouping by month-year)
SELECT
    YEAR(O.Order_Date)  AS Tahun,
    MONTH(O.Order_Date) AS Bulan,
    COUNT(DISTINCT O.Order_ID)       AS JumlahTransaksi,
    SUM(OI.Quantity * OI.Unit_Price) AS TotalPendapatan
FROM [ORDER] O
JOIN ORDER_ITEM OI ON O.Order_ID = OI.Order_ID
WHERE O.Status <> 'Dibatalkan'
GROUP BY YEAR(O.Order_Date), MONTH(O.Order_Date)
ORDER BY Tahun, Bulan;
GO

-- Query tambahan: Customer dengan total belanja tertinggi (subquery)
SELECT
    C.Customer_ID,
    C.FullName,
    C.Email,
    (SELECT SUM(O2.Total_Price)
     FROM [ORDER] O2
     WHERE O2.Customer_ID = C.Customer_ID
       AND O2.Status = 'Selesai') AS TotalBelanja
FROM CUSTOMER C
WHERE (SELECT COUNT(*) FROM [ORDER] O3
       WHERE O3.Customer_ID = C.Customer_ID) > 0
ORDER BY TotalBelanja DESC;
GO

-- Query tambahan: JOIN 4 tabel - Detail lengkap produk termasuk subtipe
SELECT
    P.Product_ID,
    P.PName,
    P.Price,
    P.Stock,
    CAT.CName AS Kategori,
    CASE
        WHEN CL.Product_ID IS NOT NULL THEN 'Pakaian'
        WHEN AC.Product_ID IS NOT NULL THEN 'Aksesoris'
        ELSE '-'
    END AS JenisProduk,
    ISNULL(CL.Size,        '-')  AS Ukuran,
    ISNULL(CL.Gender_Category, '-') AS Gender,
    ISNULL(AC.Type,        '-')  AS TipeAksesoris,
    ISNULL(CAST(CL.Material AS VARCHAR), CAST(AC.Material AS VARCHAR)) AS Material
FROM PRODUCT P
JOIN CATEGORY CAT   ON P.Category_ID = CAT.Category_ID
LEFT JOIN CLOTHING  CL ON P.Product_ID = CL.Product_ID
LEFT JOIN ACCESSORY AC ON P.Product_ID = AC.Product_ID;
GO

-- ============================================================
-- BAGIAN 8: SIMULASI TRANSAKSI LENGKAP (Transaction)
-- ============================================================

-- Contoh simulasi: customer C001 membuat pesanan baru
BEGIN TRY
    BEGIN TRANSACTION;

        -- 1. Buat pesanan
        INSERT INTO [ORDER] (Order_ID, Order_Date, Status, Customer_ID, Promo_Code)
        VALUES ('ORD-201', GETDATE(), 'Menunggu Pembayaran', 'C001', 'PROMO10');

        -- 2. Tambah item ke pesanan
        INSERT INTO ORDER_ITEM (Order_ID, Product_ID, Quantity, Unit_Price)
        VALUES ('ORD-201', 'P005', 1, 90000);

        INSERT INTO ORDER_ITEM (Order_ID, Product_ID, Quantity, Unit_Price)
        VALUES ('ORD-201', 'P014', 2, 55000);

        -- 3. Simulasi pembayaran e-wallet
        DECLARE @Total DECIMAL(15,2);
        SELECT @Total = Total_Price FROM [ORDER] WHERE Order_ID = 'ORD-201';
        DECLARE @FinalAmount DECIMAL(15,2) = dbo.fn_ApplyPromo('PROMO10', @Total);

        INSERT INTO PAYMENT (ReferenceNo, Order_ID, Payment_Date, Amount, Payment_Status)
        VALUES ('PAY-201', 'ORD-201', GETDATE(), @FinalAmount, 'Lunas');

        INSERT INTO E_WALLET (ReferenceNo, Order_ID, Provider_Name, Acc_Phone)
        VALUES ('PAY-201', 'ORD-201', 'Dana', '081234567890');

        -- 4. Update status pesanan
        UPDATE [ORDER] SET Status = 'Menunggu Konfirmasi'
        WHERE Order_ID = 'ORD-201';

    COMMIT TRANSACTION;
    PRINT 'Transaksi simulasi berhasil.';
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT 'Transaksi dibatalkan: ' + ERROR_MESSAGE();
END CATCH;
GO
